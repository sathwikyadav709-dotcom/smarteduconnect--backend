package com.smartcampus.dto;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String fullName;
    private String role;
    private Long studentId;
    private String rollNumber;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long id, String email, String fullName, String role, Long studentId, String rollNumber) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.studentId = studentId;
        this.rollNumber = rollNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }
}
