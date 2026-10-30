package edu.srmist.lostandfound.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.srmist.lostandfound.config.JwtAuthenticationFilter;
import edu.srmist.lostandfound.config.JwtTokenProvider;
import edu.srmist.lostandfound.domain.enums.Role;
import edu.srmist.lostandfound.dto.auth.AuthResponse;
import edu.srmist.lostandfound.dto.auth.LoginRequest;
import edu.srmist.lostandfound.dto.auth.RegisterRequest;
import edu.srmist.lostandfound.service.AuthService;
import edu.srmist.lostandfound.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/auth/register should return 201 Created on valid request")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test Student");
        request.setEmail("student@srmist.edu.in");
        request.setPassword("Password@123");
        request.setStudentId("RA2311003010001");
        request.setDepartment("Computer Science and Engineering");
        request.setPhoneNumber("+91 91234 56789");

        AuthResponse authResponse = new AuthResponse("mock.jwt.token", 1L, "Test Student",
                "student@srmist.edu.in", Role.USER, "RA2311003010001", "Computer Science and Engineering");

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.data.email").value("student@srmist.edu.in"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register should return 400 Bad Request on invalid email")
    void shouldFailValidationOnInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test Student");
        request.setEmail("invalid-email-string");
        request.setPassword("Password@123");
        request.setStudentId("RA2311003010001");
        request.setDepartment("Computer Science");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login should return 200 OK on valid credentials")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("student@srmist.edu.in");
        request.setPassword("Password@123");

        AuthResponse authResponse = new AuthResponse("login.jwt.token", 1L, "Test Student",
                "student@srmist.edu.in", Role.USER, "RA2311003010001", "Computer Science and Engineering");

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("login.jwt.token"));
    }
}
