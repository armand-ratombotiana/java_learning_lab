# Mock Interview: API Design

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Platform Architect Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design the public REST API for a cloud file storage platform.

---

## Transcript

**Interviewer**: "Design the public API for a Dropbox-like file storage service. Focus on: file CRUD, sharing, versioning. The API will be consumed by millions of developers."

**Candidate**: "I'll focus on resource-oriented design with consistent patterns. Key resources: Files, Folders, Shares, Versions. Let me define the API surface."

**Interviewer**: "Start with file upload."

**Candidate**: "For small files (<100MB), a simple multipart upload: `POST /v1/files` with the file content in the body. For large files, a resumable upload protocol: 1) `POST /v1/files:start` → returns upload_id, 2) `PATCH /v1/files/{upload_id}?offset=X` sends chunks, 3) `POST /v1/files/{upload_id}:complete` finalizes. Each chunk has a Content-Range header."

**Interviewer**: "How do you handle idempotency for uploads?"

**Candidate**: "Client provides an `Idempotency-Key` header. The server deduplicates: if the same key is seen within 24 hours, return the previous response without re-processing. This prevents double uploads on network retries. We store idempotency keys in a Redis cluster with 24h TTL."

**Interviewer**: "Design the sharing API."

**Candidate**: "`POST /v1/files/{fileId}:share` with body `{email, role: viewer/editor}`. Returns `{shareId, status}`. For bulk sharing: `POST /v1/files/{fileId}:batchShare` with array of recipients. The response includes a status per recipient (success, pending, blocked). Sharing creates an entry in the file's ACL."

**Interviewer**: "How do you handle version history?"

**Candidate**: "`GET /v1/files/{fileId}/versions` returns list of version metadata. `GET /v1/files/{fileId}/versions/{versionId}` downloads a specific version. `DELETE /v1/files/{fileId}/versions/{versionId}` permanently deletes a version. By default, we keep 30 days of version history, configurable by the user."

**Interviewer**: "What about listing files in a folder?"

**Candidate**: "`GET /v1/files?parentId={folderId}`. Supports pagination via cursor: `pageToken` and `pageSize` parameters. Response: `{files: [...], nextPageToken: "abc123"}`. Supports filters: `?mimeType=image/png`, sorting: `?orderBy=modifiedTime desc, name asc`."

**Interviewer**: "How do you version the API?"

**Candidate**: "URL-based versioning (`/v1/`). When we need breaking changes, we release `/v2/` with migration guides. We support each version for at least 12 months. Non-breaking changes (adding fields) are backward compatible within a version. We use protobuf-like field semantics: never remove or repurpose fields."

---

## Key Takeaways

- **Resource-oriented design**: Files, Folders, Shares, Versions as resources
- **Resumable uploads**: Start → chunk → complete protocol for large files
- **Idempotency**: Idempotency-Key header prevents duplicates
- **Cursor-based pagination**: Stable listing, not offset-based
- **Consistent error format**: Every error has code, message, details
- **API versioning**: URL-based, minimum 12 months support
