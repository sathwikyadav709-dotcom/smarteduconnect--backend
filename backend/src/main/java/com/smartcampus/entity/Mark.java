package com.smartcampus.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "marks",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_subject_exam", columnNames = {"student_id", "subject_id", "exam_type"})
    }
)
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "exam_type", nullable = false, length = 50)
    private String examType = "FINAL"; // MID_1, MID_2, FINAL, LAB, ASSIGNMENT

    @Column(nullable = false)
    private Integer semester;

    @Column(name = "marks_obtained", nullable = false)
    private Double marksObtained;

    @Column(name = "max_marks", nullable = false)
    private Double maxMarks = 100.0;

    @Column(nullable = false)
    private Double percentage;

    @Column(nullable = false, length = 10)
    private String grade;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Mark() {
    }

    public Mark(Student student, Subject subject, String examType, Integer semester,
                Double marksObtained, Double maxMarks, LocalDate examDate) {
        this.student = student;
        this.subject = subject;
        this.examType = examType != null ? examType : "FINAL";
        this.semester = semester;
        this.marksObtained = marksObtained;
        this.maxMarks = (maxMarks != null && maxMarks > 0) ? maxMarks : 100.0;
        this.examDate = examDate;
        this.createdAt = LocalDateTime.now();
        calculatePercentageAndGrade();
    }

    public void calculatePercentageAndGrade() {
        if (this.maxMarks != null && this.maxMarks > 0 && this.marksObtained != null) {
            this.percentage = Math.round((this.marksObtained / this.maxMarks) * 10000.0) / 100.0;
            this.grade = computeGrade(this.percentage);
        }
    }

    public static String computeGrade(double percentage) {
        if (percentage >= 90.0) return "A+";
        if (percentage >= 80.0) return "A";
        if (percentage >= 70.0) return "B";
        if (percentage >= 60.0) return "C";
        if (percentage >= 50.0) return "D";
        if (percentage >= 40.0) return "E";
        return "F";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
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
        calculatePercentageAndGrade();
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
        calculatePercentageAndGrade();
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
