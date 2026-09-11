export type LearningLeaseStatus =
  | "idle"
  | "acquiring"
  | "active"
  | "blocked"
  | "unavailable";

export interface LearningHeartbeatData {
  session_id: string;
  added_duration: number;
  earned_points?: number;
  active_course_id: number | null;
  active_hour_id: number | null;
}

export interface LearningLeaseError {
  code?: number;
  msg?: string;
  data?: {
    active_course_id?: number | null;
    active_hour_id?: number | null;
  } | null;
}

interface LearningLeaseClient {
  heartbeat: (sessionId?: string) => Promise<LearningHeartbeatData>;
  stop: (sessionId: string) => Promise<unknown>;
  stopBestEffort: (sessionId: string) => Promise<unknown> | void;
}

interface LearningLeaseCallbacks {
  onStatusChange?: (status: LearningLeaseStatus) => void;
  onEarnedPoints?: (points: number) => void;
  onConflict?: (error: LearningLeaseError) => void;
  onInvalidSession?: (error: LearningLeaseError) => void;
  onUnavailable?: (error: LearningLeaseError) => void;
}

const HEARTBEAT_INTERVAL_MS = 10_000;

/** Coordinates the browser lifecycle with one server-issued learning lease. */
export class LearningLeaseController {
  private sessionId: string | null = null;

  private pendingStopSessionId: string | null = null;

  private heartbeatTimer: ReturnType<typeof setInterval> | null = null;

  private acquisition: Promise<boolean> | null = null;

  private release: Promise<boolean> | null = null;

  private stopping: Promise<void> | null = null;

  private renewing = false;

  private playing = false;

  private disposed = false;

  private version = 0;

  private status: LearningLeaseStatus = "idle";

  constructor(
    private readonly client: LearningLeaseClient,
    private readonly callbacks: LearningLeaseCallbacks = {}
  ) {}

  start(): Promise<boolean> {
    if (this.disposed) {
      return Promise.resolve(false);
    }

    if (this.playing && this.acquisition) {
      return this.acquisition;
    }

    if (this.playing && this.status === "active" && this.sessionId) {
      return Promise.resolve(true);
    }

    this.playing = true;
    const version = ++this.version;
    this.setStatus("acquiring");

    const acquisition = this.acquire(version);
    const trackedAcquisition = acquisition.finally(() => {
      if (this.acquisition === trackedAcquisition) {
        this.acquisition = null;
      }
    });
    this.acquisition = trackedAcquisition;
    return trackedAcquisition;
  }

  stop(): Promise<void> {
    this.playing = false;
    ++this.version;
    this.clearHeartbeatTimer();

    if (this.stopping) {
      return this.stopping;
    }

    const acquisition = this.acquisition;
    this.acquisition = null;

    if (this.sessionId) {
      this.pendingStopSessionId = this.sessionId;
      this.sessionId = null;
    }

    if (!this.pendingStopSessionId && !acquisition) {
      if (!this.disposed) {
        this.setStatus("idle");
      }
      return Promise.resolve();
    }

    if (!this.disposed) {
      this.setStatus("idle");
    }

    const waitForAcquisition = acquisition
      ? acquisition.catch(() => false)
      : Promise.resolve(false);
    const stopping = waitForAcquisition
      .then(() => this.releasePendingStop())
      .then(() => undefined);
    const trackedStopping = stopping.finally(() => {
      if (this.stopping === trackedStopping) {
        this.stopping = null;
      }
    });
    this.stopping = trackedStopping;
    return trackedStopping;
  }

  dispose(): void {
    if (this.disposed) {
      return;
    }

    this.disposed = true;
    this.playing = false;
    ++this.version;
    this.acquisition = null;
    this.clearHeartbeatTimer();

    const sessions = new Set<string>();
    if (this.sessionId) {
      sessions.add(this.sessionId);
    }
    if (this.pendingStopSessionId) {
      sessions.add(this.pendingStopSessionId);
    }
    this.sessionId = null;
    this.pendingStopSessionId = null;

    sessions.forEach((sessionId) => this.stopBestEffort(sessionId));
  }

  private async acquire(version: number): Promise<boolean> {
    if (this.stopping) {
      await this.stopping;
    }
    if (!this.isCurrent(version)) {
      return false;
    }

    const released = await this.releasePendingStop();
    if (!released) {
      if (this.isCurrent(version)) {
        this.playing = false;
        ++this.version;
      }
      return false;
    }
    if (!this.isCurrent(version)) {
      return false;
    }

    try {
      const result = await this.client.heartbeat();
      if (!this.isCurrent(version)) {
        this.stopReturnedSessionBestEffort(result?.session_id);
        return false;
      }

      if (!result || typeof result.session_id !== "string" || !result.session_id) {
        this.fail(version, {}, "unavailable");
        return false;
      }

      this.sessionId = result.session_id;
      this.notifyEarnedPoints(result);
      this.setStatus("active");
      this.startHeartbeatTimer();
      return true;
    } catch (error) {
      if (!this.isCurrent(version)) {
        return false;
      }
      this.fail(version, this.toError(error));
      return false;
    }
  }

  private startHeartbeatTimer(): void {
    this.clearHeartbeatTimer();
    this.heartbeatTimer = setInterval(() => {
      void this.renew();
    }, HEARTBEAT_INTERVAL_MS);
  }

  private async renew(): Promise<void> {
    if (
      this.disposed ||
      !this.playing ||
      this.status !== "active" ||
      !this.sessionId ||
      this.renewing
    ) {
      return;
    }

    this.renewing = true;
    const version = this.version;
    const sessionId = this.sessionId;
    try {
      const result = await this.client.heartbeat(sessionId);
      if (!this.isCurrent(version)) {
        this.stopReturnedSessionBestEffort(result?.session_id || sessionId);
        return;
      }

      if (!result || result.session_id !== sessionId) {
        this.fail(version, {}, "unavailable", sessionId);
      } else {
        this.notifyEarnedPoints(result);
      }
    } catch (error) {
      if (this.isCurrent(version)) {
        this.fail(version, this.toError(error), undefined, sessionId);
      }
    } finally {
      this.renewing = false;
    }
  }

  private fail(
    version: number,
    error: LearningLeaseError,
    forcedKind?: "conflict" | "invalid" | "unavailable",
    sessionId?: string
  ): void {
    if (!this.isCurrent(version)) {
      return;
    }

    const kind = forcedKind || this.classify(error);
    this.playing = false;
    ++this.version;
    this.clearHeartbeatTimer();

    const currentSessionId = sessionId || this.sessionId;
    this.sessionId = null;
    if (kind === "unavailable" && currentSessionId) {
      this.pendingStopSessionId = currentSessionId;
    } else if (currentSessionId === this.pendingStopSessionId) {
      this.pendingStopSessionId = null;
    }

    if (kind === "conflict") {
      this.setStatus("blocked");
      this.callbacks.onConflict?.(error);
    } else if (kind === "invalid") {
      this.setStatus("blocked");
      this.callbacks.onInvalidSession?.(error);
    } else {
      this.setStatus("unavailable");
      this.callbacks.onUnavailable?.(error);
    }
  }

  private releasePendingStop(): Promise<boolean> {
    if (!this.pendingStopSessionId) {
      return Promise.resolve(true);
    }
    if (this.release) {
      return this.release;
    }

    const sessionId = this.pendingStopSessionId;
    const release = this.releaseSession(sessionId);
    const trackedRelease = release.finally(() => {
      if (this.release === trackedRelease) {
        this.release = null;
      }
    });
    this.release = trackedRelease;
    return trackedRelease;
  }

  private async releaseSession(sessionId: string): Promise<boolean> {
    try {
      await this.client.stop(sessionId);
      this.clearPendingStop(sessionId);
      return true;
    } catch (error) {
      const normalizedError = this.toError(error);
      if (this.isSessionGone(normalizedError)) {
        this.clearPendingStop(sessionId);
        return true;
      }

      if (this.disposed) {
        return false;
      }
      this.setStatus("unavailable");
      this.callbacks.onUnavailable?.(normalizedError);
      return false;
    }
  }

  private stopReturnedSessionBestEffort(sessionId?: string): void {
    if (!sessionId) {
      return;
    }
    if (this.disposed) {
      this.stopBestEffort(sessionId);
      return;
    }

    if (!this.pendingStopSessionId) {
      this.pendingStopSessionId = sessionId;
    }
    void this.releasePendingStop();
  }

  private stopBestEffort(sessionId: string): void {
    try {
      const result = this.client.stopBestEffort(sessionId);
      if (result && typeof (result as Promise<unknown>).catch === "function") {
        void (result as Promise<unknown>).catch(() => undefined);
      }
    } catch {
      // The lease TTL remains the final cleanup mechanism.
    }
  }

  private clearPendingStop(sessionId: string): void {
    if (this.pendingStopSessionId === sessionId) {
      this.pendingStopSessionId = null;
    }
  }

  private clearHeartbeatTimer(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
  }

  private isCurrent(version: number): boolean {
    return !this.disposed && this.playing && this.version === version;
  }

  private setStatus(status: LearningLeaseStatus): void {
    this.status = status;
    this.callbacks.onStatusChange?.(status);
  }

  private notifyEarnedPoints(result: LearningHeartbeatData): void {
    if (typeof result.earned_points === "number" && result.earned_points > 0) {
      this.callbacks.onEarnedPoints?.(result.earned_points);
    }
  }

  private classify(error: LearningLeaseError):
    | "conflict"
    | "invalid"
    | "unavailable" {
    if (error.code === 40901) {
      return "conflict";
    }
    if (error.code === 40902) {
      return "invalid";
    }
    return "unavailable";
  }

  private isSessionGone(error: LearningLeaseError): boolean {
    return error.code === 40901 || error.code === 40902;
  }

  private toError(error: unknown): LearningLeaseError {
    if (!error || typeof error !== "object") {
      return {};
    }

    const candidate = error as {
      code?: unknown;
      msg?: unknown;
      data?: unknown;
      response?: { data?: unknown };
    };
    const responseData = candidate.response?.data;
    const payload =
      responseData && typeof responseData === "object"
        ? responseData
        : candidate;
    const value = payload as {
      code?: unknown;
      msg?: unknown;
      data?: LearningLeaseError["data"];
    };

    return {
      code: typeof value.code === "number" ? value.code : Number(value.code),
      msg: typeof value.msg === "string" ? value.msg : undefined,
      data: value.data,
    };
  }
}
