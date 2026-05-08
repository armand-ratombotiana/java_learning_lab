# Pedagogic Guide - WireMock

## Learning Path

### Phase 1: Basic Stubbing
1. Standalone server vs. embedded
2. Stub creation and configuration
3. Response body and headers
4. Basic matching

### Phase 2: Advanced Matching
1. URL pattern matching (regex, glob)
2. Header matching
3. JSON body matching
4. XPath matching

### Phase 3: Response Manipulation
1. Delay and latency injection
2. Fault types (connection drop, bad response)
3. Chunked dribble delay
4. Proxying to real service

### Phase 4: State Management
1. Scenario states
2. Scenario transitions
3. Priority-based matching
4. Random response selection

### Phase 5: Best Practices
1. Test isolation
2. Centralized stub management
3. Stub documentation
4. CI/CD integration

## Matching Strategies

| Strategy | Use Case |
|----------|----------|
| EqualTo | Exact value match |
| Contains | Substring match |
| MatchesJsonPath | JSON field extraction |
| MatchesXPath | XML element match |
| BinaryEqualTo | Binary comparison |

## Interview Topics

| Topic | Question |
|-------|----------|
| vs Mockito | When to use WireMock vs Mockito? |
| Contract Testing | How to ensure contract integrity? |
| State | How to test stateful services? |
| Performance | How does WireMock handle load? |
| Alternatives | WireMock vs MockServer vs Hoverfly? |

## Advanced Features
- Extension points for custom matchers
- Webhooks for async callbacks
- Proxy mode for recording
- Admin REST API for dynamic stubs

## Next Steps
- Explore Spring Cloud Contract for contract testing
- Learn about Pact for consumer-driven contracts
- Study API simulation techniques