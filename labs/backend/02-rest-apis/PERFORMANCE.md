# Performance of REST APIs

## Response Compression
```properties
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml
```

## Caching Headers
```java
@GetMapping("/{id}")
public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
        .eTag(calculateHash(user))
        .body(user);
}
```

## JSON Optimization
```properties
spring.jackson.default-property-inclusion=non_null
spring.jackson.serialization.write-dates-as-timestamps=true
```

## Connection Pooling
```properties
server.tomcat.max-threads=200
server.tomcat.max-connections=10000
server.tomcat.accept-count=100
```

## Batch Operations
```java
@PostMapping("/batch")
public ResponseEntity<List<UserDTO>> createBatch(
        @Valid @RequestBody List<CreateUserRequest> users) {
    return ResponseEntity.created(...).body(userService.createAll(users));
}
```
