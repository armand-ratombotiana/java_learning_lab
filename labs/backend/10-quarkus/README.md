# Quarkus

Quarkus is a Kubernetes-native Java framework tailored for GraalVM and HotSpot.

## Topics
- Live reload development
- Native image compilation with GraalVM
- Panache ORM (active record pattern)
- Reactive programming with Mutiny
- Extension ecosystem
- Kubernetes integration
- Continuous testing

## Example
```java
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") Long id) {
        return User.findById(id);
    }
}
```
