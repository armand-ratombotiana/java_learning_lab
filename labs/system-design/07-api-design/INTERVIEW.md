# API Design - INTERVIEW

## Common Interview Questions

### Q1: REST vs GraphQL — when to use each?
**Answer**: REST: simple CRUD, caching important, stable relationships. GraphQL: complex data requirements, mobile APIs (minimize data transfer), rapid frontend iteration. Pro-tip: start with REST, add GraphQL when/if the complexity is justified.

### Q2: How do you handle API versioning?
**Answer**: Four strategies: (1) URL path versioning `/v1/products` — simple, visible; (2) Header versioning `Accept: application/vnd.api.v2+json` — clean URLs; (3) Query parameter versioning `?version=2` — simple; (4) No versioning, evolve backward-compatibly. I prefer URL versioning for public APIs, header versioning for internal.

### Q3: What's the difference between PUT and PATCH?
**Answer**: PUT replaces the entire resource. PATCH applies a partial update. PUT must include all fields (omitted fields = null). PATCH only includes changed fields. PUT is idempotent, PATCH is not necessarily idempotent.

### Q4: How do you design pagination for mobile APIs?
**Answer**: Cursor-based pagination (most reliable for mobile). Use `?cursor=<lastId>&limit=20`. Response includes `nextCursor` and `hasMore`. Avoid offset pagination — deep pages are slow and inserts/deletes shift results.

### Q5: What HTTP status codes should every API use?
**Answer**: 200 OK, 201 Created, 204 No Content, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict, 422 Unprocessable Entity, 429 Too Many Requests, 500 Internal Server Error.

### Q6: How do you ensure idempotency in APIs?
**Answer**: Client sends `Idempotency-Key` header. Server stores the key and response. If same key is received again, return stored response without processing. Use for POST/PATCH operations.

## System Design Problem: Design a File Upload API

### Requirements
- Upload files up to 5GB
- Resume interrupted uploads
- Virus scanning before storage
- Access control per file

### Proposed Solution
- **Multipart upload with presigned URLs** (AWS S3 or similar)
- **Chunked upload** with ETag for resume
- **Async virus scanning** via event-driven pipeline
- **Signed URLs** for download with expiry
- **Upload progress** via WebSocket
- **File metadata** stored in database with reference to storage location
