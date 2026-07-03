# Module 45: Advanced Testing Strategies - Mini Project

**Project Name**: Resilient Microservice Test Suite  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Implement a robust testing pyramid for a Spring Boot application using JUnit 5, Mockito for unit tests, Testcontainers for true integration testing against a real database, and PiTest for mutation testing.

## 📝 Requirements

### Core Features

1. **The Application**:
   - Create a `CustomerService` with a method `boolean registerCustomer(Customer c)`.
   - The service should validate the customer (email format, age > 18).
   - If valid, it saves the customer via `CustomerRepository` (which extends `JpaRepository`).
   - If the database throws a duplicate key exception (email already exists), it should catch it and throw a custom `DuplicateCustomerException`.

2. **Unit Testing (Mockito)**:
   - Create `CustomerServiceTest`.
   - Use `@ExtendWith(MockitoExtension.class)`.
   - Mock the `CustomerRepository`.
   - Write tests for the validation logic (ensure it returns false for bad emails).
   - Write a test ensuring `repository.save()` is called exactly once for a valid customer.
   - Write a test using `when(repository.save(any())).thenThrow(...)` to verify the `DuplicateCustomerException` logic.

3. **Integration Testing (Testcontainers)**:
   - Add dependencies for `testcontainers` and `testcontainers-postgresql`.
   - Create `CustomerIntegrationTest` annotated with `@SpringBootTest`.
   - Use `@Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");` to spin up a real database.
   - Use `@DynamicPropertySource` to dynamically override the `spring.datasource.url` properties so the application connects to the Docker container.
   - Write a test that actually saves a customer to the database and verifies it was persisted.

4. **Mutation Testing (PiTest)**:
   - Add the `pitest-maven` plugin to your `pom.xml`.
   - Configure it to target your `CustomerService` class.
   - Run `mvn pitest:mutationCoverage`.
   - Open the generated HTML report in `target/pit-reports/index.html`.
   - Analyze if any mutations "survived" (e.g., changing `age > 18` to `age >= 18`). If a mutation survived, it means your Unit Tests are weak. Write an additional test to kill the surviving mutant.

---

## 💡 Solution Blueprint

1. **Testcontainers Configuration**:
   ```java
   @Testcontainers
   @SpringBootTest
   public class CustomerIntegrationTest {

       @Container
       static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

       @DynamicPropertySource
       static void registerPgProperties(DynamicPropertyRegistry registry) {
           registry.add("spring.datasource.url", postgres::getJdbcUrl);
           registry.add("spring.datasource.username", postgres::getUsername);
           registry.add("spring.datasource.password", postgres::getPassword);
       }

       @Autowired
       private CustomerRepository repository;

       @Test
       void testDatabasePersistsData() {
           Customer c = new Customer("test@test.com", 25);
           repository.save(c);
           assertTrue(repository.findByEmail("test@test.com").isPresent());
       }
   }
   ```

2. **PiTest Plugin Setup (`pom.xml`)**:
   ```xml
   <plugin>
       <groupId>org.pitest</groupId>
       <artifactId>pitest-maven</artifactId>
       <version>1.15.2</version>
       <dependencies>
           <dependency>
               <groupId>org.pitest</groupId>
               <artifactId>pitest-junit5-plugin</artifactId>
               <version>1.2.0</version>
           </dependency>
       </dependencies>
       <configuration>
           <targetClasses>
               <param>com.example.service.*</param>
           </targetClasses>
           <targetTests>
               <param>com.example.*</param>
           </targetTests>
       </configuration>
   </plugin>
   ```