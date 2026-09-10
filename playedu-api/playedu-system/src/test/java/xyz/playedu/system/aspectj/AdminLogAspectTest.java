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
package xyz.playedu.system.aspectj;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AdminLogAspectTest {

    @Test
    void masksVoucherCodeFieldsBeforeTheyReachTheOperationLog() {
        String params =
                "{\"codes\":\"SECRET-CODE\",\"multiline_codes\":\"SECRET-CODE-2\","
                        + "\"reason\":\"人工补发\"}";

        String masked = new AdminLogAspect().excludeProperties(params).toString();

        assertThat(masked).doesNotContain("SECRET-CODE", "SECRET-CODE-2");
        assertThat(masked).contains("人工补发");
    }
}
