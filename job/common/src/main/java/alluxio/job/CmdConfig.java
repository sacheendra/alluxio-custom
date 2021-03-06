/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.job;

import alluxio.grpc.OperationType;
import alluxio.job.wire.JobSource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.Collection;

/**
 * A Cmd configuration. All the subclasses are both Java and JSON serializable.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public interface CmdConfig extends Serializable {
  /**
   * @return name of the Cmd job
   */
  String getName();

  /**
   * @return JobSource of the Cmd job
   */
  JobSource getJobSource();

  /**
   * @return OperationType of the Cmd job
   */
  OperationType getOperationType();

  /**
   * @return list of affected paths
   */
  Collection<String> affectedPaths();
}
