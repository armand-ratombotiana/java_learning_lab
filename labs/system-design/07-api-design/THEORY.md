# API Design - THEORY

## Overview

Good API design is crucial for developer experience, maintainability, and system evolution.

## 1. REST Best Practices

### Resource Naming
```
✓ GET /users/123
✓ GET /users/123/orders
✓ POST /users
✓ PUT /users/123
✓ DELETE /users/123

✗ GET /getUser?id=123
✗ POST /createUser
✗ GET /user/123/orders/456/items
```

### HTTP Status Codes
- 200 OK - Success
- 201 Created - Resource created
- 204 No Content - Success, no body
- 400 Bad Request - Client error
- 401 Unauthorized - Not authenticated
- 403 Forbidden - Not authorized
- 404 Not Found - Resource missing
- 500 Internal Server Error - Server error

### Error Response Format
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input",
    "details": [
      {"field": "email", "message": "Invalid format"}
    ],
    "traceId": "abc123"
  }
}
```

## 2. Versioning

### URL Versioning
```
/api/v1/users
/api/v2/users
```

### Header Versioning
```
Accept: application/vnd.api+json; version=2
```

### Strategy
- Use URL versioning for major changes
- Support old versions for migration period
- Deprecate with warnings

## 3. Pagination

### Cursor-Based
```json
{
  "data": [...],
  "pagination": {
    "nextCursor": "eyJpZCI6MTIzfQ==",
    "hasMore": true
  }
}
```

### Offset-Based
```json
{
  "data": [...],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "total": 1000,
    "totalPages": 50
  }
}
```

## 4. API Documentation

### OpenAPI/Swagger
```yaml
openapi: 3.0.0
paths:
  /users:
    get:
      summary: List users
      parameters:
        - name: page
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: Success
```

## Summary

1. **Use nouns**: /users not /getUsers
2. **Version**: /api/v1/users
3. **Status codes**: Use correct codes
4. **Errors**: Consistent error format
5. **Document**: OpenAPI specification