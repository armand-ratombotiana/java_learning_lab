# WebFlux Reactive

Reactive programming with Spring WebFlux.

## Topics
- Reactive Streams specification
- Project Reactor (Mono, Flux)
- WebFlux controllers (annotated)
- Functional endpoints (RouterFunction)
- WebClient for reactive HTTP calls
- Reactive MongoDB/R2DBC
- Error handling in reactive streams
- Backpressure and concurrency

## Example
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping
    public Flux<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserDTO>> getUser(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
```
