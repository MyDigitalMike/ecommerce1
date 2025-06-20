package core.ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.ecommerce.controller.AuthController;
import core.ecommerce.dto.AuthResponse;
import core.ecommerce.dto.LoginRequest;
import core.ecommerce.dto.RegisterRequest;
import core.ecommerce.services.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Mock
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegister_shouldReturnAuthResponse() throws Exception {
        RegisterRequest request = new RegisterRequest("Juan", "juan@correo.com", "123456", "CLIENT");
        AuthResponse response = new AuthResponse("token123", "CLIENT", "juan@correo.com");

        when(authService.register(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.role").value("CLIENT"))
                .andExpect(jsonPath("$.email").value("juan@correo.com"));
    }

    @Test
    void testLogin_shouldReturnAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest("juan@correo.com", "123456");
        AuthResponse response = new AuthResponse("token456", "CLIENT", "juan@correo.com");

        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token456"));
    }
}
