# Code Deep Dive: EBS Financials

## 1. Package Overview

Location: src/main/java/com/oracleebs/

### Source Files

Three Java classes simulate EBS components for EBS Financials. Each class contains: business logic methods, configuration management, and error handling.

## 2. Design Patterns Used

- Strategy Pattern for pluggable processing logic
- Factory Pattern for creating processors based on configuration
- Observer Pattern for event notification

## 3. Data Flow

Request -> Validator -> Processor -> PostProcessor -> Result

## 4. Unit Tests

Located in src/test/java/com/oracleebs/. Uses JUnit 5 with assertions covering happy path, validation errors, edge cases, and concurrent access.

## 5. Key Implementation Details

- Record types for immutable data transfer
- ConcurrentHashMap for thread-safe storage
- Optional for null-safe returns
- java.time API for date operations
- BigDecimal for precise financial calculations
