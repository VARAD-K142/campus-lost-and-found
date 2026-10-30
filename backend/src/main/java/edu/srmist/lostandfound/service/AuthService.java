package edu.srmist.lostandfound.service;

import edu.srmist.lostandfound.config.JwtTokenProvider;
import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.domain.enums.Role;
import edu.srmist.lostandfound.dto.auth.AuthResponse;
import edu.srmist.lostandfound.dto.auth.LoginRequest;
import edu.srmist.lostandfound.dto.auth.RegisterRequest;
import edu.srmist.lostandfound.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.auditService = auditService;
    }

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user is already registered with email: " + normalizedEmail);
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        // Security requirement: Registration must never permit self-assigning ADMIN role
        user.setRole(Role.USER);
        user.setStudentId(request.getStudentId() != null ? request.getStudentId().trim() : null);
        user.setDepartment(request.getDepartment() != null ? request.getDepartment().trim() : null);
        user.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null);

        User savedUser = userRepository.save(user);

        auditService.logAction(savedUser, "USER_REGISTERED", "User", savedUser.getId(),
                "Registered new user account: " + savedUser.getEmail(), null);

        String token = tokenProvider.generateToken(savedUser);
        return new AuthResponse(token, savedUser.getId(), savedUser.getFullName(), savedUser.getEmail(),
                savedUser.getRole(), savedUser.getStudentId(), savedUser.getDepartment());
    }

    @Transactional
public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        auditService.logAction(user, "USER_LOGIN", "User", user.getId(),
                "User successfully logged in: " + user.getEmail(), null);

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getFullName(), user.getEmail(),
                user.getRole(), user.getStudentId(), user.getDepartment());
    }
}
