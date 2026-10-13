package com.smartcampus.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class AttendanceBulkRecordDto {

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotEmpty(message = "Attendance records cannot be empty")
    private List<StudentAttendanceItem> records;

    public AttendanceBulkRecordDto() {
    }

    public AttendanceBulkRecordDto(Long subjectId, LocalDate attendanceDate, List<StudentAttendanceItem> records) {
        this.subjectId = subjectId;
        this.attendanceDate = attendanceDate;
        this.records = records;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public List<StudentAttendanceItem> getRecords() {
        return records;
    }

    public void setRecords(List<StudentAttendanceItem> records) {
        this.records = records;
    }

    public static class StudentAttendanceItem {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Attendance status is required")
        private Boolean isPresent;

        private String remarks;

        public StudentAttendanceItem() {
        }

        public StudentAttendanceItem(Long studentId, Boolean isPresent, String remarks) {
            this.studentId = studentId;
            this.isPresent = isPresent;
            this.remarks = remarks;
        }

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public Boolean getIsPresent() {
            return isPresent;
        }

        public void setIsPresent(Boolean isPresent) {
            this.isPresent = isPresent;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
