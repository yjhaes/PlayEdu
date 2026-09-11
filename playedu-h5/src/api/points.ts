import client from "./internal/httpClient";

export type PointLedgerType =
  | "COURSE_COMPLETION"
  | "HISTORICAL_COURSE_COMPLETION"
  | "REDEMPTION"
  | "MANUAL_ADJUSTMENT";

export type PointProductStatus = "ON_SALE" | "OFF_SALE";

export interface PointsApiResponse<T> {
  code: number;
  msg: string;
  data: T;
}

export interface PointsPage<T> {
  data: T[];
  total: number;
}

export interface HistoricalRewardSummary {
  user_id: number;
  completion_count: number;
  points_awarded: number;
}

export interface PointsSummary {
  credit1: number;
  historical_reward_summary: HistoricalRewardSummary | null;
  historical_reward_summary_pending: boolean;
}

export interface PointLedger {
  id: number;
  delta: number;
  balance_after: number;
  type: PointLedgerType;
  reason?: string | null;
  created_at: string;
}

export interface PointProduct {
  id: number;
  name: string;
  points_price: number;
  status: PointProductStatus;
  available_count: number;
  delivered_count?: number;
  created_at?: string;
  updated_at?: string;
}

export interface PointRedemption {
  id: number;
  user_id: number;
  product_id: number;
  code_id: number;
  points_cost: number;
  created_at: string;
}

export interface DeliveredRedemption {
  redemption: PointRedemption;
  code: string;
}

export interface PointsRules {
  course_completion_reward_points: number;
  points_never_expire: boolean;
  redemption_cancelable: boolean;
  invalid_code_after_delivery_support: boolean;
  manual_deduction_can_create_negative_balance: boolean;
  descriptions: string[];
}

export function summary() {
  return client.get<PointsApiResponse<PointsSummary>>(
    "/api/v1/points/summary",
    {}
  );
}

export function acknowledgeHistoricalReward() {
  return client.post<PointsApiResponse<{ acknowledged: boolean }>>(
    "/api/v1/points/historical-reward-summary/acknowledge",
    {}
  );
}

export function ledgers(page: number, size: number) {
  return client.get<PointsApiResponse<PointsPage<PointLedger>>>(
    "/api/v1/points/ledgers/index",
    { page, size }
  );
}

export function products(page: number, size: number) {
  return client.get<PointsApiResponse<PointsPage<PointProduct>>>(
    "/api/v1/points/products/index",
    { page, size }
  );
}

export function redeem(productId: number, requestKey: string) {
  return client.post<PointsApiResponse<DeliveredRedemption>>(
    `/api/v1/points/products/${productId}/redeem`,
    { request_key: requestKey },
    {
      headers: {
        "Idempotency-Key": requestKey,
      },
    }
  );
}

export function redemptions(page: number, size: number) {
  return client.get<PointsApiResponse<PointsPage<PointRedemption>>>(
    "/api/v1/points/redemptions/index",
    { page, size }
  );
}

export function redemptionDetail(id: number) {
  return client.get<PointsApiResponse<DeliveredRedemption>>(
    `/api/v1/points/redemptions/${id}`,
    {}
  );
}

export function rules() {
  return client.get<PointsApiResponse<PointsRules>>("/api/v1/points/rules", {});
}
