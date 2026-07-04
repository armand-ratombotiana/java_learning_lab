# Code Deep Dive: Scheduling

## Advanced Scheduling
```java
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("scheduled-");
        return scheduler;
    }
}

@Component
public class ReportScheduler {
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledTask;

    // Dynamic scheduling
    @PostConstruct
    public void init() {
        scheduledTask = taskScheduler.scheduleAtFixedRate(
            () -> generateReport(), Duration.ofHours(1));
    }

    public void reschedule(Duration newInterval) {
        scheduledTask.cancel(false);
        scheduledTask = taskScheduler.scheduleAtFixedRate(
            () -> generateReport(), newInterval);
    }

    @Async("taskExecutor")
    public CompletableFuture<Report> generateReportAsync() {
        Report report = generateReport();
        return CompletableFuture.completedFuture(report);
    }
}

@Component
public class DailyTasks {
    @Scheduled(cron = "0 0 6 * * *")
    public void dailySummary() {
        log.info("Generating daily summary...");
        summaryService.generateAndEmail();
    }

    @Scheduled(fixedDelayString = "${{report.delay.ms}}")
    public void configurableTask() {
        // Delay read from properties
    }
}
```
