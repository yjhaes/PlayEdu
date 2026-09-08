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
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.common.redis.RedisDistributedLock;
import xyz.playedu.course.domain.UserCourseHourRecord;
import xyz.playedu.course.event.DailyLearningDurationEventPublisher;
import xyz.playedu.course.event.DailyLearningDurationIncrement;

/** Persists all MySQL facts created by one accepted course-hour progress interval. */
@Service
public class LearningFactPersistenceService {

    private final CourseHourService courseHourService;
    private final DailyLearningDurationEventPublisher durationEventPublisher;
    private final RedisDistributedLock distributedLock;
    private final UserCourseHourRecordService userCourseHourRecordService;
    private final UserCourseRecordService userCourseRecordService;
    private final UserLearnDurationRecordService userLearnDurationRecordService;
    private final UserLearnDurationStatsService userLearnDurationStatsService;

    public LearningFactPersistenceService(
            CourseHourService courseHourService,
            DailyLearningDurationEventPublisher durationEventPublisher,
            RedisDistributedLock distributedLock,
            UserCourseHourRecordService userCourseHourRecordService,
            UserCourseRecordService userCourseRecordService,
            UserLearnDurationRecordService userLearnDurationRecordService,
            UserLearnDurationStatsService userLearnDurationStatsService) {
        this.courseHourService = courseHourService;
        this.durationEventPublisher = durationEventPublisher;
        this.distributedLock = distributedLock;
        this.userCourseHourRecordService = userCourseHourRecordService;
        this.userCourseRecordService = userCourseRecordService;
        this.userLearnDurationRecordService = userLearnDurationRecordService;
        this.userLearnDurationStatsService = userLearnDurationStatsService;
    }

    @Transactional
    public void record(
            Integer userId,
            Integer courseId,
            Integer hourId,
            Integer watchedDuration,
            Integer hourDuration) {
        if (!distributedLock.tryLock("learning-fact", userId.toString(), 5, 30, TimeUnit.SECONDS)) {
            throw new ServiceException("学习记录繁忙，请重试");
        }

        try {
            UserCourseHourRecord previous =
                    userCourseHourRecordService.find(userId, courseId, hourId);
            int previousDuration = previous == null ? 0 : previous.getFinishedDuration();
            int acceptedDuration = Math.min(watchedDuration, hourDuration);
            if (acceptedDuration <= previousDuration
                    || (previous != null && previous.getIsFinished() == 1)) {
                return;
            }

            userCourseHourRecordService.storeOrUpdate(
                    userId, courseId, hourId, acceptedDuration, hourDuration);
            Integer hourCount = courseHourService.getCountByCourseId(courseId);
            Integer finishedCount =
                    userCourseHourRecordService.getFinishedHourCount(userId, courseId);
            userCourseRecordService.storeOrUpdate(userId, courseId, hourCount, finishedCount);

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
        } finally {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            distributedLock.release("learning-fact", userId.toString());
                        }
                    });
        }
    }
}
