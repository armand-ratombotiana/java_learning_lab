# Kotlin Coroutines Module - PROJECTS.md

---

# Mini-Project: Async Task Processing System

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Coroutine Builders, Dispatchers, Structured Concurrency, async/await, Flow, Channels

This mini-project demonstrates Kotlin coroutines for Java developers transitioning to async programming. We'll build an asynchronous task processing system that handles multiple concurrent operations without callback hell.

---

## Project Structure

```
25-kotlin-coroutines/
├── pom.xml
├── src/main/kotlin/com/learning/
│   ├── Main.kt
│   ├── model/
│   │   └── Task.kt
│   ├── service/
│   │   ├── TaskProcessor.kt
│   │   └── AsyncService.kt
│   └── util/
│       └── CoroutineUtils.kt
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>kotlin-coroutines-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <kotlin.version>1.9.20</kotlin.version>
        <kotlinx.coroutines.version>1.7.3</kotlinx.coroutines.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId>
            <version>${kotlin.version}</version>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlinx</groupId>
            <artifactId>kotlinx-coroutines-core</artifactId>
            <version>${kotlinx.coroutines.version}</version>
        </dependency>
    </dependencies>
    
    <build>
        <sourceDirectory>${project.basedir}/src/main/kotlin</sourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${kotlin.version}</version>
                <executions>
                    <execution>
                        <id>compile</id>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>test-compile</id>
                        <goals>
                            <goal>test-compile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Step 2: Model Classes

```kotlin
// model/Task.kt
package com.learning.model

data class Task(
    val id: String,
    val name: String,
    val description: String,
    val priority: Int = 0,
    val status: TaskStatus = TaskStatus.PENDING
)

enum class TaskStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}

data class TaskResult(
    val taskId: String,
    val success: Boolean,
    val result: String? = null,
    val error: String? = null,
    val processingTimeMs: Long
)
```

---

## Step 3: Coroutine Services

```kotlin
// service/AsyncService.kt
package com.learning.service

import com.learning.model.Task
import com.learning.model.TaskResult
import com.learning.model.TaskStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import java.util.UUID
import kotlin.random.Random

class AsyncService(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    
    suspend fun processTaskAsync(task: Task): TaskResult = withContext(dispatcher) {
        val startTime = System.currentTimeMillis()
        try {
            // Simulate async work (e.g., API call, database operation)
            delay(Random.nextLong(100, 1000))
            
            val result = "Processed task ${task.name} with result ${UUID.randomUUID()}"
            TaskResult(
                taskId = task.id,
                success = true,
                result = result,
                processingTimeMs = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            TaskResult(
                taskId = task.id,
                success = false,
                error = e.message,
                processingTimeMs = System.currentTimeMillis() - startTime
            )
        }
    }
    
    fun processTasksConcurrently(tasks: List<Task>): List<Deferred<TaskResult>> {
        return tasks.map { task ->
            scope.async {
                processTaskAsync(task)
            }
        }
    }
    
    suspend fun awaitAllResults(deferredResults: List<Deferred<TaskResult>>): List<TaskResult> {
        return deferredResults.awaitAll()
    }
}
```

```kotlin
// service/TaskProcessor.kt
package com.learning.service

import com.learning.model.Task
import com.learning.model.TaskResult
import com.learning.model.TaskStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap

class TaskProcessor(
    private val asyncService: AsyncService = AsyncService()
) {
    private val tasks = ConcurrentHashMap<String, Task>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    fun addTask(task: Task): Task {
        tasks[task.id] = task
        return task
    }
    
    fun processTasksFlow(): Flow<TaskResult> = flow {
        val taskList = tasks.values.toList()
        
        taskList.asFlow()
            .map { task ->
                asyncService.processTaskAsync(task)
            }
            .collect { result ->
                emit(result)
            }
    }.flowOn(Dispatchers.IO)
    
    suspend fun processAllTasks(): List<TaskResult> = coroutineScope {
        val deferredResults = asyncService.processTasksConcurrently(tasks.values.toList())
        asyncService.awaitAllResults(deferredResults)
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}
```

---

## Step 4: Main Application

```kotlin
// Main.kt
package com.learning

import com.learning.model.Task
import com.learning.model.TaskStatus
import com.learning.service.AsyncService
import com.learning.service.TaskProcessor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    println("=== Kotlin Coroutines Demo ===")
    
    val asyncService = AsyncService()
    val processor = TaskProcessor(asyncService)
    
    // Create sample tasks
    val tasks = listOf(
        Task("1", "Fetch User Data", "Retrieve user from database"),
        Task("2", "Send Email", "Send notification email"),
        Task("3", "Generate Report", "Create PDF report"),
        Task("4", "Call External API", "Fetch data from REST API")
    )
    
    tasks.forEach { processor.addTask(it) }
    
    // Process tasks concurrently
    println("\nProcessing ${tasks.size} tasks concurrently...")
    val results = processor.processAllTasks()
    
    results.forEach { result ->
        println("Task ${result.taskId}: ${if (result.success) "SUCCESS" else "FAILED"} (${result.processingTimeMs}ms)")
    }
    
    // Demonstrate Flow-based processing
    println("\n--- Flow-based Processing ---")
    processor.processTasksFlow()
        .collect { result ->
            println("Flow result: ${result.taskId} - ${result.result}")
        }
    
    println("\nAll tasks completed!")
}
```

---

## Build Instructions

```bash
cd 25-kotlin-coroutines
mvn clean compile
mvn exec:java -Dexec.mainClass=com.learning.MainKt
```

Alternative using Kotlin directly:
```bash
cd 25-kotlin-coroutines
kotlinc -include-runtime -d app.jar -classpath "kotlinx-coroutines-core.jar" src/main/kotlin/com/learning/Main.kt -library jars/kotlin-stdlib.jar -library jars/kotlinx-coroutines-core.jar
java -cp "app.jar;kotlin-stdlib.jar;kotlinx-coroutines-core.jar" com.learning.MainKt
```

---

# Real-World Project: Real-Time Data Pipeline

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Producer/Consumer patterns with Channels, Flow streaming, Structured concurrency with scope management, Error handling and recovery, Backpressure handling

This comprehensive project implements a real-time data pipeline that processes events from multiple sources, applies transformations, and outputs results to multiple sinks—all using coroutines for efficient async processing.

---

## Complete Implementation

```kotlin
// service/DataPipeline.kt
package com.learning.service

import com.learning.model.Task
import com.learning.model.TaskResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

data class PipelineEvent(
    val id: String,
    val type: String,
    val payload: Map<String, Any>,
    val timestamp: Instant = Instant.now(),
    val metadata: Map<String, Any> = emptyMap()
)

data class ProcessedEvent(
    val originalEvent: PipelineEvent,
    val transforms: List<String>,
    val result: Map<String, Any>,
    val processedAt: Instant = Instant.now()
)

class DataPipeline(
    private val bufferSize: Int = 100
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private val inputChannel = Channel<PipelineEvent>(bufferSize)
    private val outputChannel = Channel<ProcessedEvent>(bufferSize)
    private val errorChannel = Channel<PipelineEvent>(bufferSize)
    
    private val transformers = ConcurrentHashMap<String, suspend (PipelineEvent) -> PipelineEvent>()
    private val sinks = ConcurrentHashMap<String, suspend (ProcessedEvent) -> Unit>()
    
    fun registerTransformer(name: String, transformer: suspend (PipelineEvent) -> PipelineEvent) {
        transformers[name] = transformer
    }
    
    fun registerSink(name: String, sink: suspend (ProcessedEvent) -> Unit) {
        sinks[name] = sink
    }
    
    suspend fun start() = coroutineScope {
        // Producer coroutine
        launch {
            inputChannel.consumeAsFlow()
                .collect { event ->
                    processEvent(event)
                }
        }
        
        // Processor coroutines (parallel processing)
        repeat(transformers.size) { idx ->
            launch {
                inputChannel.consumeAsFlow()
                    .buffer(bufferSize)
                    .collect { event ->
                        processWithTransformers(event)
                    }
            }
        }
        
        // Consumer coroutines
        sinks.forEach { (name, sink) ->
            launch {
                outputChannel.consumeAsFlow()
                    .collect { event ->
                        sink(event)
                    }
            }
        }
    }
    
    private suspend fun processEvent(event: PipelineEvent) {
        try {
            var transformed = event
            transformers.values.forEach { transformer ->
                transformed = transformer(transformed)
            }
            
            val processed = ProcessedEvent(
                originalEvent = transformed,
                transforms = transformers.keys().toList(),
                result = transformed.payload
            )
            
            outputChannel.send(processed)
        } catch (e: Exception) {
            errorChannel.send(event)
        }
    }
    
    private suspend fun processWithTransformers(event: PipelineEvent) {
        var current = event
        val transformList = mutableListOf<String>()
        
        for ((name, transformer) in transformers) {
            current = transformer(current)
            transformList.add(name)
        }
        
        val processed = ProcessedEvent(
            originalEvent = event,
            transforms = transformList,
            result = current.payload
        )
        
        outputChannel.send(processed)
    }
    
    suspend fun sendEvent(event: PipelineEvent) {
        inputChannel.send(event)
    }
    
    fun close() {
        inputChannel.close()
        outputChannel.close()
        errorChannel.close()
        scope.cancel()
    }
}
```

```kotlin
// service/EventProcessor.kt
package com.learning.service

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.Duration
import java.time.Instant

class EventProcessor(
    private val pipeline: DataPipeline,
    private val maxConcurrentEvents: Int = 10
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    init {
        // Register built-in transformers
        pipeline.registerTransformer("enrich") { event ->
            val enrichedPayload = event.payload.toMutableMap()
            enrichedPayload["enriched_at"] = Instant.now().toString()
            enrichedPayload["processor"] = "EventProcessor"
            event.copy(payload = enrichedPayload)
        }
        
        pipeline.registerTransformer("validate") { event ->
            require(event.payload.isNotEmpty()) { "Event payload cannot be empty" }
            event
        }
        
        pipeline.registerTransformer("normalize") { event ->
            val normalizedPayload = event.payload.mapValues { (_, value) ->
                when (value) {
                    is String -> value.lowercase().trim()
                    else -> value
                }
            }
            event.copy(payload = normalizedPayload)
        }
    }
    
    fun processStream(events: Flow<PipelineEvent>): Flow<ProcessedEvent> = flow {
        events.buffer(maxConcurrentEvents)
            .collect { event ->
                pipeline.sendEvent(event)
            }
    }.retry(3) { cause ->
        println("Error processing event: ${cause.message}")
        true
    }
    
    fun processBatch(events: List<PipelineEvent>): List<ProcessedEvent> = runBlocking {
        val results = mutableListOf<ProcessedEvent>()
        
        events.forEach { event ->
            pipeline.sendEvent(event)
        }
        
        results
    }
    
    suspend fun startProcessing(scope: CoroutineScope) = with(scope) {
        pipeline.start()
    }
}
```

```kotlin
// Main.kt (Real-World)
package com.learning

import com.learning.service.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.Instant

fun main() = runBlocking {
    println("=== Real-World Data Pipeline Demo ===\n")
    
    val pipeline = DataPipeline(bufferSize = 50)
    val processor = EventProcessor(pipeline)
    
    // Register custom sinks
    pipeline.registerSink("logging") { event ->
        println("[LOG] Processed: ${event.originalEvent.id}")
    }
    
    pipeline.registerSink("metrics") { event ->
        val durationMs = Duration.between(
            event.originalEvent.timestamp,
            event.processedAt
        ).toMillis()
        println("[METRICS] Event ${event.originalEvent.id} processed in ${durationMs}ms")
    }
    
    // Create sample events
    val events = (1..100).map { i ->
        PipelineEvent(
            id = "event-$i",
            type = "user_action",
            payload = mapOf(
                "user_id" to "user-$i",
                "action" to "CLICK",
                "page" to "/home"
            )
        )
    }
    
    // Process events
    println("Processing ${events.size} events...\n")
    
    val startTime = System.currentTimeMillis()
    
    events.asFlow()
        .collect { event ->
            pipeline.sendEvent(event)
        }
    
    val processedCount = events.size
    val duration = System.currentTimeMillis() - startTime
    
    println("\nProcessed $processedCount events in ${duration}ms")
    println("Throughput: ${processedCount * 1000 / duration} events/sec")
    
    pipeline.close()
    
    println("\nPipeline demo completed!")
}
```

---

## Advanced: Flow-Based Aggregation

```kotlin
// service/AggregationService.kt
package com.learning.service

import kotlinx.coroutines.flow.*
import java.time.Duration
import java.time.Instant

class AggregationService {
    
    fun aggregateByKey(
        events: Flow<PipelineEvent>,
        key: String,
        window: Duration
    ): Flow<Map<String, Any>> = events
        .window(window)
        .map { windowedEvents ->
            windowedEvents.groupBy { it.payload[key]?.toString() ?: "unknown" }
                .mapValues { (_, events) ->
                    mapOf(
                        "count" to events.size,
                        "first" to events.firstOrNull(),
                        "last" to events.lastOrNull()
                    )
                }
        }
    
    fun computeMetrics(
        events: Flow<PipelineEvent>
    ): Flow<Map<String, Any>> = events
        .groupBy { it.type }
        .map { (type, typeEvents) ->
            mapOf(
                "type" to type,
                "count" to typeEvents.cou nt(),
                "timestamp" to Instant.now().toString()
            )
        }
}
```

---

## Build Instructions (Real-World Project)

```bash
cd 25-kotlin-coroutines
mvn clean compile
mvn exec:java -Dexec.mainClass=com.learning.MainKt -Dexec.cleanupDaemonThreads=false
```

To build as standalone JAR:
```bash
cd 25-kotlin-coroutines
mvn package -DskipTests
java -jar target/kotlin-coroutines-demo-1.0-SNAPSHOT.jar
```

---

## Key Differences: Java vs Kotlin Coroutines

| Java Concurrency | Kotlin Coroutines |
|------------------|-------------------|
| ThreadPoolExecutor | CoroutineDispatcher |
| Callable/Runnable | suspend functions |
| Future | Deferred |
| CompletableFuture | async/await |
| ExecutorService | CoroutineScope |
| synchronized blocks | Structured concurrency |
| BlockingQueue | Channel |
| Producer-Consumer | Flow |

---

## Summary

This module demonstrates:
1. **Basic coroutines**: Using `runBlocking`, `launch`, and `async` for concurrent programming
2. **Flow**: Processing streams of data with backpressure handling
3. **Channels**: Producer-consumer patterns with bounded buffers
4. **Structured concurrency**: Proper scope management and cancellation
5. **Real-world patterns**: Data pipelines, transformation chains, and aggregation

The concepts learned here directly apply to building reactive applications, event-driven systems, and high-performance async services in Kotlin and Java interoperability scenarios.