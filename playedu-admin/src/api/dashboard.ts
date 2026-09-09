import client from "./internal/httpClient";

export function dashboardList() {
  return client.get("/backend/v1/dashboard/index", {});
}

export function rebuildLearningRanking() {
  return client.post("/backend/v1/dashboard/learning-ranking/rebuild", {});
}
