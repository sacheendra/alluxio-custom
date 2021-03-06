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

package alluxio.master.job.tracker;

import alluxio.exception.JobDoesNotExistException;
import alluxio.job.JobConfig;
import alluxio.job.wire.JobInfo;
import alluxio.job.wire.Status;
import alluxio.master.job.JobMaster;
import alluxio.retry.RetryPolicy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *  class for handling submission for a child job within a distributed command.
 */
public class CmdRunAttempt {
  private static final Logger LOG = LoggerFactory.getLogger(CmdRunAttempt.class);

  protected final RetryPolicy mRetryPolicy;
  protected final JobMaster mJobMaster;

  private Long mJobId;
  private long mCreationTime;

  private JobConfig mJobConfig;
  private long mFileCount;
  private long mFileSize;
  private Set<String> mFailedFiles = new HashSet<>(); // FAILED files

  protected CmdRunAttempt(RetryPolicy retryPolicy, JobMaster jobMaster) {
    mRetryPolicy = retryPolicy;
    mJobMaster = jobMaster;
    mJobId = null;
  }

  /**
   * Set job config.
   * @param config
   */
  public void setConfig(JobConfig config) {
    mJobConfig = config;
  }

  /**
   * Set file count.
   * @param fileCount
   */
  public void setFileCount(long fileCount) {
    mFileCount = fileCount;
  }

  /**
   * Set file size.
   * @param fileSize
   */
  public void setFileSize(long fileSize) {
    mFileSize = fileSize;
  }

  /**
   * Get job config.
   * @return job config
   */
  public JobConfig getJobConfig() {
    return mJobConfig;
  }

  /**
   * Get file count.
   * @return file count
   */
  public long getFileCount() {
    return mFileCount;
  }

  /**
   * Get file size.
   * @return file size
   */
  public long getFileSize() {
    return mFileSize;
  }

  /**
   * Returns the job creation time.
   * @return creation time of a job
   */
  public long getCreationTime() {
    return mCreationTime;
  }

  /**
   * Runs the job.
   * @return true if an attempt was made, false if attempts ran out
   */
  public boolean run() {
    mCreationTime = System.currentTimeMillis(); // set job creation time.
    while (mRetryPolicy.attempt()) {
      try {
        mJobId = mJobMaster.run(getJobConfig());
      } catch (IOException | JobDoesNotExistException e) {
        int retryCount = mRetryPolicy.getAttemptCount();
        System.out.println(String.format("Retry %d Failed to start job with error: %s",
                retryCount, e.getMessage()));
        LOG.warn("Retry {} Failed to get status for job (jobId={}) {}", retryCount, mJobId, e);
        continue;
        // Do nothing. This will be counted as a failed attempt
      }
      return true;
    }
    return false;
  }

  /**
   * Returns the job Id.
   * @return job Id
   */
  public Long getJobId() {
    return mJobId;
  }

  /**
   * Returns the status of the job.
   * @return True if finished successfully or cancelled, False if FAILED and should be retried,
   *              null if the status should be checked again later
   */
  public Status checkJobStatus() {
    if (mJobId == null) {
      return Status.FAILED;
    }

    JobInfo jobInfo;
    try {
      jobInfo = mJobMaster.getStatus(mJobId);
    } catch (JobDoesNotExistException e) {
      LOG.warn("Failed to get status for job (jobId={})", mJobId, e);
      return Status.FAILED;
    }

    // This make an assumption that this job tree only goes 1 level deep
    boolean finished = true;
    for (JobInfo child : jobInfo.getChildren()) {
      if (!child.getStatus().isFinished()) {
        finished = false;
        break;
      }
    }

    if (finished) {
      if (jobInfo.getStatus().equals(Status.FAILED)) {
        Set<JobInfo> failed = jobInfo.getChildren().stream()
                .filter(child -> child.getStatus() == Status.FAILED).collect(Collectors.toSet());
        for (JobInfo task : failed) {
          mFailedFiles.add(StringUtils.substringBetween(task.getDescription(), "FilePath=", ","));
        }
      }
      return jobInfo.getStatus();
    }
    return Status.RUNNING;
  }

  /**
   * Log failed file information.
   */
  public void printFailed() {
    LOG.warn("Failed file paths are:  ");
    mFailedFiles.forEach(LOG::warn);
  }

  /**
   * Return a copy of failed paths.
   * @return failed files
   */
  public Set<String> getFailedFiles() {
    return new HashSet<>(mFailedFiles);
  }
}
