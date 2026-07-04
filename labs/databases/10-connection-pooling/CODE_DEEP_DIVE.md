# Code Deep Dive: HikariCP Configuration

## Spring Boot Auto-Configuration

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=app_user
spring.datasource.password=${DB_PASSWORD}

# HikariCP specific settings
spring.datasource.hikari.pool-name=MyPool
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.connection-test-query=SELECT 1
spring.datasource.hikari.validation-timeout=5000
spring.datasource.hikari.leak-detection-threshold=60000
spring.datasource.hikari.register-mbeans=true
```

## Programmatic Configuration with Multiple Pools

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.oltp")
    public HikariDataSource oltpDataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
    }

    @Bean
    @ConfigurationProperties("app.datasource.reporting")
    public HikariDataSource reportingDataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
    }
}
```

## Monitoring with Micrometer

```java
@Bean
public HikariPoolMXBean poolProxy(HikariDataSource dataSource) {
    return dataSource.getHikariPoolMXBean();
}

// Metrics available via /actuator/metrics
// - hikaricp.connections.active
// - hikaricp.connections.idle
// - hikaricp.connections.pending
// - hikaricp.connections.timeout
```
