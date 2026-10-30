package edu.srmist.lostandfound.controller;

import edu.srmist.lostandfound.dto.common.ApiResponse;
import edu.srmist.lostandfound.dto.user.UpdateProfileRequest;
import edu.srmist.lostandfound.dto.user.UserProfileDto;
import edu.srmist.lostandfound.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> getMyProfile() {
        UserProfileDto profile = userService.getMyProfile();
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfileDto updated = userService.updateMyProfile(request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }
}
