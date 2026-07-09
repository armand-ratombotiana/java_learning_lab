# History — Spring Boot Internals

## Spring Boot 1.x (2014–2016)

- First stable release with auto-configuration via `spring.factories`
- `@EnableAutoConfiguration` introduced
- Tomcat-only embedded support
- Actuator endpoints with HTTP and JMX

## Spring Boot 2.0 (2018)

- `@ConditionalOnBean`, `@ConditionalOnMissingBean` improved with search strategy
- Jetty and Undertow as alternate embedded servers
- Reactive auto-configuration for WebFlux
- Actuator v2 with `/actuator` base path

## Spring Boot 2.6 (2021)

- Spring Boot 3.0 preparation
- Deprecation of `spring.factories` for auto-configuration

## Spring Boot 2.7 (2022)

- New `AutoConfiguration.imports` mechanism
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Backward compatible with `spring.factories`

## Spring Boot 3.x (2023+)

- Spring Framework 6 with Jakarta EE
- AOT compilation support
- GraalVM native images with auto-configuration analysis
- `@AutoConfiguration` annotation
- Removal of `spring.factories` for auto-configuration (only `AutoConfiguration.imports` supported)