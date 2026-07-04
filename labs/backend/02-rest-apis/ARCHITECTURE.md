# Architecture of REST APIs

## Layered Architecture
```
+--------------------------+
|   Controller Layer       |  @RestController (handles HTTP)
+--------------------------+
|    Service Layer         |  @Service (business logic)
+--------------------------+
|  Repository Layer        |  @Repository (data access)
+--------------------------+
|      Domain Layer        |  Entities, Value Objects
+--------------------------+
```

## DTO Pattern
```
Controller <-> Service: DTO objects
Service <-> Repository: Domain entities
Controller -> Response: DTO serialized to JSON
```

## API Gateway Pattern
```
Client -> API Gateway -> User Service
                      -> Order Service
                      -> Payment Service
```

## Versioning Strategies
1. URL Path: /api/v1/users, /api/v2/users
2. Request Header: Accept: application/vnd.myapp.v1+json
3. Query Parameter: /api/users?version=1

## Pagination Pattern
```java
@GetMapping
public ResponseEntity<Page<UserDTO>> getAll(Pageable pageable) {
    Page<UserDTO> page = userService.findAll(pageable);
    return ResponseEntity.ok(page);
}
```

Response:
```json
{
  "content": [{...}],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```
