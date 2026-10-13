package com.smartcampus.dto;

import jakarta.validation.constraints.*;

public class SubjectDto {

    private Long id;

    @NotBlank(message = "Subject code is required")
    @Size(min = 2, max = 30, message = "Subject code must be between 2 and 30 characters")
    private String code;

    @NotBlank(message = "Subject name is required")
    @Size(min = 2, max = 100, message = "Subject name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String departmentCode;
    private String departmentName;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 10")
    @Max(value = 10, message = "Semester must be between 1 and 10")
    private Integer semester;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be between 1 and 10")
    @Max(value = 10, message = "Credits must be between 1 and 10")
    private Integer credits;

    @NotNull(message = "Maximum marks are required")
    @DecimalMin(value = "1.0", message = "Maximum marks must be greater than 0")
    @DecimalMax(value = "1000.0", message = "Maximum marks cannot exceed 1000")
    private Double maxMarks = 100.0;

    public SubjectDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }
}
