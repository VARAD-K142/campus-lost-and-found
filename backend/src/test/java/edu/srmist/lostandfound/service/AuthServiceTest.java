package edu.srmist.lostandfound.service;

import edu.srmist.lostandfound.config.JwtTokenProvider;
import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.domain.enums.Role;
import edu.srmist.lostandfound.dto.auth.AuthResponse;
import edu.srmist.lostandfound.dto.auth.LoginRequest;
import edu.srmist.lostandfound.dto.auth.RegisterRequest;
import edu.srmist.lostandfound.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Karthik Raman");
        registerRequest.setEmail("kr9988@srmist.edu.in");
        registerRequest.setPassword("Secret@123");
        registerRequest.setStudentId("RA2311003010999");
        registerRequest.setDepartment("Computer Science and Engineering");
        registerRequest.setPhoneNumber("+91 99887 76655");

        testUser = new User();
        testUser.setId(10L);
        testUser.setFullName("Karthik Raman");
        testUser.setEmail("kr9988@srmist.edu.in");
        testUser.setPasswordHash("hashed_secret");
        testUser.setRole(Role.USER);
        testUser.setStudentId("RA2311003010999");
        testUser.setDepartment("Computer Science and Engineering");
    }

    @Test
    @DisplayName("Should register new user and enforce Role.USER with hashed password")
    void shouldRegisterNewUser() {
        when(userRepository.existsByEmail("kr9988@srmist.edu.in")).thenReturn(false);
        when(passwordEncoder.encode("Secret@123")).thenReturn("hashed_secret");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(tokenProvider.generateToken(testUser)).thenReturn("sample.jwt.token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("sample.jwt.token");
        assertThat(response.getEmail()).isEqualTo("kr9988@srmist.edu.in");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        verify(passwordEncoder).encode("Secret@123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should reject registration with 409 Conflict when email already exists")
    void shouldRejectDuplicateEmailRegistration() {
        when(userRepository.existsByEmail("kr9988@srmist.edu.in")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("A user is already registered with email");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should authenticate valid user login and return JWT token")
    void shouldLoginValidUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("kr9988@srmist.edu.in");
        loginRequest.setPassword("Secret@123");

        when(userRepository.findByEmail("kr9988@srmist.edu.in")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Secret@123", "hashed_secret")).thenReturn(true);
        when(tokenProvider.generateToken(testUser)).thenReturn("valid.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("valid.jwt.token");
        assertThat(response.getFullName()).isEqualTo("Karthik Raman");
    }

    @Test
    @DisplayName("Should reject login with 401 Unauthorized when password does not match")
    void shouldRejectInvalidPasswordLogin() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("kr9988@srmist.edu.in");
        loginRequest.setPassword("WrongPassword");

        when(userRepository.findByEmail("kr9988@srmist.edu.in")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPassword", "hashed_secret")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
