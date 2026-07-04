# Code Deep Dive: WebFlux Reactive

## Reactive REST API
```java
@RestController
@RequestMapping("/api/users")
public class UserReactiveController {
    private final UserReactiveRepository repository;

    @GetMapping
    public Flux<UserDTO> getAll() {
        return repository.findAll()
            .map(this::toDTO);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserDTO>> getById(@PathVariable String id) {
        return repository.findById(id)
            .map(user -> ResponseEntity.ok(toDTO(user)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<UserDTO>> create(@Valid @RequestBody Mono<CreateUserRequest> request) {
        return request
            .flatMap(r -> repository.save(new User(r.getName(), r.getEmail())))
            .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(toDTO(saved)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserDTO>> update(@PathVariable String id,
                                                  @Valid @RequestBody Mono<UpdateUserRequest> request) {
        return request
            .flatMap(r -> repository.findById(id)
                .flatMap(existing -> {
                    existing.setName(r.getName());
                    return repository.save(existing);
                }))
            .map(updated -> ResponseEntity.ok(toDTO(updated)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return repository.deleteById(id)
            .then(Mono.just(ResponseEntity.noContent().build()));
    }
}

// Functional endpoint alternative
@Configuration
public class UserRouter {
    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return route()
            .GET("/api/users", handler::getAll)
            .GET("/api/users/{id}", handler::getById)
            .POST("/api/users", handler::create)
            .PUT("/api/users/{id}", handler::update)
            .DELETE("/api/users/{id}", handler::delete)
            .build();
    }
}
```
