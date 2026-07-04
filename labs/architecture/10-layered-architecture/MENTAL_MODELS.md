# Mental Models for Layered Architecture

## The Office Building
Presentation layer is the reception desk (first contact). Business layer is the offices where decisions are made. Persistence layer is the filing room in the basement.

## The Waterfall
Requests flow down like water: presentation -> business -> persistence. Responses flow back up like bubbles.

## The Russian Doll
Each layer wraps around the one below. The presentation layer sees only the business layer, which sees only the persistence layer.

## The Assembly Line
A product moves through stages: raw materials (data arrives at controller), processing (business logic), storage (database).

## The Onion (Simple)
Unlike Clean Architecture's many layers, traditional layering has just 3-4 main layers stacked vertically.

```java
// Request flow in layers
// 1. Layer: Controller (receives HTTP request)
// 2. Layer: Service (validates, orchestrates)
// 3. Layer: Repository (persists data)
// 4. Layer: Database (stores data)

@RestController        // Layer 1
public class UserController {
    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserRequest req) {
        UserDto result = userService.create(req); // Calls Layer 2
        return ResponseEntity.ok(result);
    }
}

@Service               // Layer 2
public class UserService {
    public UserDto create(UserRequest req) {
        User user = userRepository.save(req.toEntity()); // Calls Layer 3
        return UserDto.from(user);
    }
}

@Repository            // Layer 3
public interface UserRepository extends JpaRepository<User, Long> { }
```
