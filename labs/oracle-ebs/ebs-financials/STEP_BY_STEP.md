# Step by Step: EBS Financials

## Step 1: Setup Directory Structure

mkdir -p src/main/java/com/oracleebs/financials

mkdir -p src/test/java/com/oracleebs/financials

## Step 2: Create Java Package

Create records and classes for the data model:

public record Request(String id, String type, Map attributes) {}

## Step 3: Implement Processor

Implement core processing with validation, business logic, and result generation.

## Step 4: Add Error Handling

Wrap processing in try-catch for controlled error handling.

## Step 5: Write JUnit 5 Tests

@Test void testHappyPath() { ... }

## Step 6: Run Tests

mvn test

## Step 7: Review

Check coverage, fix failures, refactor as needed.
