# Code Deep Dive: Testing Strategies

## Comprehensive Test Suite
```java
// Web Layer Test
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;

    @Test
    void shouldReturn200() throws Exception {
        when(userService.findById(1L)).thenReturn(new UserDTO(1L, "John"));
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John"));
    }
}

// JPA Test
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired private UserRepository userRepository;

    @Test
    void shouldFindByEmail() {
        userRepository.save(new User("john@test.com", "John"));
        Optional<User> found = userRepository.findByEmail("john@test.com");
        assertThat(found).isPresent();
    }
}

// Full Integration Test
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Containers(container = @Container("postgres:15", mode = ContainerMode.TRANSIENT))
class UserIntegrationTest {
    @Autowired private TestRestTemplate rest;

    @Test
    void fullCRUD() {
        UserDTO created = rest.postForObject("/api/users", new CreateUserRequest("John"), UserDTO.class);
        assertThat(created.getId()).isNotNull();

        UserDTO fetched = rest.getForObject("/api/users/{id}", UserDTO.class, created.getId());
        assertThat(fetched.getName()).isEqualTo("John");
    }
}
```
