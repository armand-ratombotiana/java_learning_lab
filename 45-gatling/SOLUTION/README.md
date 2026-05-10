# Gatling Solution

## Overview
This module covers performance testing with Gatling.

## Key Features

### HTTP Requests
- GET, POST, PUT, DELETE
- Headers and body
- Response checks

### Scenarios
- Chain builders
- Session management
- Feeders

### Load Simulation
- User injection
- Ramp-up periods
- Duration

## Usage

```java
GatlingSolution solution = new GatlingSolution();

// Create requests
HttpRequestBuilder getReq = solution.createGetRequest("http://api.example.com/users");
HttpRequestBuilder postReq = solution.createPostRequest(
    "http://api.example.com/users",
    "{\"name\":\"test\"}"
);

// Create scenario
ChainBuilder chain = solution.createScenario("Test", getReq);
ScenarioBuilder scenario = solution.createLoadScenario("Load Test", chain);

// Create simulation
var population = solution.createSimulation(scenario, 100, 60);
```

## Dependencies
- Gatling Core
- Gatling HTTP
- JUnit 5