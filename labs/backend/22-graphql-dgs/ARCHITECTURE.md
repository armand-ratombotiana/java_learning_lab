# Architecture: GraphQL DGS

`
Client â”€â”€â–¶ HTTP POST /graphql
              â”‚
              â–¼
        [DGS Controller]
              â”‚
              â–¼
        [GraphQL Engine]
              â”‚
         â”Œâ”€â”€â”€â”€â”´â”€â”€â”€â”€â”
         â–¼         â–¼
    [DataFetchers] [DataLoaders]
         â”‚            â”‚
         â–¼            â–¼
    [Services]    [Database/API]
`
"@

Write-Doc (Join-Path C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\backend\22-graphql-dgs "SECURITY.md") @"
# Security: GraphQL DGS

- Implement authentication and authorization
- Use @PreAuthorize on resolvers
- Limit query depth to prevent DoS
- Implement rate limiting per operation
- Disable introspection in production
- Validate input arguments
- Use persisted operations for production
- Monitor complex queries
