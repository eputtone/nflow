package com.nitorcreations.nflow.engine.internal.executor;

import java.util.concurrent.BlockingQueue;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.threeten.bp.ZonedDateTime;

public class ThresholdThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
  private static final long serialVersionUID = 1L;
  private int threshold;

  public void setNotifyThreshold(int threshold) {
    this.threshold = threshold;
  }

  public void waitUntilQueueSizeLowerThanThreshold(ZonedDateTime waitUntil) throws InterruptedException {
    ((ThresholdBlockingQueue<?>) getThreadPoolExecutor().getQueue()).waitUntilQueueSizeLowerThanThreshold(waitUntil);
  }

  @Override
  protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
    return new ThresholdBlockingQueue<>(queueCapacity, threshold);
  }
}
