# Scheduling

Task scheduling and asynchronous execution in Spring.

## Topics
- @Scheduled annotation (fixedRate, fixedDelay, cron)
- Cron expressions
- @Async for asynchronous execution
- TaskExecutor configuration
- TaskScheduler customization
- Error handling in scheduled tasks
- Dynamic scheduling with TaskScheduler
- Distributed scheduling with ShedLock

## Example
```java
@Component
public class ScheduledTasks {
    @Scheduled(cron = "0 0 * * * *")
    public void hourlyReport() {
        reportService.generateHourlyReport();
    }

    @Scheduled(fixedRate = 60000, initialDelay = 10000)
    public void cacheRefresh() {
        cacheService.refresh();
    }
}
```
