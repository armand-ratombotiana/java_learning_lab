# Reflection on REST APIs

## Key Insights
- REST is about resources, not actions or functions
- HTTP methods, status codes, and headers form the API contract
- DTOs protect internal domain model from API changes
- Consistent error handling is critical for client experience
- API versioning prevents breaking changes for consumers

## Practical Lessons
- Always validate input with @Valid and custom validators
- Use ResponseEntity for explicit status code control
- Keep controllers thin (delegate to services)
- Document with OpenAPI from the start
- Test with both unit (MockMvc) and integration tests

## Common Mistakes
- Exposing JPA entities leads to serialization issues
- Inconsistent error response format hurts clients
- Missing CORS configuration blocks frontend apps
- Not handling validation errors with meaningful messages
