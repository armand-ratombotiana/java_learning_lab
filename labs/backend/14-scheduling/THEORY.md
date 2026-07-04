# Theory: Scheduling

## Task Execution
Spring provides two abstractions for scheduling:

### TaskExecutor
Async task execution (replaces java.util.concurrent.Executor):
```java
@Bean
public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    return executor;
}
```

### TaskScheduler
Scheduled task execution:
```java
@Bean
public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(5);
    return scheduler;
}
```

### Cron Expressions
```
┌───────── minute (0-59)
│ ┌───────── hour (0-23)
│ │ ┌───────── day of month (1-31)
│ │ │ ┌───────── month (1-12)
│ │ │ │ ┌───────── day of week (0-7)
│ │ │ │ │
* * * * * command
```

Examples:
- `0 0 * * * *`: Every hour
- `0 */5 * * * *`: Every 5 minutes
- `0 0 9-17 * * MON-FRI`: Weekdays 9-5
- `0 0 0 1 1 *`: January 1st midnight
