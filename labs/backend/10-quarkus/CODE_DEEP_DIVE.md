# Code Deep Dive: Quarkus

## Quarkus with Panache
```java
@Entity
public class User extends PanacheEntity {
    public String name;
    public String email;
    public LocalDateTime createdAt;

    // Active record methods available directly
    public static List<User> findActiveUsers() {
        return list("createdAt > ?1", LocalDateTime.now().minusDays(30));
    }

    public static Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @GET
    public List<User> list(@QueryParam("page") @DefaultValue("0") int page) {
        return User.findAll().page(page, 20).list();
    }

    @GET
    @Path("/{id}")
    public User get(@PathParam Long id) {
        return User.findById(id);
    }

    @POST
    @Transactional
    public Response create(User user) {
        user.createdAt = LocalDateTime.now();
        user.persist();
        return Response.status(201).entity(user).build();
    }
}
```
