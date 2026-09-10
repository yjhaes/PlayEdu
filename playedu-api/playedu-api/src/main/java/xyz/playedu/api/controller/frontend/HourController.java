/*
 * Copyright (C) 2023 杭州白书科技有限公司
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.playedu.api.controller.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.playedu.api.request.frontend.LearningHeartbeatRequest;
import xyz.playedu.api.request.frontend.LearningStopRequest;
import xyz.playedu.common.context.FCtx;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.common.types.JsonResponse;
import xyz.playedu.course.bus.UserBus;
import xyz.playedu.course.domain.Course;
import xyz.playedu.course.domain.CourseHour;
import xyz.playedu.course.domain.UserCourseHourRecord;
import xyz.playedu.course.service.ActiveLearningLeaseService;
import xyz.playedu.course.service.CourseHourService;
import xyz.playedu.course.service.CourseService;
import xyz.playedu.course.service.UserCourseHourRecordService;
import xyz.playedu.resource.domain.Resource;
import xyz.playedu.resource.service.ResourceService;

/**
 * @Author 杭州白书科技有限公司
 *
 * @create 2023/3/20 14:59
 */
@RestController
@RequestMapping("/api/v1/course/{courseId}/hour")
public class HourController {

    private static final int ACTIVE_LEARNING_CONFLICT_CODE = 40901;

    private static final int INVALID_LEARNING_SESSION_CODE = 40902;

    @Autowired private CourseService courseService;

    @Autowired private CourseHourService hourService;

    @Autowired private ResourceService resourceService;

    @Autowired private UserCourseHourRecordService userCourseHourRecordService;

    @Autowired private UserBus userBus;

    @Autowired private ActiveLearningLeaseService activeLearningLeaseService;

    @GetMapping("/{id}")
    @SneakyThrows
    public JsonResponse detail(
            @PathVariable(name = "courseId") Integer courseId,
            @PathVariable(name = "id") Integer id) {
        Course course = courseService.findOrFail(courseId);
        CourseHour courseHour = hourService.findOrFail(id, courseId);

        UserCourseHourRecord userCourseHourRecord = null;
        if (FCtx.getId() != null && FCtx.getId() > 0) {
            // 学员已登录
            userCourseHourRecord = userCourseHourRecordService.find(FCtx.getId(), courseId, id);
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("course", course);
        data.put("hour", courseHour);
        data.put("user_hour_record", userCourseHourRecord);

        return JsonResponse.data(data);
    }

    @GetMapping("/{id}/play")
    @SneakyThrows
    public JsonResponse play(
            @PathVariable(name = "courseId") Integer courseId,
            @PathVariable(name = "id") Integer id) {
        checkCourseAccess(courseId);
        CourseHour hour = hourService.findOrFail(id, courseId);
        Resource resource = resourceService.findOrFail(hour.getRid());

        HashMap<String, Object> data = new HashMap<>();
        // 获取资源签名url
        data.put(
                "resource_url",
                resourceService.chunksPreSignUrlByIds(
                        new ArrayList<>() {
                            {
                                add(resource.getId());
                            }
                        }));
        data.put("extension", resource.getExtension()); // 视频格式
        data.put("duration", resourceService.duration(resource.getId())); // 视频时长

        return JsonResponse.data(data);
    }

    @PostMapping("/{id}/record")
    @SneakyThrows
    public JsonResponse record(@PathVariable(name = "courseId") Integer courseId) {
        return rejectClientDurationReport(courseId);
    }

    @PostMapping("/{id}/ping")
    @SneakyThrows
    public JsonResponse ping(
            @PathVariable(name = "courseId") Integer courseId,
            @PathVariable(name = "id") Integer id,
            @RequestBody(required = false) LearningHeartbeatRequest request) {
        checkCourseAccess(courseId);
        CourseHour hour = hourService.findOrFail(id, courseId);
        String sessionId = request == null ? null : request.getSessionId();
        ActiveLearningLeaseService.HeartbeatResult result =
                activeLearningLeaseService.heartbeat(
                        FCtx.getId(), courseId, hour.getId(), sessionId, hour.getDuration());
        if (result.outcome() == ActiveLearningLeaseService.Outcome.CONFLICT) {
            return new JsonResponse(
                    ACTIVE_LEARNING_CONFLICT_CODE, "当前存在活跃学习课时", activeLearningData(result));
        }
        if (result.outcome() == ActiveLearningLeaseService.Outcome.INVALID_SESSION) {
            return new JsonResponse(
                    INVALID_LEARNING_SESSION_CODE, "学习会话已失效", activeLearningData(result));
        }
        return JsonResponse.data(activeLearningData(result));
    }

    @DeleteMapping("/{id}/ping")
    @SneakyThrows
    public JsonResponse stopPing(
            @PathVariable(name = "courseId") Integer courseId,
            @PathVariable(name = "id") Integer id,
            @RequestBody @Validated LearningStopRequest request) {
        checkCourseAccess(courseId);
        ActiveLearningLeaseService.Outcome outcome =
                activeLearningLeaseService.stop(FCtx.getId(), courseId, id, request.getSessionId());
        if (outcome == ActiveLearningLeaseService.Outcome.INVALID_SESSION
                || outcome == ActiveLearningLeaseService.Outcome.CONFLICT) {
            return new JsonResponse(INVALID_LEARNING_SESSION_CODE, "学习会话已失效", null);
        }
        return JsonResponse.success();
    }

    @SneakyThrows
    private JsonResponse rejectClientDurationReport(Integer courseId) {
        checkCourseAccess(courseId);
        return JsonResponse.error("请通过学习心跳记录学习时长", 400);
    }

    private void checkCourseAccess(Integer courseId) throws ServiceException {
        if (!userBus.canSeeCourse(FCtx.getId(), courseId)) {
            throw new ServiceException("无权限观看");
        }
    }

    private HashMap<String, Object> activeLearningData(
            ActiveLearningLeaseService.HeartbeatResult result) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("session_id", result.sessionId());
        data.put("added_duration", result.addedDuration());
        data.put("active_course_id", result.activeCourseId());
        data.put("active_hour_id", result.activeHourId());
        if (result.earnedPoints() > 0) {
            data.put("earned_points", result.earnedPoints());
        }
        return data;
    }
}
