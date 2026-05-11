package com.learning.lab.module16;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class ExampleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");

        testUsers = Arrays.asList(testUser, user2);
    }

    @Test
    @DisplayName("Test GET /api/users returns all users")
    void testGetAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(testUsers);

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("John Doe"))
            .andExpect(jsonPath("$[1].name").value("Jane Doe"));
    }

    @Test
    @DisplayName("Test GET /api/users/{id} returns user by id")
    void testGetUserById() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("Test GET /api/users/{id} returns 404 for non-existent user")
    void testGetUserNotFound() throws Exception {
        when(userService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test POST /api/users creates new user")
    void testCreateUser() throws Exception {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setName("New User");
        savedUser.setEmail("new@example.com");

        when(userService.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("New User"));
    }

    @Test
    @DisplayName("Test PUT /api/users/{id} updates existing user")
    void testUpdateUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");

        when(userService.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("Test DELETE /api/users/{id} removes user")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test HTTP status codes are correct")
    void testHttpStatusCodes() {
        assertEquals(200, HttpStatus.OK.value());
        assertEquals(201, HttpStatus.CREATED.value());
        assertEquals(204, HttpStatus.NO_CONTENT.value());
        assertEquals(400, HttpStatus.BAD_REQUEST.value());
        assertEquals(404, HttpStatus.NOT_FOUND.value());
        assertEquals(500, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    static class User {
        private Long id;
        private String name;
        private String email;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    interface UserService {
        List<User> findAll();
        User findById(Long id);
        User save(User user);
    }

    static class UserController {
        private final UserService userService;

        public UserController(UserService userService) {
            this.userService = userService;
        }
    }
}