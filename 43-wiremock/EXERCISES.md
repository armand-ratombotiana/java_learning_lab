# Exercises - WireMock

## Exercise 1: Basic Stubbing
Mock external HTTP service:

1. Start WireMock server in tests
2. Stub GET request to return JSON
3. Configure response with status code and headers
4. Verify request matching

## Exercise 2: Request Matching
Advanced matching scenarios:

1. Match by URL path patterns
2. Match by query parameters
3. Match by request headers
4. Match by JSON body content

## Exercise 3: Fault Injection
Simulate production failures:

1. Add fixed delay to responses
2. Inject connection reset faults
3. Return HTTP 500 errors
4. Test client retry behavior

## Exercise 4: Stateful Behavior
Simulate conversation flows:

1. Configure scenario with state transitions
2. Return different responses based on call count
3. Reset state between tests
4. Test multi-step workflows

## Exercise 5: Contract Testing
Verify service contract adherence:

1. Record actual service responses
2. Create stubs from recording
3. Test client against stubs
4. Detect contract violations

## Bonus Challenge
Build a WireMock extension that: intercepts calls to `/api/external/*`, returns mock data from JSON files matching the path, and logs all intercepted requests.