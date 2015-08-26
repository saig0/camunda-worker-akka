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

import java.util.List;

/**
 * @author Daniel Meyer
 *
 */
public class PollAndLockTaskRequestDto {

  protected String topicName;

  protected List<String> variableNames;

  protected int maxTasks;

  protected int lockTimeInSeconds;

  protected String consumerId;

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topic) {
    this.topicName = topic;
  }

  public List<String> getVariableNames() {
    return variableNames;
  }

  public void setVariableNames(List<String> variableNames) {
    this.variableNames = variableNames;
  }

  public int getLockTimeInSeconds() {
    return lockTimeInSeconds;
  }

  public void setLockTimeInSeconds(int lockTime) {
    this.lockTimeInSeconds = lockTime;
  }

  public int getMaxTasks() {
    return maxTasks;
  }

  public void setMaxTasks(int maxTasks) {
    this.maxTasks = maxTasks;
  }

  public String getConsumerId() {
    return consumerId;
  }

  public void setConsumerId(String consumerId) {
    this.consumerId = consumerId;
  }

  @Override
  public String toString() {
    return "PollAndLockTaskRequestDto [topicName=" + topicName + ", variableNames=" + variableNames + ", maxTasks=" + maxTasks + ", lockTimeInSeconds="
        + lockTimeInSeconds + ", consumerId=" + consumerId + "]";
  }

}
