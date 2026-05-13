# 51 - Quarkus Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Extension | Quarkus module adding capabilities |
| Dev Mode | Live reload with instant startup |
| Build Goal | Native compilation for fast execution |
| Application | Entry point with @QuarkusMain |
| Config | application.yaml for all settings |

## REST Endpoints

```java
// JAX-RS with Quarkus
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    
    @Inject
    UserService userService;
    
    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") Long id) {
        return userService.findById(id);
    }
    
    @POST
    public Response createUser(User user) {
        User created = userService.create(user);
        return Response.status(Status.CREATED)
            .entity(created)
            .build();
    }
}
```

## Reactive Programming

```java
// Mutiny for reactive operations
@GET
@Path("/{id}")
public Uni<User> getUserReactive(@PathParam("id") Long id) {
    return userService.findByIdReactive(id);
}

// Multiple reactive calls
@GET
@Path("/combined/{id}")
public Uni<CombinedResult> getCombined(@PathParam("id") Long id) {
    return Uni.join()
        .and(userService.findById(id))
        .and(orderService.getOrdersByUser(id))
        .usingCompletion();
}

// Streaming
@GET
@Path("/stream")
@RestSseProducible
public Multi<String> streamData() {
    return Multi.createFrom().ticker()
        .map(t -> "tick: " + t);
}
```

## Configuration

```yaml
# application.yaml
quarkus:
  http:
    port: 8080
    host: 0.0.0.0
  
  datasource:
    db-kind: postgresql
    jdbc:
      url: jdbc:postgresql://localhost:5432/mydb
    username: admin
    password: secret
  
  hibernate-orm:
    database:
      generation: drop-and-create
  
  log:
    level: INFO
    category:
      "org.hibernate":
        level: DEBUG

# Custom config
myapp:
  batch-size: 100
  feature-flags:
    new-ui: true
```

## Dependency Injection

```java
// Singleton bean
@Singleton
public class UserService {
    @Inject
    @ConfigProperty(name = "myapp.default-role")
    String defaultRole;
}

// Request scoped
@RequestScoped
public class RequestService {
    @HeaderParam("X-Request-Id")
    String requestId;
}

// Producer methods
@Produces
@ApplicationScoped
public Jsonb createJsonb() {
    return JsonBuilder.create().build();
}

// Disposer
public void close(@Disposed EntityManager em) {
    em.close();
}
```

## Database Access

```java
// Panache active record pattern
@Entity
public class User extends PanacheEntity {
    public String name;
    public String email;
    
    public static User findByEmail(String email) {
        return find("email", email).firstResult();
    }
    
    public static List<User> findActive() {
        return list("status", Status.ACTIVE);
    }
}

// Repository pattern
@Repository
public class UserRepository implements PanacheRepository<User> {
    public User findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
```

## Testing

```java
// Unit test
@QuarkusTest
class UserResourceTest {
    
    @InjectMock
    UserService userService;
    
    @Test
    void testGetUser() {
        when(userService.findById(1L))
            .thenReturn(new User("John", "john@example.com"));
        
        given()
            .when().get("/api/users/1")
            .then()
            .statusCode(200)
            .body("name", equalTo("John"));
    }
}

// Integration test
@QuarkusIntegrationTest
class ExternalServiceTest {
    @Test
    void testExternal() {
        // Test against running app
    }
}
```

## Extensions

```bash
# Add extensions via CLI
mvn quarkus:add-extension -Dextensions="smallrye-reactive-messaging"
mvn quarkus:add-extension -Dextensions="hibernate-orm-panache"

# Common extensions
quarkus-arc           # DI container
quarkus-resteasy      # JAX-RS
quarkus-resteasy-reactive  # Reactive JAX-RS
quarkus-hibernate-orm-panache  # Database ORM
quarkus-jdbc-postgresql     # PostgreSQL driver
quarkus-smallrye-openapi   # OpenAPI/Swagger
quarkus-smallrye-health    # Health checks
quarkus-micrometer-registry-prometheus  # Metrics
```

## Best Practices

Use reactive patterns for I/O operations. Leverage Panache for database productivity. Configure dev services for automatic testing databases. Use application.yaml for all configuration. Take advantage of native compilation for container deployment.