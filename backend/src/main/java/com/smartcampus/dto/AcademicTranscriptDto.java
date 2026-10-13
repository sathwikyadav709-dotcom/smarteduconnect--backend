package com.smartcampus.dto;

import java.util.List;

public class AcademicTranscriptDto {

    private Long studentId;
    private String rollNumber;
    private String fullName;
    private String email;
    private String departmentName;
    private Integer academicYear;
    private Integer currentSemester;
    private int totalCredits;
    private double overallPercentage;
    private double cgpa;
    private String overallGrade;
    private double attendancePercentage;
    private List<TranscriptSemesterDto> semesterTranscripts;

    public AcademicTranscriptDto() {
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public Integer getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(Integer currentSemester) {
        this.currentSemester = currentSemester;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public double getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public String getOverallGrade() {
        return overallGrade;
    }

    public void setOverallGrade(String overallGrade) {
        this.overallGrade = overallGrade;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public List<TranscriptSemesterDto> getSemesterTranscripts() {
        return semesterTranscripts;
    }

    public void setSemesterTranscripts(List<TranscriptSemesterDto> semesterTranscripts) {
        this.semesterTranscripts = semesterTranscripts;
    }

    public static class TranscriptSemesterDto {
        private Integer semester;
        private double semesterGpa;
        private List<TranscriptSubjectItem> subjects;

        public TranscriptSemesterDto() {
        }

        public TranscriptSemesterDto(Integer semester, double semesterGpa, List<TranscriptSubjectItem> subjects) {
            this.semester = semester;
            this.semesterGpa = semesterGpa;
            this.subjects = subjects;
        }

        public Integer getSemester() {
            return semester;
        }

        public void setSemester(Integer semester) {
            this.semester = semester;
        }

        public double getSemesterGpa() {
            return semesterGpa;
        }

        public void setSemesterGpa(double semesterGpa) {
            this.semesterGpa = semesterGpa;
        }

        public List<TranscriptSubjectItem> getSubjects() {
            return subjects;
        }

        public void setSubjects(List<TranscriptSubjectItem> subjects) {
            this.subjects = subjects;
        }
    }

    public static class TranscriptSubjectItem {
        private String subjectCode;
        private String subjectName;
        private int credits;
        private double marksObtained;
        private double maxMarks;
        private double percentage;
        private String grade;
        private double gradePoint;

        public TranscriptSubjectItem() {
        }

        public TranscriptSubjectItem(String subjectCode, String subjectName, int credits,
                                     double marksObtained, double maxMarks,
                                     double percentage, String grade, double gradePoint) {
            this.subjectCode = subjectCode;
            this.subjectName = subjectName;
            this.credits = credits;
            this.marksObtained = marksObtained;
            this.maxMarks = maxMarks;
            this.percentage = percentage;
            this.grade = grade;
            this.gradePoint = gradePoint;
        }

        public String getSubjectCode() {
            return subjectCode;
        }

        public void setSubjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public int getCredits() {
            return credits;
        }

        public void setCredits(int credits) {
            this.credits = credits;
        }

        public double getMarksObtained() {
            return marksObtained;
        }

        public void setMarksObtained(double marksObtained) {
            this.marksObtained = marksObtained;
        }

        public Double getMaxMarks() {
            return maxMarks;
        }

        public void setMaxMarks(Double maxMarks) {
            this.maxMarks = maxMarks;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public double getGradePoint() {
            return gradePoint;
        }

        public void setGradePoint(double gradePoint) {
            this.gradePoint = gradePoint;
        }
    }
}
