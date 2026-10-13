package com.smartcampus.dto;

public class AttendanceSummaryDto {

    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private long totalClasses;
    private long presentClasses;
    private double attendancePercentage;
    private boolean belowThreshold;

    public AttendanceSummaryDto() {
    }

    public AttendanceSummaryDto(Long subjectId, String subjectCode, String subjectName,
                                long totalClasses, long presentClasses,
                                double attendancePercentage, boolean belowThreshold) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.totalClasses = totalClasses;
        this.presentClasses = presentClasses;
        this.attendancePercentage = attendancePercentage;
        this.belowThreshold = belowThreshold;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
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

    public long getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(long totalClasses) {
        this.totalClasses = totalClasses;
    }

    public long getPresentClasses() {
        return presentClasses;
    }

    public void setPresentClasses(long presentClasses) {
        this.presentClasses = presentClasses;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public boolean isBelowThreshold() {
        return belowThreshold;
    }

    public void setBelowThreshold(boolean belowThreshold) {
        this.belowThreshold = belowThreshold;
    }
}
