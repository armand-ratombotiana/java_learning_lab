# Visual Guide to REST APIs

## Request Flow

```
Client                     Spring Boot                    Service
  |                            |                            |
  |--- GET /api/users/1 ------>|                            |
  |                            |--- findById(1L) --------->|
  |                            |<-- User object -----------|
  |<-- 200 OK {JSON} ----------|                            |
```

## Status Code Decision Tree

```
Request received
  +-- Valid syntax?
  |    +-- No -> 400 Bad Request
  |    +-- Yes -> Process
  |         +-- Resource exists?
  |         |    +-- No -> 404 Not Found
  |         |    +-- Yes -> Authorized?
  |         |         +-- No -> 401/403
  |         |         +-- Yes -> Return
  |         |              +-- GET -> 200 OK
  |         |              +-- POST -> 201 Created
  |         |              +-- DELETE -> 204 No Content
```

## Spring MVC REST Stack

```
DispatcherServlet
  -> HandlerMapping (URL matching)
  -> HandlerAdapter (method invocation)
  -> HttpMessageConverter (JSON serialization)
  -> Response to client
```
