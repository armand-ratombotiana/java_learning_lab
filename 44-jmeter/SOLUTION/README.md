# JMeter Solution

## Overview
This module covers load testing with Apache JMeter.

## Key Features

### Test Elements
- HTTP Samplers
- Thread Groups
- Test Plans

### Configuration
- CSV Data Sets
- Header Managers
- Cookie Managers

### Variables
- User-defined variables
- Property functions
- Variable extraction

## Usage

```java
JMeterSolution solution = new JMeterSolution();

// Create HTTP sampler
HTTPSamplerProxy sampler = solution.createHttpSampler(
    "API Test", "api.example.com", "/users", "GET"
);

// Create thread group
ThreadGroup tg = solution.createThreadGroup("load-test", 100, 60, 10);

// Create test plan
TestPlan plan = solution.createTestPlan("My Test Plan", true);

// CSV data set
CSVDataSet csv = solution.createCsvDataSet("users.csv", "username,password", ",");
```

## Dependencies
- Apache JMeter API
- JUnit 5