# REST Best Practices

## Resource Naming

```
┌─────────────────────────────────────────────────────────────────┐
│  DO                                                              │
├─────────────────────────────────────────────────────────────────┤
│  /users                    Plural nouns                        │
│  /users/123                Resource by ID                      │
│  /users/123/orders         Nested relationships                 │
│  /orders?status=pending   Query parameters for filtering       │
│  /users?page=1&size=20    Pagination                           │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  DON'T                                                           │
├─────────────────────────────────────────────────────────────────┤
│  /getAllUsers              Avoid action words                   │
│  /createUser               Use HTTP methods instead             │
│  /updateUser                                                    │
│  /User                     Singular (inconsistent)            │
│  /users/123/orders/456/items  Too deep (max 2-3 levels)        │
└─────────────────────────────────────────────────────────────────┘
```

## HTTP Status Code Usage

```
2xx SUCCESS
──────────────────────────────────────────
200 OK          GET/PUT/PATCH successful
201 Created     POST created new resource
204 No Content  DELETE successful, no body

4xx CLIENT ERROR
──────────────────────────────────────────
400 Bad Request     Invalid syntax, bad JSON
401 Unauthorized    Missing/invalid auth
403 Forbidden       No permission
404 Not Found       Resource doesn't exist
409 Conflict        Duplicate, constraint violation
422 Unprocessable   Valid but semantically wrong

5xx SERVER ERROR
──────────────────────────────────────────
500 Internal Error  Unhandled exception
503 Unavailable     Service down
```

## Request/Response Patterns

```json
// Collection GET /users
{
  "data": [
    { "id": "1", "type": "user", "attributes": { "name": "Alice" } },
    { "id": "2", "type": "user", "attributes": { "name": "Bob" } }
  ],
  "meta": { "total": 100, "page": 1, "size": 20 }
}

// Single GET /users/1
{
  "data": {
    "id": "1",
    "type": "user",
    "attributes": { "name": "Alice", "email": "alice@example.com" }
  }
}

// POST /users (201 Created)
{
  "data": { "id": "3", "type": "user", "attributes": { "name": "Charlie" } }
}

// Error Response (RFC 7807)
{
  "type": "https://api.example.com/errors/validation",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Invalid email format",
  "errors": [
    { "field": "email", "message": "Invalid format" }
  ]
}
```

## Pagination

```
GET /users?page=2&size=50

Response Headers:
X-Total-Count: 250
X-Total-Pages: 5
X-Current-Page: 2
X-Next-Page: 3
X-Prev-Page: 1
```

## Versioning Strategies

```yaml
# URL Path (most common)
GET /v1/users
GET /v2/users

# Query Parameter
GET /users?version=2

# Header
Accept: application/vnd.api+json; version=2
```

## Filtering, Sorting, Field Selection

```
GET /users?status=active&role=admin
GET /users?sort=-created_at,name
GET /users?fields=id,name,email
GET /users?created_after=2024-01-01
GET /users?created_before=2024-12-31
```

## Idempotency

```
GET        - Idempotent
POST       - Not idempotent
PUT        - Idempotent (replace)
PATCH      - Not idempotent (usually)
DELETE     - Idempotent
```

## Best Practices Summary

1. **Use consistent URL structure** - plural nouns, lowercase
2. **Leverage HTTP methods properly** - GET/POST/PUT/PATCH/DELETE
3. **Return appropriate status codes** - don't return 200 for errors
4. **Version your API** - `/v1/`, `/v2/`
5. **Paginate large collections** - `page`, `size`
6. **Use JSON consistently** - JSON:API or custom format
7. **Document errors clearly** - RFC 7807 problem details
8. **Secure with HTTPS** - always
9. **Cache wisely** - proper headers (ETag, Cache-Control)
10. **Be consistent** - naming, response format, error handling