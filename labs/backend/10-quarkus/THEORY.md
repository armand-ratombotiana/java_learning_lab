# Theory: Quarkus

## Supersonic Subatomic Java
Quarkus is designed for container-first, Kubernetes-native Java.

### Key Concepts
- **Build-time processing**: Most work done at build time, not runtime
- **Extension system**: Plug-and-play functionality
- **Continuous testing**: Tests run in background as code changes
- **Dev Services**: Auto-configured test containers (Databases, Kafka, etc.)

### Panache ORM
Active record pattern for Hibernate ORM:
```java
@Entity
public class User extends PanacheEntity {
    public String name;
    public String email;

    public static List<User> findByName(String name) {
        return find("name", name).list();
    }
}
```

### Native Images
Using GraalVM's native-image tool:
- Startup: < 0.1s
- Memory: ~10MB
- No JIT warmup needed
- Perfect for serverless and auto-scaling
