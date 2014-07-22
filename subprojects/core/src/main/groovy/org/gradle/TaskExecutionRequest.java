/*
 * Copyright 2013 the original author or authors.
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

package org.gradle;

import org.gradle.api.Incubating;
import org.gradle.api.Nullable;

/**
 * Task name with an optional project path context to provide information necessary to initiate the build.
 *
 * @since 2.0
 */
@Incubating
public interface TaskExecutionRequest {
    /**
     * Task path for real tasks, selector name for task selectors.
     *
     * @return task name.
     */
    String getTaskName();

    /**
     * Project path associated with this task parameter request if any.
     *
     * @return project path or {@code null} to use the default project path.
     */
    @Nullable String getProjectPath();
}