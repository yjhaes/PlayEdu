import client from "./internal/httpClient";

// 线上课详情
export function detail(id: number) {
  return client.get(`/api/v1/course/${id}`, {});
}

// 线上课课时详情
export function play(courseId: number, id: number) {
  return client.get(`/api/v1/course/${courseId}/hour/${id}`, {});
}

// 获取播放地址
export function playUrl(courseId: number, hourId: number) {
  return client.get(`/api/v1/course/${courseId}/hour/${hourId}/play`, {});
}

//观看ping
export function playPing(
  courseId: number,
  hourId: number,
  sessionId?: string
) {
  return client.post(`/api/v1/course/${courseId}/hour/${hourId}/ping`, {
    ...(sessionId ? { session_id: sessionId } : {}),
  });
}

// 主动停止学习
export function stopPing(courseId: number, hourId: number, sessionId: string) {
  return client.request({
    method: "DELETE",
    url: `/api/v1/course/${courseId}/hour/${hourId}/ping`,
    data: { session_id: sessionId },
  });
}

// 页面卸载时尽力释放学习租约
export function stopPingBestEffort(
  courseId: number,
  hourId: number,
  sessionId: string
) {
  return client.keepalive(`/api/v1/course/${courseId}/hour/${hourId}/ping`, {
    session_id: sessionId,
  });
}

//最近学习课程
export function latestLearn() {
  return client.get(`/api/v1/user/latest-learn`, {});
}

//下载课件
export function downloadAttachment(courseId: number, id: number) {
  return client.get(`/api/v1/course/${courseId}/attach/${id}/download`, {});
}
