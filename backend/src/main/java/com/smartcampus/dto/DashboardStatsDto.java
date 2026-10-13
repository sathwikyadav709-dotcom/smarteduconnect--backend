package com.smartcampus.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDto {

    private long totalStudents;
    private long totalSubjects;
    private long totalDepartments;
    private double overallAttendancePercentage;
    private double averageMarksPercentage;
    private Map<String, Long> departmentStudentDistribution;
    private List<RecentActivityItem> recentActivities;

    public DashboardStatsDto() {
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(long totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public void setTotalDepartments(long totalDepartments) {
        this.totalDepartments = totalDepartments;
    }

    public double getOverallAttendancePercentage() {
        return overallAttendancePercentage;
    }

    public void setOverallAttendancePercentage(double overallAttendancePercentage) {
        this.overallAttendancePercentage = overallAttendancePercentage;
    }

    public double getAverageMarksPercentage() {
        return averageMarksPercentage;
    }

    public void setAverageMarksPercentage(double averageMarksPercentage) {
        this.averageMarksPercentage = averageMarksPercentage;
    }

    public Map<String, Long> getDepartmentStudentDistribution() {
        return departmentStudentDistribution;
    }

    public void setDepartmentStudentDistribution(Map<String, Long> departmentStudentDistribution) {
        this.departmentStudentDistribution = departmentStudentDistribution;
    }

    public List<RecentActivityItem> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<RecentActivityItem> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public static class RecentActivityItem {
        private String type;
        private String title;
        private String description;
        private String timestamp;

        public RecentActivityItem() {
        }

        public RecentActivityItem(String type, String title, String description, String timestamp) {
            this.type = type;
            this.title = title;
            this.description = description;
            this.timestamp = timestamp;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
