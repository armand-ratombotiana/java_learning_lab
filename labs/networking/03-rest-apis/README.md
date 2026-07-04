# 03 - REST APIs

## Overview

REST (Representational State Transfer) is an architectural style for designing networked applications. This lab covers REST principles, resource design, HATEOAS, content negotiation, and versioning with Java/Spring implementations.

## Learning Objectives
- Understand REST architectural constraints
- Design resource-oriented APIs
- Implement HATEOAS hypermedia
- Handle content negotiation and versioning

## Quick Start
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
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.create(user));
    }
}
```
