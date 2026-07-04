# Debugging Spring Boot Applications

## Remote Debugging
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar
```

## Actuator for Debugging
```properties
# Expose all endpoints
management.endpoints.web.exposure.include=*

# Show auto-configuration report
# GET /actuator/conditions - shows what was auto-configured and why
# GET /actuator/beans - shows all beans in context
# GET /actuator/configprops - shows @ConfigurationProperties binding
# GET /actuator/env - shows environment property sources
```

## Debug Auto-Configuration
```properties
debug=true
# Enables auto-configuration report in logs
# Positive matches (configured) and negative matches (not configured)
```

## Common Debugging Commands
```bash
# Check which auto-configuration is active
curl localhost:8080/actuator/conditions | jq

# View environment properties
curl localhost:8080/actuator/env | jq

# View all beans
curl localhost:8080/actuator/beans | jq
```
