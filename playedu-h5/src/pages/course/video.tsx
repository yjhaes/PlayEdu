import { useEffect, useRef, useState } from "react";
import styles from "./video.module.scss";
import { useParams, useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { course as Course } from "../../api/index";
import { Toast, Image } from "antd-mobile";
import backIcon from "../../assets/images/commen/icon-back-n.png";
import { Empty } from "../../components";
import { HourCompenent } from "./compenents/videoHour";
import {
  LearningLeaseController,
  type LearningHeartbeatData,
} from "../../utils/learningLease";

interface DPlayerInstance {
  video: HTMLVideoElement;
  on: (event: string, handler: () => void) => void;
  seek: (seconds: number) => void;
  destroy: () => void;
}

interface DPlayerWindow extends Window {
  DPlayer: new (options: Record<string, unknown>) => DPlayerInstance;
  player?: DPlayerInstance | null;
}

declare const window: DPlayerWindow;

type LastSeePosition = {
  time: number;
  pos: number;
};

type LocalUserLearnHourRecordModel = {
  [key: number]: UserLearnHourRecordModel;
};

type LocalCourseHour = {
  [key: number]: CourseHourModel[];
};

const LEARNING_CONFLICT_MESSAGE =
  "当前已有其他课时正在学习，请先暂停或结束其他视频";
const LEARNING_SESSION_MESSAGE = "学习会话已失效，请点击播放重试";
const LEARNING_UNAVAILABLE_MESSAGE =
  "学习服务暂时不可用，请检查网络后点击重试";

const CoursePlayPage = () => {
  const navigate = useNavigate();
  const params = useParams();
  const systemConfig = useSelector((state: any) => state.systemConfig.value);
  const user = useSelector((state: any) => state.loginUser.value.user);
  const courseId = Number(params.courseId);
  const hourId = Number(params.hourId);
  const routeKey = `${courseId}-${hourId}`;
  const [playUrl, setPlayUrl] = useState("");
  const [playendedStatus, setPlayendedStatus] = useState(false);
  const [course, setCourse] = useState<CourseModel | null>(null);
  const [hour, setHour] = useState<any>({});
  const [isLastpage, setIsLastpage] = useState(false);
  const [totalHours, setTotalHours] = useState<any>([]);
  const [playingTime, setPlayingTime] = useState(0);
  const [watchedSeconds, setWatchedSeconds] = useState(0);
  const [chapters, setChapters] = useState<ChapterModel[]>([]);
  const [hours, setHours] = useState<LocalCourseHour | null>(null);
  const [learnHourRecord, setLearnHourRecord] =
    useState<LocalUserLearnHourRecordModel>({});
  const [leaseMessage, setLeaseMessage] = useState("");
  const playRef = useRef(0);
  const watchRef = useRef(0);
  const totalRef = useRef(0);
  const playerRef = useRef<DPlayerInstance | null>(null);
  const routeKeyRef = useRef(routeKey);
  const leaseControllerRef = useRef<LearningLeaseController | null>(null);
  const suppressPauseStopRef = useRef(false);

  routeKeyRef.current = routeKey;

  const destroyPlayer = () => {
    const player = playerRef.current;
    if (!player) {
      return;
    }
    player.destroy();
    if (window.player === player) {
      window.player = null;
    }
    playerRef.current = null;
    suppressPauseStopRef.current = false;
  };

  const pauseForLeaseIssue = () => {
    const player = playerRef.current;
    if (player?.video && !player.video.paused) {
      suppressPauseStopRef.current = true;
      player.video.pause();
    } else {
      suppressPauseStopRef.current = false;
    }
  };

  const retryPlayback = () => {
    const player = playerRef.current;
    if (!player?.video) {
      return;
    }
    setLeaseMessage("");
    const playPromise = player.video.play();
    if (playPromise && typeof playPromise.catch === "function") {
      void playPromise.catch(() => undefined);
    }
  };

  useEffect(() => {
    const controller = new LearningLeaseController(
      {
        heartbeat: async (sessionId?: string) => {
          const response = (await Course.playPing(
            courseId,
            hourId,
            sessionId
          )) as { data: LearningHeartbeatData };
          return response.data;
        },
        stop: (sessionId: string) =>
          Course.stopPing(courseId, hourId, sessionId),
        stopBestEffort: (sessionId: string) =>
          Course.stopPingBestEffort(courseId, hourId, sessionId),
      },
      {
        onStatusChange: (status) => {
          if (status === "acquiring") {
            setLeaseMessage("");
          }
        },
        onConflict: () => {
          setLeaseMessage(LEARNING_CONFLICT_MESSAGE);
          pauseForLeaseIssue();
        },
        onInvalidSession: () => {
          setLeaseMessage(LEARNING_SESSION_MESSAGE);
          pauseForLeaseIssue();
        },
        onUnavailable: () => {
          setLeaseMessage(LEARNING_UNAVAILABLE_MESSAGE);
          pauseForLeaseIssue();
        },
      }
    );
    leaseControllerRef.current = controller;

    const releaseOnPageHide = () => controller.dispose();
    window.addEventListener("pagehide", releaseOnPageHide);

    return () => {
      window.removeEventListener("pagehide", releaseOnPageHide);
      controller.dispose();
      if (leaseControllerRef.current === controller) {
        leaseControllerRef.current = null;
      }
    };
  }, [courseId, hourId]);

  useEffect(() => {
    setPlayendedStatus(false);
    setLeaseMessage("");
    setPlayingTime(0);
    setWatchedSeconds(0);
    getCourse();
    getDetail();

    return () => {
      destroyPlayer();
    };
  }, [routeKey]);

  useEffect(() => {
    playRef.current = playingTime;
  }, [playingTime]);

  useEffect(() => {
    watchRef.current = watchedSeconds;
  }, [watchedSeconds]);

  useEffect(() => {
    totalRef.current = hour.duration || 0;
  }, [hour]);

  const getCourse = () => {
    Course.detail(courseId).then((res: any) => {
      if (routeKeyRef.current !== routeKey) {
        return;
      }
      setChapters(res.data.chapters);
      setHours(res.data.hours);
      if (res.data.learn_hour_records) {
        setLearnHourRecord(res.data.learn_hour_records);
      }
      let totalHours: any = [];
      if (res.data.chapters.length === 0) {
        setTotalHours(res.data.hours[0]);
        totalHours = res.data.hours[0];
      } else if (res.data.chapters.length > 0) {
        const arr: any = [];
        for (let key in res.data.hours) {
          res.data.hours[key].map((item: CourseHourModel) => {
            arr.push(item);
          });
        }
        setTotalHours(arr);
        totalHours = arr;
      }
      const index = totalHours.findIndex((i: CourseHourModel) => i.id === hourId);
      setIsLastpage(index === totalHours.length - 1);
    });
  };

  const getDetail = () => {
    Course.play(courseId, hourId)
      .then((res: any) => {
        if (routeKeyRef.current !== routeKey) {
          return;
        }
        const courseItem: CourseModel = res.data.course;
        setCourse(courseItem);
        setHour(res.data.hour);
        document.title = res.data.hour.title;
        const record = res.data.user_hour_record;
        let params: LastSeePosition | null = null;
        if (record && record.finished_duration && record.is_finished === 0) {
          params = {
            time: 5,
            pos: record.finished_duration,
          };
          setWatchedSeconds(record.finished_duration);
        } else if (record && record.is_finished === 1) {
          setWatchedSeconds(res.data.hour.duration);
        }
        getVideoUrl(res.data.hour.rid, params);
      })
      .catch(() => undefined);
  };

  const getVideoUrl = (rid: number, data: LastSeePosition | null) => {
    Course.playUrl(courseId, hourId).then((res: any) => {
      if (routeKeyRef.current !== routeKey) {
        return;
      }
      destroyPlayer();
      setPlayUrl(res.data.resource_url[rid]);
      initDPlayer(res.data.resource_url[rid], 0, data);
    });
  };

  const initDPlayer = (
    url: string,
    isTrySee: number,
    params: LastSeePosition | null
  ) => {
    const player = new window.DPlayer({
      container: document.getElementById("meedu-player-container"),
      autoplay: false,
      video: {
        url,
        pic: systemConfig.playerPoster,
      },
      try: isTrySee === 1,
      bulletSecret: {
        enabled: systemConfig.playerIsEnabledBulletSecret,
        text: systemConfig.playerBulletSecretText
          .replace("{name}", user.name)
          .replace("{email}", user.email)
          .replace("{idCard}", user.id_card),
        size: "14px",
        color: systemConfig.playerBulletSecretColor || "red",
        opacity: Number(systemConfig.playerBulletSecretOpacity),
      },
      ban_drag:
        systemConfig.playerIsDisabledDrag &&
        watchRef.current < totalRef.current &&
        watchRef.current === 0,
      last_see_pos: params,
    });
    playerRef.current = player;
    window.player = player;

    const isCurrentPlayer = () =>
      routeKeyRef.current === routeKey && playerRef.current === player;

    player.on("play", () => {
      if (!isCurrentPlayer()) {
        return;
      }
      void leaseControllerRef.current?.start();
    });

    player.on("pause", () => {
      if (!isCurrentPlayer()) {
        return;
      }
      if (suppressPauseStopRef.current) {
        suppressPauseStopRef.current = false;
        return;
      }
      void leaseControllerRef.current?.stop();
    });

    player.on("timeupdate", () => {
      if (!isCurrentPlayer()) {
        return;
      }
      const currentTime = Math.trunc(player.video.currentTime);
      if (
        systemConfig.playerIsDisabledDrag &&
        watchRef.current < totalRef.current &&
        currentTime - playRef.current >= 2 &&
        currentTime > watchRef.current
      ) {
        Toast.show("首次学习禁止快进");
        player.seek(watchRef.current);
      } else {
        setPlayingTime(currentTime);
      }
    });

    player.on("ended", () => {
      if (!isCurrentPlayer()) {
        return;
      }
      if (
        systemConfig.playerIsDisabledDrag &&
        watchRef.current < totalRef.current &&
        player.video.duration - playRef.current >= 2
      ) {
        player.seek(playRef.current);
        return;
      }
      setPlayendedStatus(true);
      setPlayingTime(0);
      void leaseControllerRef.current?.stop();
      exitFullscreen();
      destroyPlayer();
    });
  };

  const goNextVideo = () => {
    const index = totalHours.findIndex((i: CourseHourModel) => i.id === hourId);
    if (index === totalHours.length - 1) {
      setIsLastpage(true);
      Toast.show("已经是最后一节了！");
    } else if (index < totalHours.length - 1) {
      const release = leaseControllerRef.current?.stop() ?? Promise.resolve();
      destroyPlayer();
      setIsLastpage(false);
      void release.then(() => {
        if (routeKeyRef.current !== routeKey) {
          return;
        }
        navigate(`/course/${courseId}/hour/${totalHours[index + 1].id}`, {
          replace: true,
        });
      });
    }
  };

  const playVideo = (cid: number, id: number) => {
    const release = leaseControllerRef.current?.stop() ?? Promise.resolve();
    destroyPlayer();
    void release.then(() => {
      if (routeKeyRef.current === routeKey) {
        navigate(`/course/${cid}/hour/${id}`, { replace: true });
      }
    });
  };

  const leavePage = () => {
    const release = leaseControllerRef.current?.stop() ?? Promise.resolve();
    destroyPlayer();
    void release.then(() => {
      if (routeKeyRef.current === routeKey) {
        navigate(-1);
      }
    });
  };

  const exitFullscreen = () => {
    let de: any;
    de = document;
    if (de.fullscreenElement !== null) {
      de.exitFullscreen();
    } else if (de.mozCancelFullScreen) {
      de.mozCancelFullScreen();
    } else if (de.webkitCancelFullScreen) {
      de.webkitCancelFullScreen();
    }
  };

  return (
    <div className="main-body">
      <div className={styles["video-body"]}>
        <Image className={styles["back-icon"]} src={backIcon} onClick={leavePage} />
        <div className={styles["video-box"]}>
          <div
            className="play-box"
            style={{ display: playendedStatus ? "none" : "block" }}
            id="meedu-player-container"
          ></div>
          {leaseMessage && (
            <div className={styles["alert-message"]}>
              <div className={styles["des-video"]}>{leaseMessage}</div>
              <div className={styles["alert-button"]} onClick={retryPlayback}>
                点击重试
              </div>
            </div>
          )}
          {!leaseMessage && playendedStatus && (
            <div className={styles["alert-message"]}>
              {isLastpage && (
                <div
                  className={styles["alert-button"]}
                  onClick={leavePage}
                >
                  恭喜你学完最后一节
                </div>
              )}
              {!isLastpage && (
                <div className={styles["alert-button"]} onClick={() => {
                  setPlayendedStatus(false);
                  void goNextVideo();
                }}>
                  播放下一节
                </div>
              )}
            </div>
          )}
        </div>
      </div>
      <div className={styles["chapters-hours-cont"]}>
        {chapters.length === 0 && !hours && <Empty />}
        {chapters.length === 0 && hours && (
          <div className={styles["hours-list-box"]} style={{ marginTop: 10 }}>
            {hours[0].map((item: CourseHourModel) => (
              <div key={item.id} className={styles["hours-it"]}>
                <HourCompenent
                  id={item.id}
                  cid={item.course_id}
                  title={item.title}
                  record={learnHourRecord[item.id]}
                  duration={item.duration}
                  vid={hourId}
                  onSuccess={playVideo}
                ></HourCompenent>
              </div>
            ))}
          </div>
        )}
        {chapters.length > 0 && hours && (
          <div className={styles["hours-list-box"]}>
            {chapters.map((item: ChapterModel) => (
              <div key={item.id} className={styles["chapter-it"]}>
                <div className={styles["chapter-name"]}>{item.name}</div>
                {hours[item.id]?.map((it: CourseHourModel) => (
                  <div key={it.id} className={styles["hours-it"]}>
                    <HourCompenent
                      id={it.id}
                      cid={item.course_id}
                      title={it.title}
                      record={learnHourRecord[it.id]}
                      duration={it.duration}
                      vid={hourId}
                      onSuccess={playVideo}
                    ></HourCompenent>
                  </div>
                ))}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default CoursePlayPage;
