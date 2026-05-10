# Liquibase Solution

## Overview
This module covers database refactoring with Liquibase.

## Key Features

### Changes
- Create table
- Add column
- Modify column

### Operations
- Update
- Rollback
- Tag

### Management
- Change sets
- Contexts
- Labels

## Usage

```java
LiquibaseSolution solution = new LiquibaseSolution();

// Create Liquibase
DataSource ds = ...;
Liquibase liquibase = solution.createLiquibase(ds);

// Update
solution.update(liquibase);

// Rollback
solution.rollback(liquibase, "v1.0");

// Tag
solution.tag(liquibase, "release-1.0");

// Validate
solution.validate(liquibase);

// Create changes
CreateTableChange table = solution.createTable("users", "id", "name");
```

## Dependencies
- Liquibase Core
- JUnit 5
- Mockito