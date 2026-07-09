# Challenge: Build a Production Custom Starter

## Requirements

Create a Spring Boot starter called `simple-cache-starter` that provides:
1. A `@EnableSimpleCache` annotation that activates the starter
2. A `SimpleCacheManager` that stores values in a `ConcurrentHashMap`
3. `@SimpleCacheable` annotation with `key` and `ttl` attributes
4. Auto-configuration that:
   - Activates only when `simple.cache.enabled=true`
   - Creates a default cache if no custom beans exist
   - Exposes metrics via Micrometer `MeterRegistry`
5. A health indicator that checks cache size vs capacity

## Deliverables

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` listing
- Auto-configuration class with proper conditionals
- `@ConfigurationProperties` for `simple.cache.*` properties
- Health indicator using `HealthIndicator`
- Metrics exposed via Micrometer

## Evaluation

- Does the auto-configuration properly use `@ConditionalOnMissingBean`?
- Are all conditions documented?
- Does the health indicator work in composite mode?
- Are metrics properly tagged?