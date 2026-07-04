# Debugging 03 Spring Mvc

## Enable Debug Logging
```properties
logging.level.com.example=03springmvc=DEBUG
logging.level.org.springframework=DEBUG
```

## Common Issues

### Issue: Bean Not Created
Check:
1. Is auto-configuration condition met?
2. Is the required dependency on classpath?
3. Are properties correctly configured?

### Issue: Configuration Not Loaded
Check:
1. Property prefix matches @ConfigurationProperties
2. File is in correct location (application.yml)
3. Profile is active

## Debug Commands
```bash
# Check active configuration
curl localhost:8080/actuator/conditions

# Check property values
curl localhost:8080/actuator/env
```

## Testing Configuration
```java
@SpringBootTest
@ActiveProfiles("test")
class ConfigurationTest {
    @Test
    void testConfig() {
        // Verify configuration
    }
}
```

