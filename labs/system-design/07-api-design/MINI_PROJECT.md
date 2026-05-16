# API Design - MINI PROJECT

Design and implement user management API:
- REST endpoints
- Pagination
- Error handling
- OpenAPI documentation

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserApi {
    @GetMapping
    public Page<UserDto> list(@RequestParam(defaultValue = "0") int page) {
        return userService.findAll(page);
    }
}
```