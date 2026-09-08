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
package xyz.playedu.course.event;

import java.time.LocalDate;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/** The confirmed daily increment available to the asynchronous ranking projection. */
@Getter
public class DailyLearningDurationConfirmedEvent extends ApplicationEvent {

    private final Integer userId;
    private final LocalDate learningDate;
    private final Long duration;

    public DailyLearningDurationConfirmedEvent(
            Object source, Integer userId, LocalDate learningDate, Long duration) {
        super(source);
        this.userId = userId;
        this.learningDate = learningDate;
        this.duration = duration;
    }
}
