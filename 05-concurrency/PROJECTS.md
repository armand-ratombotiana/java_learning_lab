# Java Concurrency Module - PROJECTS.md

---

# 🎯 Mini-Project: Multi-threaded Task Processor

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Thread, ExecutorService, Callable, Future, Synchronization, Thread Safety

This project demonstrates Java concurrency fundamentals through a practical task processing system.

---

## Project Structure

```
05-concurrency/src/main/java/com/learning/project/
├── Main.java
├── model/
│   └── Task.java
├── service/
│   └── TaskProcessor.java
├── executor/
│   └── TaskExecutor.java
└── ui/
    └── ProcessorMenu.java
```

---

## Step 1: Task Model

```java
// model/Task.java
package com.learning.project.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private final String id;
    private final String name;
    private final TaskType type;
    private final int priority;
    private final int estimatedDuration;
    private TaskStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String result;
    private Exception error;
    
    public enum TaskType {
        COMPUTATION, IO, NETWORK, ANALYSIS
    }
    
    public enum TaskStatus {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    }
    
    public Task(String name, TaskType type, int priority, int estimatedDuration) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.type = type;
        this.priority = priority;
        this.estimatedDuration = estimatedDuration;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public void start() {
        this.status = TaskStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }
    
    public void complete(String result) {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.result = result;
    }
    
    public void fail(Exception error) {
        this.status = TaskStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.error = error;
    }
    
    public void cancel() {
        this.status = TaskStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public TaskType getType() { return type; }
    public int getPriority() { return priority; }
    public int getEstimatedDuration() { return estimatedDuration; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getResult() { return result; }
    public Exception getError() { return error; }
    
    public long getDuration() {
        if (startedAt == null || completedAt == null) return 0;
        return java.time.Duration.between(startedAt, completedAt).toMillis();
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s", id, name, type, status);
    }
}
```

---

## Step 2: Task Processor Service

```java
// service/TaskProcessor.java
package com.learning.project.service;

import com.learning.project.model.Task;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class TaskProcessor {
    private final ExecutorService executor;
    private final Map<String, Task> tasks;
    private final Map<String, Future<Task>> futures;
    private final int maxConcurrent;
    
    public TaskProcessor(int threads, int maxConcurrent) {
        this.executor = Executors.newFixedThreadPool(threads);
        this.tasks = new ConcurrentHashMap<>();
        this.futures = new ConcurrentHashMap<>();
        this.maxConcurrent = maxConcurrent;
    }
    
    public String submitTask(Task task) {
        if (futures.size() >= maxConcurrent) {
            return null; // Queue full
        }
        
        tasks.put(task.getId(), task);
        
        Future<Task> future = executor.submit(() -> {
            task.start();
            
            try {
                String result = processTask(task);
                task.complete(result);
            } catch (Exception e) {
                task.fail(e);
            }
            
            return task;
        });
        
        futures.put(task.getId(), future);
        return task.getId();
    }
    
    private String processTask(Task task) throws InterruptedException {
        // Simulate different processing based on task type
        Thread.sleep(task.getEstimatedDuration());
        
        switch (task.getType()) {
            case COMPUTATION:
                return performComputation(task);
            case IO:
                return performIO(task);
            case NETWORK:
                return performNetwork(task);
            case ANALYSIS:
                return performAnalysis(task);
            default:
                return "Unknown task type";
        }
    }
    
    private String performComputation(Task task) {
        long sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum += i;
        }
        return String.format("Computation complete: sum=%d", sum);
    }
    
    private String performIO(Task task) {
        return "IO operation completed: read/wrote data";
    }
    
    private String performNetwork(Task task) {
        return "Network request completed: fetched data";
    }
    
    private String performAnalysis(Task task) {
        return "Analysis complete: processed " + task.getEstimatedDuration() + " records";
    }
    
    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }
    
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    
    public List<Task> getPendingTasks() {
        return tasks.values().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.PENDING)
            .collect(Collectors.toList());
    }
    
    public List<Task> getCompletedTasks() {
        return tasks.values().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED)
            .collect(Collectors.toList());
    }
    
    public List<Task> getFailedTasks() {
        return tasks.values().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.FAILED)
            .collect(Collectors.toList());
    }
    
    public boolean cancelTask(String taskId) {
        Future<Task> future = futures.get(taskId);
        if (future != null) {
            boolean cancelled = future.cancel(true);
            if (cancelled) {
                Task task = tasks.get(taskId);
                if (task != null) {
                    task.cancel();
                }
            }
            return cancelled;
        }
        return false;
    }
    
    public boolean awaitTask(String taskId, long timeout, TimeUnit unit) 
            throws InterruptedException, ExecutionException {
        Future<Task> future = futures.get(taskId);
        if (future != null) {
            Task task = future.get(timeout, unit);
            return task.getStatus() == Task.TaskStatus.COMPLETED;
        }
        return false;
    }
    
    public void shutdown() {
        executor.shutdown();
    }
    
    public Map<String, Integer> getTaskCountByStatus() {
        return tasks.values().stream()
            .collect(Collectors.groupingBy(
                t -> t.getStatus().name(),
                Collectors.counting()
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().intValue()
            ));
    }
    
    public double getAverageDuration() {
        return tasks.values().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED)
            .mapToLong(Task::getDuration)
            .average()
            .orElse(0);
    }
    
    public int getActiveTaskCount() {
        return (int) tasks.values().stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.RUNNING)
            .count();
    }
}
```

---

## Step 3: Task Executor with Priority

```java
// executor/TaskExecutor.java
package com.learning.project.executor;

import com.learning.project.model.Task;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class TaskExecutor {
    private final PriorityBlockingQueue<Runnable> taskQueue;
    private final ExecutorService executor;
    private final Map<String, Task> completedTasks;
    private final int threadCount;
    
    public TaskExecutor(int threadCount) {
        this.threadCount = threadCount;
        this.taskQueue = new PriorityBlockingQueue<>(100, new TaskComparator());
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.completedTasks = new ConcurrentHashMap<>();
        
        // Start workers
        for (int i = 0; i < threadCount; i++) {
            executor.execute(this::processNextTask);
        }
    }
    
    private void processNextTask() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Runnable task = taskQueue.poll(1, TimeUnit.SECONDS);
                if (task != null) {
                    task.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public Future<Task> submit(Task task, Callable<Task> processor) {
        TaskFuture future = new TaskFuture(task, processor);
        taskQueue.offer(future);
        return future;
    }
    
    public void shutdown() {
        executor.shutdown();
    }
    
    private class TaskComparator implements Comparator<Runnable> {
        @Override
        public int compare(Runnable r1, Runnable r2) {
            if (r1 instanceof PrioritizedTask && r2 instanceof PrioritizedTask) {
                int p1 = ((PrioritizedTask) r1).getPriority();
                int p2 = ((PrioritizedTask) r2).getPriority();
                return Integer.compare(p2, p1); // Higher priority first
            }
            return 0;
        }
    }
    
    private interface PrioritizedTask {
        int getPriority();
    }
    
    private class TaskFuture implements Future<Task> {
        private final Task task;
        private final Callable<Task> callable;
        private volatile boolean cancelled;
        private volatile boolean done;
        private Task result;
        
        TaskFuture(Task task, Callable<Task> callable) {
            this.task = task;
            this.callable = callable;
        }
        
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            cancelled = true;
            return true;
        }
        
        @Override
        public boolean isCancelled() {
            return cancelled;
        }
        
        @Override
        public boolean isDone() {
            return done;
        }
        
        @Override
        public Task get() throws InterruptedException, ExecutionException {
            if (!done) {
                synchronized (this) {
                    while (!done) {
                        wait();
                    }
                }
            }
            return result;
        }
        
        @Override
        public Task get(long timeout, TimeUnit unit) 
                throws InterruptedException, ExecutionException, TimeoutException {
            if (!done) {
                synchronized (this) {
                    wait(timeout);
                }
            }
            if (!done) {
                throw new TimeoutException();
            }
            return result;
        }
        
        public void setResult(Task result) {
            this.result = result;
            this.done = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }
}
```

---

## Step 4: Processor Menu Interface

```java
// ui/ProcessorMenu.java
package com.learning.project.ui;

import com.learning.project.model.Task;
import com.learning.project.service.TaskProcessor;
import java.util.*;

public class ProcessorMenu {
    private Scanner scanner;
    private TaskProcessor processor;
    private boolean running;
    
    public ProcessorMenu() {
        this.scanner = new Scanner(System.in);
        this.processor = new TaskProcessor(4, 10); // 4 threads, max 10 concurrent
        this.running = true;
    }
    
    public void start() {
        System.out.println("\n⚡ MULTI-THREADED TASK PROCESSOR");
        System.out.println("=================================");
        
        // Create some sample tasks
        createSampleTasks();
        
        while (running) {
            displayMenu();
            handleChoice(getChoice());
        }
        
        processor.shutdown();
    }
    
    private void createSampleTasks() {
        processor.submitTask(new Task("Data Processing", Task.TaskType.COMPUTATION, 1, 2000));
        processor.submitTask(new Task("File Download", Task.TaskType.IO, 2, 3000));
        processor.submitTask(new Task("API Call", Task.TaskType.NETWORK, 3, 1500));
        processor.submitTask(new Task("Report Generation", Task.TaskType.ANALYSIS, 1, 2500));
    }
    
    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Submit New Task");
        System.out.println("2. View All Tasks");
        System.out.println("3. View Pending Tasks");
        System.out.println("4. View Completed Tasks");
        System.out.println("5. View Failed Tasks");
        System.out.println("6. Check Task Status");
        System.out.println("7. Cancel Task");
        System.out.println("8. Statistics");
        System.out.println("9. Wait for Task");
        System.out.println("10. Exit");
        System.out.print("\nChoice: ");
    }
    
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> submitTask();
                case 2 -> viewAllTasks();
                case 3 -> viewPendingTasks();
                case 4 -> viewCompletedTasks();
                case 5 -> viewFailedTasks();
                case 6 -> checkTaskStatus();
                case 7 -> cancelTask();
                case 8 -> showStatistics();
                case 9 -> waitForTask();
                case 10 -> { System.out.println("Goodbye!"); running = false; }
                default -> System.out.println("Invalid choice!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        // Small delay to allow task completion
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }
    
    private void submitTask() {
        System.out.println("\n--- Submit Task ---");
        System.out.print("Task Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.println("Types: COMPUTATION, IO, NETWORK, ANALYSIS");
        System.out.print("Type: ");
        String typeStr = scanner.nextLine().trim().toUpperCase();
        
        System.out.print("Priority (1-5): ");
        int priority = getInt();
        
        System.out.print("Duration (ms): ");
        int duration = getInt();
        
        try {
            Task.TaskType type = Task.TaskType.valueOf(typeStr);
            Task task = new Task(name, type, priority, duration);
            String taskId = processor.submitTask(task);
            
            if (taskId != null) {
                System.out.println("Task submitted: " + taskId);
            } else {
                System.out.println("Task queue is full!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid task type!");
        }
    }
    
    private void viewAllTasks() {
        var tasks = processor.getAllTasks();
        printTasks(tasks);
    }
    
    private void viewPendingTasks() {
        var tasks = processor.getPendingTasks();
        printTasks(tasks);
    }
    
    private void viewCompletedTasks() {
        var tasks = processor.getCompletedTasks();
        printTasks(tasks);
    }
    
    private void viewFailedTasks() {
        var tasks = processor.getFailedTasks();
        printTasks(tasks);
    }
    
    private void checkTaskStatus() {
        System.out.print("Task ID: ");
        String id = scanner.nextLine().trim();
        
        Task task = processor.getTask(id);
        if (task != null) {
            System.out.println("\n--- Task Details ---");
            System.out.println("ID: " + task.getId());
            System.out.println("Name: " + task.getName());
            System.out.println("Type: " + task.getType());
            System.out.println("Status: " + task.getStatus());
            System.out.println("Priority: " + task.getPriority());
            
            if (task.getStartedAt() != null) {
                System.out.println("Started: " + task.getStartedAt());
            }
            if (task.getCompletedAt() != null) {
                System.out.println("Completed: " + task.getCompletedAt());
                System.out.println("Duration: " + task.getDuration() + "ms");
            }
            
            if (task.getResult() != null) {
                System.out.println("Result: " + task.getResult());
            }
            if (task.getError() != null) {
                System.out.println("Error: " + task.getError().getMessage());
            }
        } else {
            System.out.println("Task not found!");
        }
    }
    
    private void cancelTask() {
        System.out.print("Task ID: ");
        String id = scanner.nextLine().trim();
        
        if (processor.cancelTask(id)) {
            System.out.println("Task cancelled!");
        } else {
            System.out.println("Failed to cancel task!");
        }
    }
    
    private void showStatistics() {
        System.out.println("\n=== STATISTICS ===");
        System.out.println("Active Tasks: " + processor.getActiveTaskCount());
        
        var counts = processor.getTaskCountByStatus();
        System.out.println("\nTasks by Status:");
        for (var entry : counts.entrySet()) {
            System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue());
        }
        
        System.out.printf("Average Duration: %.2f ms%n", processor.getAverageDuration());
    }
    
    private void waitForTask() throws Exception {
        System.out.print("Task ID: ");
        String id = scanner.nextLine().trim();
        
        System.out.print("Timeout (seconds): ");
        int timeout = getInt();
        
        boolean completed = processor.awaitTask(id, timeout, TimeUnit.SECONDS);
        System.out.println(completed ? "Task completed!" : "Task not completed within timeout");
    }
    
    private void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
    
    private int getInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static void main(String[] args) {
        new ProcessorMenu().start();
    }
}
```

---

## Running the Mini-Project

```bash
cd 05-concurrency
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.ProcessorMenu
```

---

## Concurrency Concepts Demonstrated

| Concept | Implementation |
|---------|----------------|
| **ExecutorService** | Fixed thread pool for task execution |
| **Callable/Future** | Submit tasks and get results |
| **Thread Safety** | ConcurrentHashMap for shared state |
| **Synchronization** | Methods for coordinated access |
| **Thread Pool** | Reusable worker threads |
| **Task Priority** | Priority queue for scheduling |

---

# 🚀 Real-World Project: Multi-threaded Web Scraper

## Project Overview

**Duration**: 15-20 hours  
**Difficulty**: Advanced  
**Concepts Used**: Concurrent Web Scraping, Rate Limiting, Caching, Thread Pools, Synchronization

This project implements a production-ready web scraper with concurrent fetching, rate limiting, and error handling.

---

## Project Structure

```
05-concurrency/src/main/java/com/learning/project/
├── Main.java
├── model/
│   ├── Page.java
│   ├── Link.java
│   └── ScraperResult.java
├── service/
│   ├── WebScraperService.java
│   └── LinkExtractor.java
├── scraper/
│   ├── AsyncWebFetcher.java
│   └── RateLimiter.java
├── cache/
│   └── PageCache.java
├── executor/
│   └── ScraperExecutor.java
├── queue/
│   └── WorkQueue.java
└── ui/
    └── ScraperMenu.java
```

---

## Step 1: Page and Link Models

```java
// model/Page.java
package com.learning.project.model;

import java.time.LocalDateTime;
import java.util.*;

public class Page {
    private final String url;
    private final String content;
    private final int statusCode;
    private final Map<String, String> headers;
    private final LocalDateTime fetchedAt;
    private final long fetchDuration;
    private final List<String> errors;
    
    public Page(String url, String content, int statusCode, 
                Map<String, String> headers, long fetchDuration) {
        this.url = url;
        this.content = content;
        this.statusCode = statusCode;
        this.headers = headers;
        this.fetchedAt = LocalDateTime.now();
        this.fetchDuration = fetchDuration;
        this.errors = new ArrayList<>();
    }
    
    public void addError(String error) {
        errors.add(error);
    }
    
    public String getUrl() { return url; }
    public String getContent() { return content; }
    public int getStatusCode() { return statusCode; }
    public Map<String, String> getHeaders() { return headers; }
    public LocalDateTime getFetchedAt() { return fetchedAt; }
    public long getFetchDuration() { return fetchDuration; }
    public List<String> getErrors() { return errors; }
    public boolean isSuccess() { return statusCode >= 200 && statusCode < 300; }
}

// model/Link.java
package com.learning.project.model;

public class Link {
    private final String url;
    private final String text;
    private final String sourceUrl;
    private final int depth;
    
    public Link(String url, String text, String sourceUrl, int depth) {
        this.url = url;
        this.text = text;
        this.sourceUrl = sourceUrl;
        this.depth = depth;
    }
    
    public String getUrl() { return url; }
    public String getText() { return text; }
    public String getSourceUrl() { return sourceUrl; }
    public int getDepth() { return depth; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return url.equals(link.url);
    }
    
    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
```

---

## Step 2: Rate Limiter

```java
// scraper/RateLimiter.java
package com.learning.project.scraper;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class RateLimiter {
    private final int maxRequestsPerSecond;
    private final AtomicLong lastRequestTime;
    private final Semaphore semaphore;
    private final AtomicInteger currentRequests;
    private final int maxConcurrent;
    
    public RateLimiter(int requestsPerSecond, int maxConcurrent) {
        this.maxRequestsPerSecond = requestsPerSecond;
        this.lastRequestTime = new AtomicLong(0);
        this.semaphore = new Semaphore(maxConcurrent);
        this.currentRequests = new AtomicInteger(0);
        this.maxConcurrent = maxConcurrent;
    }
    
    public boolean tryAcquire() {
        try {
            if (!semaphore.tryAcquire(1, 100, TimeUnit.MILLISECONDS)) {
                return false;
            }
            
            // Rate limiting
            long now = System.currentTimeMillis();
            long last = lastRequestTime.get();
            long minInterval = 1000 / maxRequestsPerSecond;
            
            if (now - last < minInterval) {
                try {
                    Thread.sleep(minInterval - (now - last));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            lastRequestTime.set(System.currentTimeMillis());
            currentRequests.incrementAndGet();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public void release() {
        semaphore.release();
        currentRequests.decrementAndGet();
    }
    
    public int getCurrentRequests() {
        return currentRequests.get();
    }
}
```

---

## Step 3: Async Web Fetcher

```java
// scraper/AsyncWebFetcher.java
package com.learning.project.scraper;

import com.learning.project.model.Page;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class AsyncWebFetcher {
    private final ExecutorService executor;
    private final RateLimiter rateLimiter;
    private final int timeout;
    private final Map<String, String> defaultHeaders;
    
    public AsyncWebFetcher(int threads, int requestsPerSecond, int timeout) {
        this.executor = Executors.newFixedThreadPool(threads);
        this.rateLimiter = new RateLimiter(requestsPerSecond, threads);
        this.timeout = timeout;
        
        this.defaultHeaders = new HashMap<>();
        defaultHeaders.put("User-Agent", "Mozilla/5.0 (compatible; JavaScraper/1.0)");
        defaultHeaders.put("Accept", "text/html,application/xhtml+xml");
    }
    
    public CompletableFuture<Page> fetchAsync(String url) {
        return CompletableFuture.supplyAsync(() -> fetch(url), executor);
    }
    
    public Page fetch(String url) {
        if (!rateLimiter.tryAcquire()) {
            return new Page(url, "", 429, Map.of(), 0);
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            
            for (var entry : defaultHeaders.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            
            int statusCode = conn.getResponseCode();
            Map<String, String> headers = new HashMap<>();
            for (var key : conn.getHeaderFields().keySet()) {
                if (key != null) {
                    headers.put(key, conn.getHeaderField(key));
                }
            }
            
            String content = "";
            if (statusCode == 200) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    content = sb.toString();
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            return new Page(url, content, statusCode, headers, duration);
            
        } catch (Exception e) {
            return new Page(url, "", -1, Map.of(), 0);
        } finally {
            rateLimiter.release();
        }
    }
    
    public List<CompletableFuture<Page>> fetchAll(List<String> urls) {
        return urls.stream()
            .map(this::fetchAsync)
            .collect(java.util.stream.Collectors.toList());
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
```

---

## Step 4: Link Extractor

```java
// service/LinkExtractor.java
package com.learning.project.service;

import com.learning.project.model.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class LinkExtractor {
    private static final Pattern HREF_PATTERN = 
        Pattern.compile("<a[^>]+href=[\"']([^\"']+)[\"'][^>]*>([^<]*)</a>", 
            Pattern.CASE_INSENSITIVE);
    
    private static final Pattern ABSOLUTE_URL_PATTERN = 
        Pattern.compile("https?://[^/]+");
    
    public List<Link> extractLinks(Page page, String baseUrl, int maxDepth) {
        List<Link> links = new ArrayList<>();
        
        if (!page.isSuccess() || page.getContent().isEmpty()) {
            return links;
        }
        
        Matcher matcher = HREF_PATTERN.matcher(page.getContent());
        
        while (matcher.find()) {
            String href = matcher.group(1);
            String text = matcher.group(2).trim();
            
            // Resolve relative URLs
            String absoluteUrl = resolveUrl(baseUrl, href);
            
            if (absoluteUrl != null && isValidUrl(absoluteUrl)) {
                links.add(new Link(absoluteUrl, text, baseUrl, maxDepth));
            }
        }
        
        return links.stream()
            .distinct()
            .collect(Collectors.toList());
    }
    
    private String resolveUrl(String baseUrl, String href) {
        try {
            // Skip anchors, javascript, mailto
            if (href.startsWith("#") || href.startsWith("javascript:") 
                    || href.startsWith("mailto:")) {
                return null;
            }
            
            URL base = new URL(baseUrl);
            URL resolved = new URL(base, href);
            return resolved.toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    private boolean isValidUrl(String url) {
        try {
            URL u = new URL(url);
            String protocol = u.getProtocol();
            return "http".equals(protocol) || "https".equals(protocol);
        } catch (MalformedURLException e) {
            return false;
        }
    }
    
    public Map<String, Integer> getLinkStatistics(List<Link> links) {
        return links.stream()
            .collect(Collectors.groupingBy(
                link -> {
                    try {
                        return new URL(link.getUrl()).getHost();
                    } catch (MalformedURLException e) {
                        return "unknown";
                    }
                },
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().intValue()
            ));
    }
}
```

---

## Step 5: Web Scraper Service

```java
// service/WebScraperService.java
package com.learning.project.service;

import com.learning.project.model.*;
import com.learning.project.scraper.AsyncWebFetcher;
import com.learning.project.scraper.RateLimiter;
import com.learning.project.cache.PageCache;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class WebScraperService {
    private final AsyncWebFetcher fetcher;
    private final LinkExtractor linkExtractor;
    private final PageCache cache;
    private final int maxDepth;
    private final int maxPages;
    private final ExecutorService executor;
    private final Set<String> visited;
    private final ConcurrentHashMap<String, Page> results;
    private final CountDownLatch latch;
    
    public WebScraperService(int threads, int rateLimit, int maxDepth, int maxPages) {
        this.fetcher = new AsyncWebFetcher(threads, rateLimit, 10000);
        this.linkExtractor = new LinkExtractor();
        this.cache = new PageCache(300); // 5 min TTL
        this.maxDepth = maxDepth;
        this.maxPages = maxPages;
        this.executor = Executors.newFixedThreadPool(threads);
        this.visited = ConcurrentHashMap.newKeySet();
        this.results = new ConcurrentHashMap<>();
        this.latch = new CountDownLatch(maxPages);
    }
    
    public void scrape(String startUrl) throws InterruptedException {
        scrapeRecursive(startUrl, 0);
        latch.await();
    }
    
    private void scrapeRecursive(String url, int depth) {
        if (depth > maxDepth || results.size() >= maxPages) {
            return;
        }
        
        if (!visited.add(url)) {
            return;
        }
        
        // Check cache
        Page cached = cache.get(url);
        if (cached != null) {
            results.put(url, cached);
            processPage(cached, depth);
            return;
        }
        
        // Fetch asynchronously
        fetcher.fetchAsync(url).thenAccept(page -> {
            results.put(url, page);
            cache.put(url, page);
            latch.countDown();
            
            if (page.isSuccess()) {
                processPage(page, depth);
            }
        });
        
        // Rate limiting handled by fetcher
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void processPage(Page page, int depth) {
        if (depth < maxDepth && page.isSuccess()) {
            List<Link> links = linkExtractor.extractLinks(page, page.getUrl(), depth + 1);
            
            for (Link link : links) {
                if (results.size() < maxPages) {
                    scrapeRecursive(link.getUrl(), depth + 1);
                }
            }
        }
    }
    
    public Map<String, Page> getResults() {
        return new HashMap<>(results);
    }
    
    public List<Page> getSuccessfulPages() {
        return results.values().stream()
            .filter(Page::isSuccess)
            .collect(Collectors.toList());
    }
    
    public List<Page> getFailedPages() {
        return results.values().stream()
            .filter(p -> !p.isSuccess())
            .collect(Collectors.toList());
    }
    
    public ScraperResult generateReport() {
        List<Page> successful = getSuccessfulPages();
        List<Page> failed = getFailedPages();
        
        long totalFetchTime = results.values().stream()
            .mapToLong(Page::getFetchDuration)
            .sum();
        
        double avgFetchTime = results.isEmpty() ? 0 : 
            (double) totalFetchTime / results.size();
        
        return new ScraperResult(
            results.size(),
            successful.size(),
            failed.size(),
            avgFetchTime,
            results.keySet()
        );
    }
    
    public void shutdown() {
        fetcher.shutdown();
        executor.shutdown();
    }
}
```

---

## Step 6: Page Cache

```java
// cache/PageCache.java
package com.learning.project.cache;

import com.learning.project.model.Page;
import java.util.concurrent.*;
import java.time.Instant;

public class PageCache {
    private final ConcurrentHashMap<String, CachedPage> cache;
    private final long ttlSeconds;
    
    private static class CachedPage {
        final Page page;
        final long cachedAt;
        
        CachedPage(Page page) {
            this.page = page;
            this.cachedAt = Instant.now().getEpochSecond();
        }
        
        boolean isExpired(long ttl) {
            return Instant.now().getEpochSecond() - cachedAt > ttl;
        }
    }
    
    public PageCache(long ttlSeconds) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlSeconds = ttlSeconds;
    }
    
    public void put(String url, Page page) {
        cache.put(url, new CachedPage(page));
    }
    
    public Page get(String url) {
        CachedPage cached = cache.get(url);
        if (cached == null) return null;
        
        if (cached.isExpired(ttlSeconds)) {
            cache.remove(url);
            return null;
        }
        
        return cached.page;
    }
    
    public void remove(String url) {
        cache.remove(url);
    }
    
    public void clear() {
        cache.clear();
    }
    
    public int size() {
        return cache.size();
    }
    
    public void cleanup() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired(ttlSeconds));
    }
}
```

---

## Running the Real-World Project

```bash
cd 05-concurrency
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.ScraperMenu
```

---

## Advanced Concurrency Concepts

| Concept | Implementation |
|---------|----------------|
| **CompletableFuture** | Async web fetching |
| **Rate Limiting** | Token bucket algorithm |
| **Thread Safety** | Concurrent collections |
| **Caching** | TTL-based page cache |
| **Link Extraction** | Concurrent processing |
| **Distributed Fetching** | Parallel URL fetching |

---

## Extensions

1. Add Proxy rotation
2. Implement retry with exponential backoff
3. Add database storage
4. Implement JavaScript rendering with Selenium
5. Add API for distributed scraping

---

## Next Steps

After completing this module:
- **Module 7**: Add file persistence for scraped data
- **Module 8**: Genericize the scraper for any data type
- **Module 4**: Use streams for data processing