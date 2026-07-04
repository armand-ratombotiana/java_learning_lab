# API Documentation

Documenting REST APIs with OpenAPI, SpringDoc, and Spring REST Docs.

## Topics
- OpenAPI 3.1 specification
- SpringDoc OpenAPI (springdoc-openapi)
- Swagger UI integration
- API versioning documentation
- Request/response examples
- Spring REST Docs (test-driven documentation)
- Security scheme documentation

## Example
```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management API")
public class UserController {
    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) { ... }
}
```
