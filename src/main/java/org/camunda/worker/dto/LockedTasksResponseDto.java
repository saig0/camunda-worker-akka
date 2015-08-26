/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.camunda.worker.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Meyer
 *
 */
public class LockedTasksResponseDto {

  protected List<LockedTaskDto> tasks = new ArrayList<LockedTaskDto>();

  public List<LockedTaskDto> getTasks() {
    return tasks;
  }

  public void setTasks(List<LockedTaskDto> tasks) {
    this.tasks = tasks;
  }

  @Override
  public String toString() {
    return "LockedTasksResponseDto [tasks=" + tasks + "]";
  }
}
