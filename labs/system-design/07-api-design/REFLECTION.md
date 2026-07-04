# API Design - REFLECTION

## Key Takeaways

1. **Consistency is king**: Developers using your API should be able to predict how any endpoint works based on patterns they've seen.

2. **Errors should be useful**: A good error message tells the developer what went wrong, what field caused it, and how to fix it.

3. **Versioning is about trust**: Breaking changes without migration breaks developer trust. Maintain backward compatibility.

4. **Security from day one**: Authentication, rate limiting, and input validation are not optional. Add them early.

## Self-Assessment Questions

- Can I design a RESTful API from scratch?
- Do I understand when to use GraphQL vs REST vs gRPC?
- Can I implement proper error handling with consistent formats?
- Do I know how to version APIs without breaking clients?

## Common Misconceptions

- "REST requires HATEOAS" — Level 2 REST (proper HTTP methods) is excellent for most APIs. Level 3 (HATEOAS) adds complexity.
- "GraphQL replaces REST" — They're complementary. Many organizations use both.
- "API documentation can be added later" — API-first design produces better APIs. Document the contract before implementing.
- "All endpoints should be RESTful" — Actions (cancel, refund, publish) don't fit CRUD. Use POST with meaningful names.

## Next Steps

- Design an API spec using OpenAPI before writing code
- Implement a GraphQL server alongside a REST API
- Set up an API gateway with rate limiting
- Read "REST API Design Rulebook" by Mark Masse
