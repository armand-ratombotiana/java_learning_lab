# Module 29: Spring Boot Advanced - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-28, specifically Module 25 (Spring Boot Basics)  
**Estimated Reading Time**: 90 minutes  

---

## 📚 Table of Contents

1. [Spring Boot Actuator](#actuator)
2. [Custom Auto-Configuration](#custom-autoconfig)
3. [Profiles and Externalized Configuration](#profiles)
4. [Spring Boot Testing Advanced](#testing)
5. [Caching in Spring Boot](#caching)

---

## 1. Spring Boot Actuator <a name="actuator"></a>
Actuator brings production-ready features to your application without having to write them yourself. It provides endpoints to monitor and manage your application, such as health checks, metrics, info, and environment properties.

```properties
# Enable all actuator endpoints over web
management.endpoints.web.exposure.include=*
```

---

## 2. Custom Auto-Configuration <a name="custom-autoconfig"></a>
You can write your own auto-configuration to automatically configure beans when certain classes are on the classpath or specific properties are set.

```java
@Configuration
@ConditionalOnClass(MyCustomService.class)
@ConditionalOnMissingBean
public class MyCustomAutoConfiguration {

    @Bean
    public MyCustomService myCustomService() {
        return new MyCustomServiceImpl();
    }
}
```

---

## 3. Profiles and Externalized Configuration <a name="profiles"></a>
Profiles provide a way to segregate parts of your application configuration and make it available only in certain environments (e.g., `dev`, `prod`).

```java
@Component
@Profile("prod")
public class ProductionDatabaseConfig implements DatabaseConfig {
    // Production specific config
}
```

---

## 4. Spring Boot Testing Advanced <a name="testing"></a>
Spring Boot provides `@SpringBootTest` for integration testing. For sliced testing, you can use `@WebMvcTest` (for testing only the web layer) or `@DataJpaTest` (for testing only the persistence layer).

```java
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testGetUser() throws Exception {
        // MockMvc test logic
    }
}
```

---

## 5. Caching in Spring Boot <a name="caching"></a>
Spring provides an abstraction over caching. Adding `@EnableCaching` allows you to use `@Cacheable`, `@CachePut`, and `@CacheEvict`.

```java
@Service
public class ProductService {

    @Cacheable(value = "products", key = "#id")
    public Product getProduct(Long id) {
        // Expensive DB call
        return productRepository.findById(id).orElseThrow();
    }
}
```