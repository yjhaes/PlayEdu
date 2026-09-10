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
package xyz.playedu.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.playedu.api.service.UserDeletionService;
import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.common.service.UserLoginRecordService;
import xyz.playedu.common.service.UserService;
import xyz.playedu.course.service.UserCourseHourRecordService;
import xyz.playedu.course.service.UserCourseRecordService;
import xyz.playedu.course.service.UserLearnDurationRecordService;
import xyz.playedu.course.service.UserLearnDurationStatsService;
import xyz.playedu.course.service.impl.DailyLearningRankingService;
import xyz.playedu.points.service.PointLedgerService;
import xyz.playedu.points.service.PointRedemptionService;

/** Coordinates the database-owned parts of physical learner deletion. */
@Service
@Slf4j
public class UserDeletionServiceImpl implements UserDeletionService {

    private final UserService userService;
    private final UserCourseHourRecordService userCourseHourRecordService;
    private final UserCourseRecordService userCourseRecordService;
    private final UserLearnDurationRecordService userLearnDurationRecordService;
    private final UserLearnDurationStatsService userLearnDurationStatsService;
    private final UserLoginRecordService userLoginRecordService;
    private final PointLedgerService pointLedgerService;
    private final PointRedemptionService pointRedemptionService;
    private final DailyLearningRankingService dailyLearningRankingService;

    public UserDeletionServiceImpl(
            UserService userService,
            UserCourseHourRecordService userCourseHourRecordService,
            UserCourseRecordService userCourseRecordService,
            UserLearnDurationRecordService userLearnDurationRecordService,
            UserLearnDurationStatsService userLearnDurationStatsService,
            UserLoginRecordService userLoginRecordService,
            PointLedgerService pointLedgerService,
            PointRedemptionService pointRedemptionService,
            DailyLearningRankingService dailyLearningRankingService) {
        this.userService = userService;
        this.userCourseHourRecordService = userCourseHourRecordService;
        this.userCourseRecordService = userCourseRecordService;
        this.userLearnDurationRecordService = userLearnDurationRecordService;
        this.userLearnDurationStatsService = userLearnDurationStatsService;
        this.userLoginRecordService = userLoginRecordService;
        this.pointLedgerService = pointLedgerService;
        this.pointRedemptionService = pointRedemptionService;
        this.dailyLearningRankingService = dailyLearningRankingService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void destroy(Integer userId) throws NotFoundException {
        userService.ensureExistsForUpdate(userId);

        pointRedemptionService.removeByUserId(userId);
        pointLedgerService.removeByUserId(userId);
        userService.removeRelateDepartmentsByUserId(userId);
        userCourseHourRecordService.remove(userId);
        userCourseRecordService.destroy(userId);
        userLearnDurationRecordService.remove(userId);
        userLearnDurationStatsService.remove(userId);
        userLoginRecordService.remove(userId);

        if (!userService.removeById(userId)) {
            throw new ServiceException("学员删除失败");
        }

        // Redis is a projection, so a transient failure must not leave authoritative MySQL
        // deletion half-completed. The existing best-effort behavior is retained here.
        try {
            dailyLearningRankingService.removeUser(userId);
        } catch (RuntimeException exception) {
            log.atError()
                    .setMessage("learning_ranking_user_cleanup_failed")
                    .addKeyValue("user_id", userId)
                    .setCause(exception)
                    .log();
        }
    }
}
