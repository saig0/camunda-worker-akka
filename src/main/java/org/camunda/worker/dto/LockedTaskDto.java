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

import java.util.Date;
import java.util.Map;

/**
 * @author Daniel Meyer
 *
 */
public class LockedTaskDto {

  protected String id;

  protected String topicName;

  protected Date lockTime;

  protected String activityId;

  protected String activityInstanceId;

  protected String processInstanceId;

  protected String processDefinitionId;

  protected Map<String, Object> variables;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public Date getLockTime() {
    return lockTime;
  }

  public void setLockTime(Date lockTime) {
    this.lockTime = lockTime;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public String getActivityInstanceId() {
    return activityInstanceId;
  }

  public void setActivityInstanceId(String activityInstanceId) {
    this.activityInstanceId = activityInstanceId;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, Object> variables) {
    this.variables = variables;
  }

  @Override
  public String toString() {
    return "LockedTaskDto [id=" + id + ", topicName=" + topicName + ", lockTime=" + lockTime + ", activityId=" + activityId + ", activityInstanceId="
        + activityInstanceId + ", processInstanceId=" + processInstanceId + ", processDefinitionId=" + processDefinitionId + ", variables=" + variables + "]";
  }
}
