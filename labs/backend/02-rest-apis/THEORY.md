# Theory: REST APIs

## REST Constraints (Roy Fielding)

1. **Client-Server** - Separation of concerns
2. **Stateless** - Each request contains all necessary information
3. **Cacheable** - Responses must define cacheability
4. **Uniform Interface** - Resource identification, manipulation through representations
5. **Layered System** - Intermediaries (proxies, gateways) can be inserted
6. **Code on Demand** (optional) - Executable code from server

## HTTP Methods and Idempotency

| Method | CRUD | Idempotent | Safe | Request Body | Response |
|--------|------|------------|------|-------------|----------|
| GET | Read | Yes | Yes | No | 200 OK |
| POST | Create | No | No | Yes | 201 Created |
| PUT | Update/Replace | Yes | No | Yes | 200 OK |
| PATCH | Partial Update | No | No | Yes | 200 OK |
| DELETE | Delete | Yes | No | No | 204 No Content |

## Status Code Categories
- 1xx: Informational
- 2xx: Success (200 OK, 201 Created, 204 No Content)
- 3xx: Redirection (301 Moved, 304 Not Modified)
- 4xx: Client Error (400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict)
- 5xx: Server Error (500 Internal, 502 Bad Gateway, 503 Service Unavailable)
