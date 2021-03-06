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

package alluxio.job.plan.replicate;

import alluxio.util.CommonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Test {@link SetReplicaConfig}.
 */
public final class SetReplicaConfigTest {
  @Test
  public void json() throws Exception {
    SetReplicaConfig config = createRandom();
    ObjectMapper mapper = new ObjectMapper();
    SetReplicaConfig other =
        mapper.readValue(mapper.writeValueAsString(config), SetReplicaConfig.class);
    checkEquality(config, other);
  }

  @Test
  public void negativeReplicateNumber() {
    try {
      new SetReplicaConfig("", 0, -1);
      Assert.fail("Cannot create ReplicateConfig with negative replicateNumber");
    } catch (IllegalArgumentException exception) {
      // expected exception thrown. test passes
    }
  }

  public void checkEquality(SetReplicaConfig a, SetReplicaConfig b) {
    Assert.assertEquals(a.getBlockId(), b.getBlockId());
    Assert.assertEquals(a.getReplicas(), b.getReplicas());
    Assert.assertEquals(a, b);
  }

  public static SetReplicaConfig createRandom() {
    Random random = new Random();
    String path = "/" + CommonUtils.randomAlphaNumString(random.nextInt(10) + 1);
    SetReplicaConfig config =
        new SetReplicaConfig(path, random.nextLong(), random.nextInt(Integer.MAX_VALUE) + 1);
    return config;
  }
}
