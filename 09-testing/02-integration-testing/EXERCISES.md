# Exercises: Integration Testing

## Basic Exercises

1. **@SpringBootTest**: Create full application context test
   - Configure test properties
   - Verify beans load correctly

2. **WebEnvironment**: Test with mock web layer
   - Use `WebEnvironment.MOCK`
   - Test REST controllers

3. **Test Slices**: Use test slices for focused tests
   - `@DataJpaTest` for repositories
   - `@WebMvcTest` for controllers
   - `@RestClientTest` for REST clients

4. **MockMvc**: Test HTTP endpoints
   - Perform GET/POST requests
   - Verify responses and status

5. **Test Properties**: Override configuration
   - Use `@TestPropertySource`
   - Configure test databases

## Intermediate Exercises

6. **Context Customization**: Customize test context
   - Import custom configurations
   - Mock specific beans

7. **HTMLUnit**: Test Thymeleaf views
   - Use HtmlUnit with mockMvc
   - Verify rendered HTML

8. **Selenium Tests**: Browser-based testing
   - Integrate Selenium WebDriver
   - Test JavaScript interactions

## Advanced Exercises

9. **Performance**: Optimize test execution
   - Use `@Transactional` for rollback
   - Configure parallel execution