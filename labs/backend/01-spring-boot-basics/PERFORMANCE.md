# Performance

## Startup Optimization
```properties
# Lazy initialization (faster startup, slower first request)
spring.main.lazy-initialization=true

# Exclude unused auto-configuration
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    SecurityAutoConfiguration.class
})
```

## JVM Tuning
```bash
java -Xms256m -Xmx512m -jar app.jar
# Use G1GC for server applications
-XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

## Embedded Server Tuning
```properties
server.tomcat.max-threads=200
server.tomcat.max-connections=10000
server.tomcat.accept-count=100
server.tomcat.connection-timeout=5000
```

## Actuator Performance
Disable unnecessary actuator endpoints in production:
```properties
management.endpoints.web.exposure.include=health,info
```

## Unsorted Dependencies
Remove unused starter dependencies to reduce classpath scanning.
