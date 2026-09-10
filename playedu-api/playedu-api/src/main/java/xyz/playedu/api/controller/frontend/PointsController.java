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

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.playedu.common.context.FCtx;
import xyz.playedu.common.domain.User;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.common.service.UserService;
import xyz.playedu.common.types.JsonResponse;
import xyz.playedu.points.migration.HistoricalRewardSummaryService;
import xyz.playedu.points.migration.PointsFeatureGate;

/** Learner-facing points summary and one-time historical reward notice. */
@RestController
@RequestMapping("/api/v1/points")
public class PointsController {

    private final UserService userService;
    private final PointsFeatureGate pointsFeatureGate;
    private final HistoricalRewardSummaryService historicalRewardSummaryService;

    public PointsController(
            UserService userService,
            PointsFeatureGate pointsFeatureGate,
            HistoricalRewardSummaryService historicalRewardSummaryService) {
        this.userService = userService;
        this.pointsFeatureGate = pointsFeatureGate;
        this.historicalRewardSummaryService = historicalRewardSummaryService;
    }

    @GetMapping("/summary")
    public JsonResponse summary() {
        pointsFeatureGate.requireOpen();
        Integer userId = FCtx.getId();
        if (userId == null) {
            throw new ServiceException("请登录后查看积分");
        }

        User user = userService.find(userId);
        if (user == null) {
            throw new ServiceException("学员不存在");
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("credit1", user.getCredit1());
        data.put(
                "historical_reward_summary",
                historicalRewardSummaryService.claimForDisplay(userId).orElse(null));
        return JsonResponse.data(data);
    }
}
