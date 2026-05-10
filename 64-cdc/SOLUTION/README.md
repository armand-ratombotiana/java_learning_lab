# CDC Solution

## Overview
This module covers Debezium and CDC patterns.

## Key Features

### CDC Configuration
- Creating configurations
- MySQL configuration
- PostgreSQL configuration
- MongoDB configuration

### CDC Events
- Insert events
- Update events
- Delete events
- Before/after data

### CDC Processor
- Processing events
- Event handlers
- Operation-specific handling

### Debezium Connector
- Starting connector
- Stopping connector
- Status checks

### Snapshot Modes
- Initial snapshot
- Schema only
- Always
- Never

## Usage

```java
CDCSolution solution = new CDCSolution();

// Create MySQL config
CDCConfig config = solution.mysqlConfig()
    .database("localhost", 3306, "mydb")
    .table("users");

// Add snapshot mode
config = solution.withSnapshotMode(config, solution.initial());

// Create connector
DebeziumConnector connector = solution.createConnector(config);
connector.start();

// Process events
CDCProcessor processor = solution.createProcessor()
    .onInsert(event -> System.out.println("Insert: " + event.getAfter()))
    .onUpdate(event -> System.out.println("Update: " + event.getAfter()))
    .onDelete(event -> System.out.println("Delete: " + event.getBefore()));

CDCEvent event = solution.createInsertEvent("users", Map.of("id", 1, "name", "John"));
processor.process(event);
```

## Dependencies
- Debezium
- JUnit 5