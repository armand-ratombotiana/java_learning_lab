# REST APIs Solution

Reference implementation for REST controllers, validation, and HATEOAS.

## REST Controller
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- `@PathVariable`, `@RequestParam`, `@RequestBody`
- Request/Response handling

## Validation
- `@NotNull`, `@NotEmpty`, `@NotBlank`
- `@Size`, `@Email`, `@Min`, `@Max`, `@Pattern`
- Custom validation

## HATEOAS
- Link creation with `Link.of(href, rel)`
- `ResourceSupport` for adding links
- Self-referencing links

## Paging & Sorting
- `Pageable` for pagination
- `Page<T>` for paginated results
- `hasNext()`, `hasPrevious()`

## Error Handling
- Global exception handler
- `ApiError` with status, message, path, timestamp
- Custom exceptions

## CORS
- `CorsConfiguration` for cross-origin setup
- Allowed origins/methods configuration

## Key Classes
- `ResponseEntity<T>`: HTTP response wrapper
- `UserDTO`, `ProductDTO`: Data transfer objects
- `GlobalExceptionHandler`: Central error handling

## Test Coverage: 40+ tests