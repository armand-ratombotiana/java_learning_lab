# Axon Framework Projects

This directory contains hands-on projects using Axon Framework, a powerful framework for building CQRS (Command Query Responsibility Segregation) and Event Sourcing applications in Java. Axon provides infrastructure for domain-driven design patterns.

## Project Overview

Axon Framework simplifies the implementation of complex domain models by providing abstractions for aggregates, commands, events, and queries. This module covers two projects demonstrating CQRS and Event Sourcing patterns.

---

# Mini-Project: Task Management with CQRS (2-4 Hours)

## Project Description

Build a task management application using Axon Framework with CQRS pattern. This project demonstrates command handling, event sourcing, and query model updates through Axon.

## Technologies Used

- Axon Framework 4.9.0
- Axon Server (embedded)
- Java 21
- Maven
- Spring Boot

## Implementation Steps

### Step 1: Create Project Structure

```bash
mkdir axon-task-management
cd axon-task-management
mkdir -p src/main/java/com/learning/task/{command,query,model,event,repository}
mkdir -p src/main/resources
mkdir -p src/test/java/com/learning/task
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>axon-task-management</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Axon Task Management</name>
    <description>Task management with CQRS and Axon Framework</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <axon.version>4.9.0</axon.version>
        <spring-boot.version>3.2.0</spring-boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.axonframework</groupId>
            <artifactId>axon-spring-boot-starter</artifactId>
            <version>${axon.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Task Aggregate

```java
package com.learning.task.command;

import com.learning.task.event.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Aggregate
public class TaskAggregate {
    
    @AggregateIdentifier
    private String taskId;
    
    private String title;
    private String description;
    private String assignee;
    private TaskStatus status;
    private int priority;
    private LocalDateTime dueDate;
    private List<String> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public TaskAggregate() {
        this.status = TaskStatus.OPEN;
        this.comments = new ArrayList<>();
    }
    
    @CommandHandler
    public TaskAggregate(CreateTaskCommand command) {
        AggregateLifecycle.apply(new TaskCreatedEvent(
            command.getTaskId(),
            command.getTitle(),
            command.getDescription(),
            command.getAssignee(),
            command.getPriority(),
            command.getDueDate()
        ));
    }
    
    @CommandHandler
    public void handle(UpdateTaskCommand command) {
        AggregateLifecycle.apply(new TaskUpdatedEvent(
            taskId,
            command.getTitle(),
            command.getDescription(),
            command.getPriority()
        ));
    }
    
    @CommandHandler
    public void handle(AssignTaskCommand command) {
        if (assignee != null && !assignee.equals(command.getAssignee())) {
            AggregateLifecycle.apply(new TaskAssignedEvent(
                taskId,
                command.getAssignee()
            ));
        }
    }
    
    @CommandHandler
    public void handle(CompleteTaskCommand command) {
        if (status != TaskStatus.COMPLETED) {
            AggregateLifecycle.apply(new TaskCompletedEvent(taskId));
        }
    }
    
    @CommandHandler
    public void handle(ReopenTaskCommand command) {
        if (status == TaskStatus.COMPLETED) {
            AggregateLifecycle.apply(new TaskReopenedEvent(taskId));
        }
    }
    
    @CommandHandler
    public void handle(AddCommentCommand command) {
        AggregateLifecycle.apply(new CommentAddedEvent(
            taskId,
            command.getComment()
        ));
    }
    
    @CommandHandler
    public void handle(DeleteTaskCommand command) {
        AggregateLifecycle.apply(new TaskDeletedEvent(taskId));
    }
    
    @EventSourcingHandler
    public void on(TaskCreatedEvent event) {
        this.taskId = event.getTaskId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.assignee = event.getAssignee();
        this.priority = event.getPriority();
        this.dueDate = event.getDueDate();
        this.status = TaskStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(TaskUpdatedEvent event) {
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.priority = event.getPriority();
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(TaskAssignedEvent event) {
        this.assignee = event.getAssignee();
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(TaskCompletedEvent event) {
        this.status = TaskStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(TaskReopenedEvent event) {
        this.status = TaskStatus.OPEN;
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(CommentAddedEvent event) {
        this.comments.add(event.getComment());
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(TaskDeletedEvent event) {
        AggregateLifecycle.markDeleted();
    }
    
    public enum TaskStatus {
        OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
```

### Step 4: Create Command Classes

```java
package com.learning.task.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.time.LocalDateTime;

public class CreateTaskCommand {
    
    @TargetAggregateIdentifier
    private String taskId;
    private String title;
    private String description;
    private String assignee;
    private int priority;
    private LocalDateTime dueDate;
    
    public CreateTaskCommand() {}
    
    public CreateTaskCommand(String taskId, String title, String description,
                         String assignee, int priority, LocalDateTime dueDate) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.priority = priority;
        this.dueDate = dueDate;
    }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}
```

```java
package com.learning.task.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class UpdateTaskCommand {
    
    @TargetAggregateIdentifier
    private String taskId;
    private String title;
    private String description;
    private int priority;
    
    public UpdateTaskCommand() {}
    
    public UpdateTaskCommand(String taskId, String title, String description, int priority) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}
```

```java
package com.learning.task.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class AssignTaskCommand {
    
    @TargetAggregateIdentifier
    private String taskId;
    private String assignee;
    
    public AssignTaskCommand() {}
    
    public AssignTaskCommand(String taskId, String assignee) {
        this.taskId = taskId;
        this.assignee = assignee;
    }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
}
```

```java
package com.learning.task.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CompleteTaskCommand {
    
    @TargetAggregateIdentifier
    private String taskId;
    
    public CompleteTaskCommand() {}
    
    public CompleteTaskCommand(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
}
```

```java
package com.learning.task.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ReopenTaskCommand {
    
    @TargetAggregateIdentifier
    private String taskId;
    
    public ReopenTaskCommand() {}
    
    public ReopenTaskCommand(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
}
```

```java
package com.learning.task.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class AddCommentCommand {
    
    @TargetAggregateIdentifier
    private String taskId;
    private String comment;
    
    public AddCommentCommand() {}
    
    public AddCommentCommand(String taskId, String comment) {
        this.taskId = taskId;
        this.comment = comment;
    }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
```

```java
package com.learning.task.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class DeleteTaskCommand {
    
    @TargetAggregateIdentifier
    private String taskId;
    
    public DeleteTaskCommand() {}
    
    public DeleteTaskCommand(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
}
```

### Step 5: Create Event Classes

```java
package com.learning.task.event;

import java.time.LocalDateTime;

public class TaskCreatedEvent {
    
    private String taskId;
    private String title;
    private String description;
    private String assignee;
    private int priority;
    private LocalDateTime dueDate;
    
    public TaskCreatedEvent() {}
    
    public TaskCreatedEvent(String taskId, String title, String description,
                       String assignee, int priority, LocalDateTime dueDate) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.priority = priority;
        this.dueDate = dueDate;
    }
    
    public String getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAssignee() { return assignee; }
    public int getPriority() { return priority; }
    public LocalDateTime getDueDate() { return dueDate; }
}
```

```java
package com.learning.task.event;

public class TaskUpdatedEvent {
    
    private String taskId;
    private String title;
    private String description;
    private int priority;
    
    public TaskUpdatedEvent() {}
    
    public TaskUpdatedEvent(String taskId, String title, String description, int priority) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
    
    public String getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPriority() { return priority; }
}
```

```java
package com.learning.task.event;

public class TaskAssignedEvent {
    
    private String taskId;
    private String assignee;
    
    public TaskAssignedEvent() {}
    
    public TaskAssignedEvent(String taskId, String assignee) {
        this.taskId = taskId;
        this.assignee = assignee;
    }
    
    public String getTaskId() { return taskId; }
    public String getAssignee() { return assignee; }
}
```

```java
package com.learning.task.event;

public class TaskCompletedEvent {
    
    private String taskId;
    
    public TaskCompletedEvent() {}
    
    public TaskCompletedEvent(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskId() { return taskId; }
}
```

```java
package com.learning.task.event;

public class TaskReopenedEvent {
    
    private String taskId;
    
    public TaskReopenedEvent() {}
    
    public TaskReopenedEvent(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskId() { return taskId; }
}
```

```java
package com.learning.task.event;

public class CommentAddedEvent {
    
    private String taskId;
    private String comment;
    
    public CommentAddedEvent() {}
    
    public CommentAddedEvent(String taskId, String comment) {
        this.taskId = taskId;
        this.comment = comment;
    }
    
    public String getTaskId() { return taskId; }
    public String getComment() { return comment; }
}
```

```java
package com.learning.task.event;

public class TaskDeletedEvent {
    
    private String taskId;
    
    public TaskDeletedEvent() {}
    
    public TaskDeletedEvent(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskId() { return taskId; }
}
```

### Step 6: Create Query Model

```java
package com.learning.task.query;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_query_model")
public class TaskQueryModel {
    
    @Id
    private String taskId;
    
    private String title;
    private String description;
    private String assignee;
    private String status;
    private int priority;
    private LocalDateTime dueDate;
    
    @ElementCollection
    @CollectionTable(name = "task_comments", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "comment")
    private List<String> comments = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public List<String> getComments() { return comments; }
    public void setComments(List<String> comments) { this.comments = comments; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

### Step 7: Create Event Handlers

```java
package com.learning.task.query;

import com.learning.task.event.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TaskEventListener {
    
    private final TaskQueryRepository repository;
    
    public TaskEventListener(TaskQueryRepository repository) {
        this.repository = repository;
    }
    
    @EventHandler
    public void on(TaskCreatedEvent event) {
        TaskQueryModel model = new TaskQueryModel();
        model.setTaskId(event.getTaskId());
        model.setTitle(event.getTitle());
        model.setDescription(event.getDescription());
        model.setAssignee(event.getAssignee());
        model.setStatus("OPEN");
        model.setPriority(event.getPriority());
        model.setDueDate(event.getDueDate());
        model.setCreatedAt(java.time.LocalDateTime.now());
        model.setUpdatedAt(java.time.LocalDateTime.now());
        
        repository.save(model);
    }
    
    @EventHandler
    public void on(TaskUpdatedEvent event) {
        repository.findById(event.getTaskId()).ifPresent(model -> {
            model.setTitle(event.getTitle());
            model.setDescription(event.getDescription());
            model.setPriority(event.getPriority());
            model.setUpdatedAt(java.time.LocalDateTime.now());
            repository.save(model);
        });
    }
    
    @EventHandler
    public void on(TaskAssignedEvent event) {
        repository.findById(event.getTaskId()).ifPresent(model -> {
            model.setAssignee(event.getAssignee());
            model.setUpdatedAt(java.time.LocalDateTime.now());
            repository.save(model);
        });
    }
    
    @EventHandler
    public void on(TaskCompletedEvent event) {
        repository.findById(event.getTaskId()).ifPresent(model -> {
            model.setStatus("COMPLETED");
            model.setUpdatedAt(java.time.LocalDateTime.now());
            repository.save(model);
        });
    }
    
    @EventHandler
    public void on(TaskReopenedEvent event) {
        repository.findById(event.getTaskId()).ifPresent(model -> {
            model.setStatus("OPEN");
            model.setUpdatedAt(java.time.LocalDateTime.now());
            repository.save(model);
        });
    }
    
    @EventHandler
    public void on(CommentAddedEvent event) {
        repository.findById(event.getTaskId()).ifPresent(model -> {
            model.getComments().add(event.getComment());
            model.setUpdatedAt(java.time.LocalDateTime.now());
            repository.save(model);
        });
    }
    
    @EventHandler
    public void on(TaskDeletedEvent event) {
        repository.deleteById(event.getTaskId());
    }
}
```

### Step 8: Create Query Repository

```java
package com.learning.task.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskQueryRepository extends JpaRepository<TaskQueryModel, String> {
}
```

### Step 9: Create REST Controllers

```java
package com.learning.task.command;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskCommandController {
    
    private final CommandGateway commandGateway;
    
    public TaskCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }
    
    @PostMapping
    public String createTask(@RequestBody CreateTaskRequest request) {
        String taskId = UUID.randomUUID().toString();
        
        CreateTaskCommand command = new CreateTaskCommand(
            taskId,
            request.title(),
            request.description(),
            request.assignee(),
            request.priority(),
            request.dueDate()
        );
        
        commandGateway.send(command);
        return taskId;
    }
    
    @PutMapping("/{taskId}")
    public void updateTask(@PathVariable String taskId, @RequestBody UpdateTaskRequest request) {
        UpdateTaskCommand command = new UpdateTaskCommand(
            taskId,
            request.title(),
            request.description(),
            request.priority()
        );
        commandGateway.send(command);
    }
    
    @PatchMapping("/{taskId}/assign/{assignee}")
    public void assignTask(@PathVariable String taskId, @PathVariable String assignee) {
        AssignTaskCommand command = new AssignTaskCommand(taskId, assignee);
        commandGateway.send(command);
    }
    
    @PatchMapping("/{taskId}/complete")
    public void completeTask(@PathVariable String taskId) {
        CompleteTaskCommand command = new CompleteTaskCommand(taskId);
        commandGateway.send(command);
    }
    
    @PatchMapping("/{taskId}/reopen")
    public void reopenTask(@PathVariable String taskId) {
        ReopenTaskCommand command = new ReopenTaskCommand(taskId);
        commandGateway.send(command);
    }
    
    @PostMapping("/{taskId}/comments")
    public void addComment(@PathVariable String taskId, @RequestBody AddCommentRequest request) {
        AddCommentCommand command = new AddCommentCommand(taskId, request.comment());
        commandGateway.send(command);
    }
    
    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable String taskId) {
        DeleteTaskCommand command = new DeleteTaskCommand(taskId);
        commandGateway.send(command);
    }
    
    record CreateTaskRequest(String title, String description, String assignee, int priority, java.time.LocalDateTime dueDate) {}
    record UpdateTaskRequest(String title, String description, int priority) {}
    record AddCommentRequest(String comment) {}
}
```

```java
package com.learning.task.query;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskQueryController {
    
    private final TaskQueryRepository repository;
    
    public TaskQueryController(TaskQueryRepository repository) {
        this.repository = repository;
    }
    
    @GetMapping
    public List<TaskQueryModel> getAllTasks() {
        return repository.findAll();
    }
    
    @GetMapping("/{taskId}")
    public TaskQueryModel getTask(@PathVariable String taskId) {
        return repository.findById(taskId).orElse(null);
    }
    
    @GetMapping("/assignee/{assignee}")
    public List<TaskQueryModel> getTasksByAssignee(@PathVariable String assignee) {
        return repository.findByAssignee(assignee);
    }
    
    @GetMapping("/status/{status}")
    public List<TaskQueryModel> getTasksByStatus(@PathVariable String status) {
        return repository.findByStatus(status);
    }
}
```

### Step 10: Configure Application

Create `src/main/resources/application.yaml`:

```yaml
server:
  port: 8080

axon:
  server:
    port: 8124

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create
```

### Step 11: Run and Test

```bash
# Build project
cd axon-task-management
mvn clean package

# Run application
java -jar target/axon-task-management-1.0.0.jar

# Test API
# Create task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Axon",
    "description": "Build CQRS application",
    "assignee": "developer",
    "priority": 1,
    "dueDate": "2024-12-31T23:59:59"
  }'

# Get all tasks
curl http://localhost:8080/api/tasks

# Get specific task
curl http://localhost:8080/api/tasks/{taskId}

# Assign task
curl -X PATCH http://localhost:8080/api/tasks/{taskId}/assign/john

# Complete task
curl -X PATCH http://localhost:8080/api/tasks/{taskId}/complete

# Add comment
curl -X POST http://localhost:8080/api/tasks/{taskId}/comments \
  -H "Content-Type: application/json" \
  -d '{"comment": "Working on this task"}'
```

## Expected Output

The mini-project produces:
- Task aggregate with event sourcing
- Command handlers for CRUD operations
- Separate query model (read model)
- Event handlers updating query model

---

# Real-World Project: E-Commerce Order System with Event Sourcing (8+ Hours)

## Project Description

Build a complete e-commerce order management system using Axon Framework with full event sourcing, sagas for orchestration, and query projections.

## Architecture

```
┌──────────────────────────────────────────────────────┐
│              Axon Framework Server                    │
├──────────────────────────────────────────────────────┤
│  Commands → Aggregate → Events                        │
│         │                                           │
│         ▼                                          │
│  Event Handlers → Query Models                       │
│         │                                           │
│         ▼                                          │
│  Sagas → Orchestration                               │
└──────────────────────────────────────────────────────┘
```

## Implementation Steps

### Step 1: Create Order Service Project

```bash
mkdir axon-order-system
cd axon-order-system
mkdir -p src/main/java/com/learning/order/{command,query,model,event,saga,repository,service}
mkdir -p src/main/resources
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>axon-order-system</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Axon Order System</name>
    <description>E-Commerce order system with event sourcing</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <axon.version>4.9.0</axon.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.axonframework</groupId>
            <artifactId>axon-spring-boot-starter</artifactId>
            <version>${axon.version}</version>
        </dependency>
        <dependency>
            <groupId>org.axonframework</groupId>
            <artifactId>axon-modelling</artifactId>
            <version>${axon.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>3.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>3.2.0</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Order Aggregate

```java
package com.learning.order.command;

import com.learning.order.event.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Aggregate
public class OrderAggregate {
    
    @AggregateIdentifier
    private String orderId;
    
    private String customerId;
    private String customerEmail;
    private BigDecimal totalAmount;
    private OrderState state;
    private String shippingAddress;
    private String paymentId;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public OrderAggregate() {
        this.items = new ArrayList<>();
        this.state = OrderState.CREATED;
    }
    
    @CommandHandler
    public OrderAggregate(PlaceOrderCommand command) {
        validateItems(command.getItems());
        
        AggregateLifecycle.apply(new OrderPlacedEvent(
            command.getOrderId(),
            command.getCustomerId(),
            command.getCustomerEmail(),
            command.getItems(),
            command.getShippingAddress(),
            command.getTotalAmount()
        ));
    }
    
    @CommandHandler
    public void handle(ConfirmOrderCommand command) {
        if (state == OrderState.CREATED) {
            AggregateLifecycle.apply(new OrderConfirmedEvent(orderId));
        }
    }
    
    @CommandHandler
    public void handle(ProcessPaymentCommand command) {
        if (state == OrderState.CONFIRMED) {
            AggregateLifecycle.apply(new PaymentProcessedEvent(
                orderId,
                command.getPaymentId()
            ));
        }
    }
    
    @CommandHandler
    public void handle(ShipOrderCommand command) {
        if (state == OrderState.PAID) {
            AggregateLifecycle.apply(new OrderShippedEvent(
                orderId,
                command.getTrackingNumber()
            ));
        }
    }
    
    @CommandHandler
    public void handle(DeliverOrderCommand command) {
        if (state == OrderState.SHIPPED) {
            AggregateLifecycle.apply(new OrderDeliveredEvent(orderId));
        }
    }
    
    @CommandHandler
    public void handle(CancelOrderCommand command) {
        if (state != OrderState.DELIVERED && state != OrderState.CANCELLED) {
            AggregateLifecycle.apply(new OrderCancelledEvent(
                orderId,
                command.getReason()
            ));
        }
    }
    
    private void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
    }
    
    @EventSourcingHandler
    public void on(OrderPlacedEvent event) {
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.customerEmail = event.getCustomerEmail();
        this.items = event.getItems();
        this.shippingAddress = event.getShippingAddress();
        this.totalAmount = event.getTotalAmount();
        this.state = OrderState.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(OrderConfirmedEvent event) {
        this.state = OrderState.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.paymentId = event.getPaymentId();
        this.state = OrderState.PAID;
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(OrderShippedEvent event) {
        this.state = OrderState.SHIPPED;
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(OrderDeliveredEvent event) {
        this.state = OrderState.DELIVERED;
        this.updatedAt = LocalDateTime.now();
    }
    
    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.state = OrderState.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum OrderState {
        CREATED, CONFIRMED, PAID, SHIPPED, DELIVERED, CANCELLED
    }
    
    public static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
        
        public OrderItem() {}
        
        public OrderItem(String productId, String productName, int quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
    }
}
```

### Step 4: Create Commands

```java
package com.learning.order.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.math.BigDecimal;
import java.util.List;

public class PlaceOrderCommand {
    
    @TargetAggregateIdentifier
    private String orderId;
    private String customerId;
    private String customerEmail;
    private List<OrderAggregate.OrderItem> items;
    private String shippingAddress;
    private BigDecimal totalAmount;
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public List<OrderAggregate.OrderItem> getItems() { return items; }
    public void setItems(List<OrderAggregate.OrderItem> items) { this.items = items; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
```

```java
package com.learning.order.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ConfirmOrderCommand {
    
    @TargetAggregateIdentifier
    private String orderId;
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
}
```

```java
package com.learning.order.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ProcessPaymentCommand {
    
    @TargetAggregateIdentifier
    private String orderId;
    private String paymentId;
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}
```

```java
package com.learning.order.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ShipOrderCommand {
    
    @TargetAggregateIdentifier
    private String orderId;
    private String trackingNumber;
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
}
```

```java
package com.learning.order.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CancelOrderCommand {
    
    @TargetAggregateIdentifier
    private String orderId;
    private String reason;
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
```

### Step 5: Create Events

```java
package com.learning.order.event;

import java.math.BigDecimal;
import java.util.List;

public class OrderPlacedEvent {
    
    private String orderId;
    private String customerId;
    private String customerEmail;
    private List<OrderAggregate.OrderItem> items;
    private String shippingAddress;
    private BigDecimal totalAmount;
    
    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerEmail() { return customerEmail; }
    public List<OrderAggregate.OrderItem> getItems() { return items; }
    public String getShippingAddress() { return shippingAddress; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}
```

```java
package com.learning.order.event;

public class OrderConfirmedEvent {
    private String orderId;
    public String getOrderId() { return orderId; }
}
```

```java
package com.learning.order.event;

public class PaymentProcessedEvent {
    private String orderId;
    private String paymentId;
    
    public PaymentProcessedEvent(String orderId, String paymentId) {
        this.orderId = orderId;
        this.paymentId = paymentId;
    }
    
    public String getOrderId() { return orderId; }
    public String getPaymentId() { return paymentId; }
}
```

```java
package com.learning.order.event;

public class OrderShippedEvent {
    private String orderId;
    private String trackingNumber;
    
    public OrderShippedEvent(String orderId, String trackingNumber) {
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
    }
    
    public String getOrderId() { return orderId; }
    public String getTrackingNumber() { return trackingNumber; }
}
```

```java
package com.learning.order.event;

public class OrderDeliveredEvent {
    private String orderId;
    public String getOrderId() { return orderId; }
}
```

```java
package com.learning.order.event;

public class OrderCancelledEvent {
    private String orderId;
    private String reason;
    
    public OrderCancelledEvent(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }
    
    public String getOrderId() { return orderId; }
    public String getReason() { return reason; }
}
```

### Step 6: Create Order Saga

```java
package com.learning.order.saga;

import com.learning.order.command.*;
import com.learning.order.event.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.saga.SagaEventHandler;
import org.axonframework.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import java.util.UUID;

@Saga("order-saga")
public class OrderSaga {
    
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderPlacedEvent event) {
        String confirmId = UUID.randomUUID().toString();
        
        ConfirmOrderCommand command = new ConfirmOrderCommand();
        command.setOrderId(event.getOrderId());
        
        org.axonframework.commandhandling.gateway.CommandGateway gateway = 
            org.axonframework.SpringVariableCommandGateway.builder()
                .build();
        gateway.send(command);
    }
    
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderConfirmedEvent event) {
        ProcessPaymentCommand command = new ProcessPaymentCommand();
        command.setOrderId(event.getOrderId());
        command.setPaymentId("PAY-" + UUID.randomUUID().toString());
        
        org.axonframework.commandhandling.gateway.CommandGateway gateway = 
            org.axonframework.SpringVariableCommandGateway.builder()
                .build();
        gateway.send(command);
    }
    
    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentProcessedEvent event) {
        ShipOrderCommand command = new ShipOrderCommand();
        command.setOrderId(event.getOrderId());
        command.setTrackingNumber("TRACK-" + UUID.randomUUID().toString().substring(0, 8));
        
        org.axonframework.commandhandling.gateway.CommandGateway gateway = 
            org.axonframework.SpringVariableCommandGateway.builder()
                .build();
        gateway.send(command);
    }
}
```

### Step 7: Create Query Models

```java
package com.learning.order.query;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_query_model")
public class OrderQueryModel {
    
    @Id
    private String orderId;
    
    private String customerId;
    private String customerEmail;
    private BigDecimal totalAmount;
    private String state;
    private String shippingAddress;
    private String paymentId;
    private String trackingNumber;
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

```java
package com.learning.order.query;

import com.learning.order.command.OrderAggregate;
import com.learning.order.event.*;
import jakarta.persistence.*;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderEventHandler {
    
    private final OrderQueryRepository repository;
    
    public OrderEventHandler(OrderQueryRepository repository) {
        this.repository = repository;
    }
    
    @EventHandler
    public void on(OrderPlacedEvent event) {
        OrderQueryModel model = new OrderQueryModel();
        model.setOrderId(event.getOrderId());
        model.setCustomerId(event.getCustomerId());
        model.setCustomerEmail(event.getCustomerEmail());
        model.setTotalAmount(event.getTotalAmount());
        model.setState("CREATED");
        model.setShippingAddress(event.getShippingAddress());
        model.setCreatedAt(LocalDateTime.now());
        
        repository.save(model);
    }
    
    @EventHandler
    public void on(OrderConfirmedEvent event) {
        repository.findById(event.getOrderId()).ifPresent(m -> {
            m.setState("CONFIRMED");
            repository.save(m);
        });
    }
    
    @EventHandler
    public void on(PaymentProcessedEvent event) {
        repository.findById(event.getOrderId()).ifPresent(m -> {
            m.setPaymentId(event.getPaymentId());
            m.setState("PAID");
            repository.save(m);
        });
    }
    
    @EventHandler
    public void on(OrderShippedEvent event) {
        repository.findById(event.getOrderId()).ifPresent(m -> {
            m.setTrackingNumber(event.getTrackingNumber());
            m.setState("SHIPPED");
            repository.save(m);
        });
    }
    
    @EventHandler
    public void on(OrderDeliveredEvent event) {
        repository.findById(event.getOrderId()).ifPresent(m -> {
            m.setState("DELIVERED");
            repository.save(m);
        });
    }
    
    @EventHandler
    public void on(OrderCancelledEvent event) {
        repository.findById(event.getOrderId()).ifPresent(m -> {
            m.setState("CANCELLED");
            repository.save(m);
        });
    }
}
```

```java
package com.learning.order.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderQueryRepository extends JpaRepository<OrderQueryModel, String> {
}
```

### Step 8: Create Controllers

```java
package com.learning.order.service;

import com.learning.order.command.*;
import com.learning.order.query.OrderQueryModel;
import com.learning.order.query.OrderQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final CommandGateway commandGateway;
    private final OrderQueryRepository queryRepository;
    
    public OrderController(CommandGateway commandGateway, OrderQueryRepository queryRepository) {
        this.commandGateway = commandGateway;
        this.queryRepository = queryRepository;
    }
    
    @PostMapping
    public String placeOrder(@RequestBody PlaceOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        
        PlaceOrderCommand command = new PlaceOrderCommand();
        command.setOrderId(orderId);
        command.setCustomerId(request.customerId());
        command.setCustomerEmail(request.customerEmail());
        command.setItems(request.items().stream()
            .map(i -> new OrderAggregate.OrderItem(
                i.productId(),
                i.productName(),
                i.quantity(),
                i.unitPrice()
            ))
            .collect(Collectors.toList()));
        command.setShippingAddress(request.shippingAddress());
        command.setTotalAmount(calculateTotal(request.items()));
        
        commandGateway.send(command);
        return orderId;
    }
    
    @GetMapping
    public List<OrderQueryModel> getAllOrders() {
        return queryRepository.findAll();
    }
    
    @GetMapping("/{orderId}")
    public OrderQueryModel getOrder(@PathVariable String orderId) {
        return queryRepository.findById(orderId).orElse(null);
    }
    
    @GetMapping("/customer/{customerId}")
    public List<OrderQueryModel> getOrdersByCustomer(@PathVariable String customerId) {
        return queryRepository.findByCustomerId(customerId);
    }
    
    @PostMapping("/{orderId}/confirm")
    public void confirmOrder(@PathVariable String orderId) {
        ConfirmOrderCommand command = new ConfirmOrderCommand();
        command.setOrderId(orderId);
        commandGateway.send(command);
    }
    
    @PostMapping("/{orderId}/payment")
    public void processPayment(@PathVariable String orderId, @RequestBody PaymentRequest request) {
        ProcessPaymentCommand command = new ProcessPaymentCommand();
        command.setOrderId(orderId);
        command.setPaymentId(request.paymentId());
        commandGateway.send(command);
    }
    
    @PostMapping("/{orderId}/ship")
    public void shipOrder(@PathVariable String orderId, @RequestBody ShipRequest request) {
        ShipOrderCommand command = new ShipOrderCommand();
        command.setOrderId(orderId);
        command.setTrackingNumber(request.trackingNumber());
        commandGateway.send(command);
    }
    
    @PostMapping("/{orderId}/cancel")
    public void cancelOrder(@PathVariable String orderId, @RequestBody CancelRequest request) {
        CancelOrderCommand command = new CancelOrderCommand();
        command.setOrderId(orderId);
        command.setReason(request.reason());
        commandGateway.send(command);
    }
    
    private BigDecimal calculateTotal(List<OrderItemRequest> items) {
        return items.stream()
            .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    record PlaceOrderRequest(String customerId, String customerEmail, 
                            List<OrderItemRequest> items, String shippingAddress) {}
    record OrderItemRequest(String productId, String productName, int quantity, BigDecimal unitPrice) {}
    record PaymentRequest(String paymentId) {}
    record ShipRequest(String trackingNumber) {}
    record CancelRequest(String reason) {}
}
```

### Step 9: Configure and Run

Create `src/main/resources/application.yaml`:

```yaml
server:
  port: 8080

axon:
  server:
    port: 8124
```

Build and run:

```bash
cd axon-order-system
mvn clean package
java -jar target/axon-order-system-1.0.0.jar

# Test API
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-001",
    "customerEmail": "customer@example.com",
    "items": [
      {"productId": "P001", "productName": "Laptop", "quantity": 1, "unitPrice": 999.99},
      {"productId": "P002", "productName": "Mouse", "quantity": 2, "unitPrice": 29.99}
    ],
    "shippingAddress": "123 Main St"
  }'
```

## Expected Output

The real-world project produces:
- Full order lifecycle with event sourcing
- Payment and shipping orchestration
- Saga for distributed transactions
- Separate query models for reads

## Build Instructions Summary

| Step | Command | Time |
|------|---------|------|
| Setup project | Maven setup | 15 min |
| Create aggregate | Domain model | 1.5 hours |
| Create commands | Command classes | 1 hour |
| Create events | Event classes | 1 hour |
| Create queries | Query projection | 1 hour |
| Create saga | Orchestration | 2 hours |
| Test integration | Full testing | 3 hours |

Total estimated time: 8-10 hours