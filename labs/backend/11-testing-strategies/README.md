# Testing Strategies

Comprehensive testing approaches for Spring applications.

## Topics
- Unit testing with JUnit 5
- Mockito for mocking
- @SpringBootTest integration tests
- Test slices (@WebMvcTest, @DataJpaTest)
- TestContainers for database testing
- WireMock for HTTP mocking
- AssertJ for fluent assertions
- Test coverage and TDD

## Example
```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(new UserDTO(1L, "John"));
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John"));
    }
}
```
