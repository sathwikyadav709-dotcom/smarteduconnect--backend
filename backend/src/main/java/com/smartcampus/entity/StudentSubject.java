package com.smartcampus.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "student_subjects",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_subject", columnNames = {"student_id", "subject_id"})
    }
)
public class StudentSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private LocalDate enrollmentDate = LocalDate.now();

    @Column(length = 20)
    private String status = "ACTIVE";

    public StudentSubject() {
    }

    public StudentSubject(Student student, Subject subject, LocalDate enrollmentDate, String status) {
        this.student = student;
        this.subject = subject;
        this.enrollmentDate = (enrollmentDate != null) ? enrollmentDate : LocalDate.now();
        this.status = (status != null) ? status : "ACTIVE";
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

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
