# GraphQL - Why It Exists

## The Problem GraphQL Solved

REST APIs often suffer from:
1. **Over-fetching**: Getting more data than needed
2. **Under-fetching**: Needing multiple requests for related data (N+1 problem)
3. **Versioning**: API version management is complex
4. **Documentation**: API docs often out of sync with implementation

## REST vs GraphQL
| Aspect | REST | GraphQL |
|--------|------|---------|
| Data fetching | Fixed response structure | Client specifies fields |
| Endpoints | Multiple (/users, /users/1) | Single (/graphql) |
| Versioning | URL or header based | Evolve schema, deprecate fields |
| Tooling | Swagger/OpenAPI | GraphiQL, Apollo Studio |
| Caching | HTTP caching built-in | Custom caching needed |
