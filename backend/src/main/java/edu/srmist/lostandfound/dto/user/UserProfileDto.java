package edu.srmist.lostandfound.dto.user;

import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.domain.enums.Role;
import java.time.LocalDateTime;

public class UserProfileDto {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String studentId;
    private String department;
    private String phoneNumber;
    private LocalDateTime createdAt;

    public UserProfileDto() {}

    public static UserProfileDto fromEntity(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStudentId(user.getStudentId());
        dto.setDepartment(user.getDepartment());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
