package edu.srmist.lostandfound.dto.auth;

import edu.srmist.lostandfound.domain.enums.Role;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String studentId;
    private String department;

    public AuthResponse(String token, Long id, String fullName, String email, Role role, String studentId, String department) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.studentId = studentId;
        this.department = department;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public String getStudentId() { return studentId; }
    public String getDepartment() { return department; }
}
