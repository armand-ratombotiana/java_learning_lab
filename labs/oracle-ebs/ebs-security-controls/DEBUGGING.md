# Debugging: EBS Security Controls

## 1. Common Issues

### 1.1 Null Pointer Exceptions

Check null inputs. Use Objects.requireNonNull().

### 1.2 Configuration Errors

Missing profile options cause runtime failures. Verify seeded values.

### 1.3 Transaction Issues

Ensure commit/rollback boundaries correct. Use try-with-resources.

### 1.4 Stack Trace Analysis

Read bottom to top. Focus on com.oracleebs.* frames.

## 2. Debugging Techniques

### 2.1 Console Logging

System.out.printf for trace messages with timestamps.

### 2.2 JUnit Debugging

Use assertThrows() to verify exception paths.

## 3. Debugging Checklist

- Inputs validated

- Configuration loaded

- Transaction boundaries correct

- Exceptions logged

- No resource leaks

## 4. Failure Simulation

Java simulation includes FailureSimulator for testing resilience.
