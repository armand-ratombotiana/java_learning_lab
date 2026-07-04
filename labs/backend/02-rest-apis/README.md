# REST APIs

Building RESTful APIs with Spring Boot, covering HTTP methods, request handling, status codes, and response entities.

## Topics
- @RestController and @RequestMapping
- GET, POST, PUT, DELETE, PATCH mappings
- Request parameters, path variables, request bodies
- ResponseEntity and status codes
- Exception handling with @ControllerAdvice
- HATEOAS and API versioning
- Content negotiation (JSON/XML)

## Example
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }
}
```
