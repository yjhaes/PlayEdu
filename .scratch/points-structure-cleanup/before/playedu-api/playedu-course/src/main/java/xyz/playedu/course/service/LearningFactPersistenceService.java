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
package xyz.playedu.course.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.common.redis.RedisDistributedLock;
import xyz.playedu.common.redis.RedisLockException;
import xyz.playedu.course.domain.UserCourseHourRecord;
import xyz.playedu.course.event.DailyLearningDurationEventPublisher;
import xyz.playedu.course.event.DailyLearningDurationIncrement;
import xyz.playedu.points.domain.PointLedgerType;
import xyz.playedu.points.service.PointBalanceChange;
import xyz.playedu.points.service.PointBalanceChangeResult;
import xyz.playedu.points.service.PointBalanceService;
import xyz.playedu.points.service.PointSourceKeys;

/** Persists all MySQL facts created by one accepted course-hour progress interval. */
@Service
public class LearningFactPersistenceService {

    private static final int COURSE_COMPLETION_REWARD_POINTS = 10;

    private final CourseHourService courseHourService;
    private final DailyLearningDurationEventPublisher durationEventPublisher;
    private final RedisDistributedLock distributedLock;
    private final UserCourseHourRecordService userCourseHourRecordService;
    private final UserCourseRecordService userCourseRecordService;
    private final UserLearnDurationRecordService userLearnDurationRecordService;
    private final UserLearnDurationStatsService userLearnDurationStatsService;
    private final PointBalanceService pointBalanceService;

    public LearningFactPersistenceService(
            CourseHourService courseHourService,
            DailyLearningDurationEventPublisher durationEventPublisher,
            RedisDistributedLock distributedLock,
            UserCourseHourRecordService userCourseHourRecordService,
            UserCourseRecordService userCourseRecordService,
            UserLearnDurationRecordService userLearnDurationRecordService,
            UserLearnDurationStatsService userLearnDurationStatsService,
            PointBalanceService pointBalanceService) {
        this.courseHourService = courseHourService;
        this.durationEventPublisher = durationEventPublisher;
        this.distributedLock = distributedLock;
        this.userCourseHourRecordService = userCourseHourRecordService;
        this.userCourseRecordService = userCourseRecordService;
        this.userLearnDurationRecordService = userLearnDurationRecordService;
        this.userLearnDurationStatsService = userLearnDurationStatsService;
        this.pointBalanceService = pointBalanceService;
    }

    @Transactional
    public LearningFactPersistenceResult record(
            Integer userId,
            Integer courseId,
            Integer hourId,
            Integer watchedDuration,
            Integer hourDuration) {
        try {
            return distributedLock.execute(
                    "learning-fact",
                    userId.toString(),
                    () -> {
                        return persistLearningFacts(
                                userId, courseId, hourId, watchedDuration, hourDuration);
                    });
        } catch (RedisLockException exception) {
            throw new ServiceException("学习记录繁忙，请重试");
        }
    }

    /** Records a server-confirmed continuous interval without trusting a client progress value. */
    @Transactional
    public LearningFactPersistenceResult recordIncrement(
            Integer userId,
            Integer courseId,
            Integer hourId,
            Integer durationIncrement,
            Integer hourDuration) {
        if (durationIncrement <= 0) {
            return LearningFactPersistenceResult.NO_REWARD;
        }
        try {
            return distributedLock.execute(
                    "learning-fact",
                    userId.toString(),
                    () -> {
                        UserCourseHourRecord previous =
                                userCourseHourRecordService.find(userId, courseId, hourId);
                        int previousDuration =
                                previous == null ? 0 : previous.getFinishedDuration();
                        return persistLearningFacts(
                                userId,
                                courseId,
                                hourId,
                                previousDuration + durationIncrement,
                                hourDuration);
                    });
        } catch (RedisLockException exception) {
            throw new ServiceException("学习记录繁忙，请重试");
        }
    }

    private LearningFactPersistenceResult persistLearningFacts(
            Integer userId,
            Integer courseId,
            Integer hourId,
            Integer watchedDuration,
            Integer hourDuration) {
        UserCourseHourRecord previous = userCourseHourRecordService.find(userId, courseId, hourId);
        int previousDuration = previous == null ? 0 : previous.getFinishedDuration();
        int acceptedDuration = Math.min(watchedDuration, hourDuration);
        if (acceptedDuration <= previousDuration
                || (previous != null && previous.getIsFinished() == 1)) {
            return LearningFactPersistenceResult.NO_REWARD;
        }

        userCourseHourRecordService.storeOrUpdate(
                userId, courseId, hourId, acceptedDuration, hourDuration);
        Integer hourCount = courseHourService.getCountByCourseId(courseId);
        Integer finishedCount = userCourseHourRecordService.getFinishedHourCount(userId, courseId);
        CourseCompletionTransition completionTransition =
                userCourseRecordService.storeOrUpdate(userId, courseId, hourCount, finishedCount);
        int earnedPoints =
                completionTransition.isFirstCompletion()
                        ? awardCourseCompletion(userId, courseId)
                        : 0;

        long endedAt = System.currentTimeMillis();
        long duration = (long) (acceptedDuration - previousDuration) * 1000;
        long startedAt = endedAt - duration;
        List<DailyLearningDurationIncrement> durationIncrements =
                userLearnDurationStatsService.storeOrUpdate(userId, startedAt, endedAt);
        userLearnDurationRecordService.store(
                userId, courseId + "_" + hourId, "hour", startedAt, endedAt);
        for (DailyLearningDurationIncrement increment : durationIncrements) {
            durationEventPublisher.publishAfterCommit(
                    userId, increment.learningDate(), increment.duration());
        }
        return new LearningFactPersistenceResult(earnedPoints);
    }

    private int awardCourseCompletion(Integer userId, Integer courseId) {
        PointBalanceChangeResult result =
                pointBalanceService.apply(
                        new PointBalanceChange(
                                userId,
                                COURSE_COMPLETION_REWARD_POINTS,
                                PointLedgerType.COURSE_COMPLETION,
                                PointSourceKeys.courseCompletion(userId, courseId),
                                null,
                                null,
                                false));
        return result.applied() ? COURSE_COMPLETION_REWARD_POINTS : 0;
    }
}
