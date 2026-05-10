# Flyway Solution

## Overview
This module covers database migrations with Flyway.

## Key Features

### Migration Management
- Schema creation
- Version tracking
- Migration execution

### Configuration
- Multiple schemas
- Custom tables
- Baseline settings

### Operations
- Migrate
- Clean
- Validate
- Repair

## Usage

```java
FlywaySolution solution = new FlywaySolution();

// Create Flyway
DataSource ds = ...;
Flyway flyway = solution.createFlyway(ds);

// Migrate
solution.migrate(flyway);

// Validate
solution.validate(flyway);

// Get pending migrations
List<String> pending = solution.getPendingMigrations(flyway);

// Repair
solution.repair(flyway);
```

## Dependencies
- Flyway Core
- JUnit 5
- Mockito