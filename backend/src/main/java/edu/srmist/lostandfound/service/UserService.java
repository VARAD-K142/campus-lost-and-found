package edu.srmist.lostandfound.service;

import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.dto.user.UpdateProfileRequest;
import edu.srmist.lostandfound.dto.user.UserProfileDto;
import edu.srmist.lostandfound.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    @Transactional(readOnly = true)
    public UserProfileDto getMyProfile() {
        User user = getCurrentAuthenticatedUser();
        return UserProfileDto.fromEntity(user);
    }

    public UserProfileDto updateMyProfile(UpdateProfileRequest request) {
        User user = getCurrentAuthenticatedUser();

        user.setFullName(request.getFullName().trim());
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment().trim());
        if (request.getStudentId() != null) user.setStudentId(request.getStudentId().trim());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber().trim());

        User updated = userRepository.save(user);

        auditService.logAction(updated, "PROFILE_UPDATED", "User", updated.getId(),
                "User updated personal profile details", null);

        return UserProfileDto.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));
    }
}
