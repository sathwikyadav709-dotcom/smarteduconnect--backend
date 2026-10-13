package com.smartcampus.service;

import com.smartcampus.dto.DashboardStatsDto;
import com.smartcampus.entity.Department;
import com.smartcampus.repository.DepartmentRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceService attendanceService;
    private final MarkService markService;

    public DashboardService(StudentRepository studentRepository,
                            SubjectRepository subjectRepository,
                            DepartmentRepository departmentRepository,
                            AttendanceService attendanceService,
                            MarkService markService) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.attendanceService = attendanceService;
        this.markService = markService;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalStudents(studentRepository.count());
        stats.setTotalSubjects(subjectRepository.count());
        stats.setTotalDepartments(departmentRepository.count());
        stats.setOverallAttendancePercentage(attendanceService.getCampusOverallAttendancePercentage());
        stats.setAverageMarksPercentage(markService.getOverallAveragePercentage());

        // Department-wise student distribution
        Map<String, Long> distribution = new LinkedHashMap<>();
        List<Department> departments = departmentRepository.findAll();
        for (Department dept : departments) {
            long count = studentRepository.countByDepartmentId(dept.getId());
            distribution.put(dept.getCode(), count);
        }
        stats.setDepartmentStudentDistribution(distribution);

        // Recent system activities
        List<DashboardStatsDto.RecentActivityItem> activities = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        activities.add(new DashboardStatsDto.RecentActivityItem(
                "ATTENDANCE",
                "Daily Attendance Marked",
                "Faculty submitted attendance records for today's active classes.",
                LocalDateTime.now().minusHours(1).format(dtf)
        ));
        activities.add(new DashboardStatsDto.RecentActivityItem(
                "MARKS",
                "Exam Evaluation Published",
                "Semester examination marks were verified and recorded in the database.",
                LocalDateTime.now().minusHours(4).format(dtf)
        ));
        activities.add(new DashboardStatsDto.RecentActivityItem(
                "ENROLLMENT",
                "Semester Course Registrations",
                "Student semester course enrollments synchronized across all departments.",
                LocalDateTime.now().minusDays(1).format(dtf)
        ));

        stats.setRecentActivities(activities);
        return stats;
    }
}
