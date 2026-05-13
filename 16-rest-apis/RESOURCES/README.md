# REST API Resources

Best practices and patterns for REST API design.

## Contents

- [REST Best Practices](./rest-best-practices.md) - Design principles and guidelines

---

## Official Documentation

| Topic | Link |
|-------|------|
| REST Wikipedia | https://en.wikipedia.org/wiki/REST |
| RFC 7231 (HTTP/1.1) | https://tools.ietf.org/html/rfc7231 |
| RFC 7807 (Problem Details) | https://tools.ietf.org/html/rfc7807 |
| Richardson Maturity Model | https://martinfowler.com/articles/richardsonMaturityModel.html |

---

## Key Concepts

### HTTP Methods
- **GET** - Retrieve resources
- **POST** - Create new resources
- **PUT** - Update (replace) entire resource
- **PATCH** - Partial update
- **DELETE** - Remove resources

### Status Codes
- `200 OK` - Successful GET/PUT/PATCH
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Authorization failed
- `404 Not Found` - Resource doesn't exist
- `500 Internal Server Error` - Server issue

### Design Principles
- Use nouns for resources (`/users`, `/orders`)
- Use HTTP methods meaningfully
- Stateless communication
- Consistent URL naming (lowercase, hyphens)
- Version your APIs (`/v1/users`)