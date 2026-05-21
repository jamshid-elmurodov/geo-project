package org.elec.geoproject.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

  @Value("${geo.async.core-pool-size:4}")
  private int corePoolSize;

  @Value("${geo.async.max-pool-size:8}")
  private int maxPoolSize;

  @Value("${geo.async.queue-capacity:100}")
  private int queueCapacity;

  @Value("${geo.async.thread-name-prefix:geo-async-}")
  private String threadNamePrefix;

  @Override
  @Bean(name = "taskExecutor")
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix(threadNamePrefix);
    executor.initialize();
    return executor;
  }
}
