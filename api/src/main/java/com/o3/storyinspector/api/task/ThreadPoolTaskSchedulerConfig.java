package com.o3.storyinspector.api.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@ComponentScan(basePackages = "com.o3.storyinspector.api")
public class ThreadPoolTaskSchedulerConfig {

    private static final int THREAD_POOL_SIZE = 1;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(THREAD_POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("ProcessBookTaskScheduler");
        return threadPoolTaskScheduler;
    }
}