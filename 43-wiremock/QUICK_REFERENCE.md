# 43 - WireMock Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Stub | Request/response mapping |
| Mapping | URL + method + response configuration |
| Verification | Assert request was made |
| State | Behavior based on call count |
| Fault Injection | Simulate errors, latency |

## Commands

```bash
# Run tests
cd wiremock-service
mvn test

# Start standalone server
java -jar wiremock-standalone-3.0.1.jar --port 8080
```

## Common Patterns

```java
// Stub creation
stubFor(get(urlEqualTo("/api/resource"))
    .willReturn(aResponse()
        .withStatus(200)
        .withBody("{\"id\":1}")));

// With headers
stubFor(post(urlEqualTo("/api/create"))
    .withHeader("Content-Type", equalTo("application/json"))
    .willReturn(created()));

// Delay simulation
stubFor(get("/api/slow")
    .willReturn(ok())
    .withFixedDelay(2000));

// Error response
stubFor(get("/api/error")
    .willReturn(serverError()));

// Stateful behavior
stubFor(get("/api/state")
    .inScenario("State Test")
    .willSetStateTo("Step1")
    .willReturn(ok("Step 1")));

stubFor(get("/api/state")
    .inScenario("State Test")
    .whenScenarioStateIs("Step1")
    .willReturn(ok("Step 2")));

// Verification
verify(postRequestedFor(urlEqualTo("/api/create"))
    .withHeader("Content-Type", equalTo("application/json")));
```

## Mapping JSON

```json
{
  "request": {
    "method": "GET",
    "urlPath": "/api/data"
  },
  "response": {
    "status": 200,
    "jsonBody": {
      "status": "success"
    }
  }
}
```