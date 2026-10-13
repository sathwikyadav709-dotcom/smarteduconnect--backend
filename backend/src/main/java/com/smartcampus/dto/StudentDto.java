package com.smartcampus.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class StudentDto {

    private Long id;

    @NotBlank(message = "Roll number is required")
    @Size(min = 2, max = 50, message = "Roll number must be between 2 and 50 characters")
    private String rollNumber;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Pattern(regexp = "^$|[0-9+\\-\\s]{7,15}$", message = "Phone number must be valid")
    private String phone;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Academic year is required")
    @Min(value = 1, message = "Academic year must be between 1 and 5")
    @Max(value = 5, message = "Academic year must be between 1 and 5")
    private Integer academicYear;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 10")
    @Max(value = 10, message = "Semester must be between 1 and 10")
    private Integer semester;

    private LocalDate enrollmentDate;

    // Optional: automatically create a login user account for student
    private Boolean createLoginAccount = true;
    private String initialPassword = "CampusStudent#2026";

    public StudentDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Boolean getCreateLoginAccount() {
        return createLoginAccount;
    }

    public void setCreateLoginAccount(Boolean createLoginAccount) {
        this.createLoginAccount = createLoginAccount;
    }

    public String getInitialPassword() {
        return initialPassword;
    }

    public void setInitialPassword(String initialPassword) {
        this.initialPassword = initialPassword;
    }
}
