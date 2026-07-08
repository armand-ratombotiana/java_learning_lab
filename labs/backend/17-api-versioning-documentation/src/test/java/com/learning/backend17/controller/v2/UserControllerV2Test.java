package com.learning.backend17.controller.v2;

import com.learning.backend17.model.CreateUserRequest;
import com.learning.backend17.service.UserServiceV2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerV2.class)
class UserControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceV2 userService;

    @Test
    void getAllUsers_returns200() throws Exception {
        mockMvc.perform(get("/v2/users")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void createUser_withValidRequest_returns201() throws Exception {
        when(userService.create(any())).thenReturn(null);

        mockMvc.perform(post("/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john@example.com"
                    }
                    """))
            .andExpect(status().isCreated());
    }
}
