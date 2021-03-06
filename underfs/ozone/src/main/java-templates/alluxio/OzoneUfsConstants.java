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

package alluxio;

/**
 * Ufs constants from compilation time by maven.
 */
public final class OzoneUfsConstants {
  /* Hadoop version, specified in maven property. **/
  public static final String UFS_OZONE_VERSION = "${ufs.ozone.version}";

  private OzoneUfsConstants() {} // prevent instantiation
}
