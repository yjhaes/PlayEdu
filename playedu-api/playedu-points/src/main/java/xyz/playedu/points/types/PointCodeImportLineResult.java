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
package xyz.playedu.points.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A safe, code-free import result for one input line. */
public final class PointCodeImportLineResult {

    private final int lineNumber;
    private final PointCodeImportLineStatus status;

    public PointCodeImportLineResult(int lineNumber, PointCodeImportLineStatus status) {
        this.lineNumber = lineNumber;
        this.status = status;
    }

    @JsonProperty("line_number")
    public int getLineNumber() {
        return lineNumber;
    }

    public PointCodeImportLineStatus getStatus() {
        return status;
    }
}
