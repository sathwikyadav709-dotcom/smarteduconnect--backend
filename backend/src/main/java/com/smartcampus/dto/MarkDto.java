package com.smartcampus.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class MarkDto {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotBlank(message = "Exam type is required")
    private String examType = "FINAL"; // MID_1, MID_2, FINAL, LAB

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 10")
    @Max(value = 10, message = "Semester must be between 1 and 10")
    private Integer semester;

    @NotNull(message = "Marks obtained is required")
    @DecimalMin(value = "0.0", message = "Marks obtained cannot be negative")
    private Double marksObtained;

    @DecimalMin(value = "1.0", message = "Maximum marks must be greater than 0")
    private Double maxMarks;

    private LocalDate examDate;

    public MarkDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Double marksObtained) {
        this.marksObtained = marksObtained;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }
}
