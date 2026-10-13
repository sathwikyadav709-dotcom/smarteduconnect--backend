package com.smartcampus;

import com.smartcampus.dto.AttendanceDto;
import com.smartcampus.dto.AttendanceSummaryDto;
import com.smartcampus.entity.Department;
import com.smartcampus.entity.Student;
import com.smartcampus.entity.Subject;
import com.smartcampus.repository.DepartmentRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.SubjectRepository;
import com.smartcampus.service.AttendanceService;
import com.smartcampus.service.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AttendanceServiceTest {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SubjectService subjectService;

    private Student student;
    private Subject subject;

    @BeforeEach
    void setUp() {
        Department dept = departmentRepository.findByCode("CSE").orElseGet(() ->
                departmentRepository.save(new Department("CSE", "Computer Science", "CSE Dept")));

        student = studentRepository.save(new Student(null, "ATT_STU_01", "Attendance Test Student",
                "att.student@smartcampus.edu", "9988112233", dept, 2, 3, LocalDate.now()));

        subject = subjectRepository.save(new Subject("ATT_SUB_01", "Software Testing", dept, 3, 3, 100.0));
        subjectService.assignSubjectToStudent(student.getId(), subject.getId());
    }

    @Test
    void testAttendancePercentageCalculation() {
        LocalDate d1 = LocalDate.of(2025, 1, 10);
        LocalDate d2 = LocalDate.of(2025, 1, 11);
        LocalDate d3 = LocalDate.of(2025, 1, 12);
        LocalDate d4 = LocalDate.of(2025, 1, 13);

        // 3 Present, 1 Absent = 3/4 = 75.0%
        record(d1, true);
        record(d2, true);
        record(d3, false);
        record(d4, true);

        List<AttendanceSummaryDto> summaries = attendanceService.getStudentAttendanceSummaries(student.getId());
        assertFalse(summaries.isEmpty());

        AttendanceSummaryDto summary = summaries.stream()
                .filter(s -> s.getSubjectId().equals(subject.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(summary);
        assertEquals(4, summary.getTotalClasses());
        assertEquals(3, summary.getPresentClasses());
        assertEquals(75.0, summary.getAttendancePercentage(), 0.01);
        assertFalse(summary.isBelowThreshold()); // 75.0% is not below threshold 75.0%
    }

    @Test
    void testAttendanceThresholdBelow75Percent() {
        LocalDate d1 = LocalDate.of(2025, 2, 1);
        LocalDate d2 = LocalDate.of(2025, 2, 2);

        // 1 Present, 1 Absent = 1/2 = 50.0% (< 75.0%)
        record(d1, true);
        record(d2, false);

        List<AttendanceSummaryDto> summaries = attendanceService.getStudentAttendanceSummaries(student.getId());
        AttendanceSummaryDto summary = summaries.stream()
                .filter(s -> s.getSubjectId().equals(subject.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(summary);
        assertEquals(2, summary.getTotalClasses());
        assertEquals(1, summary.getPresentClasses());
        assertEquals(50.0, summary.getAttendancePercentage(), 0.01);
        assertTrue(summary.isBelowThreshold());
    }

    @Test
    void testNoDuplicateAttendanceOnSameDate() {
        LocalDate date = LocalDate.of(2025, 3, 1);

        AttendanceDto dto1 = new AttendanceDto();
        dto1.setStudentId(student.getId());
        dto1.setSubjectId(subject.getId());
        dto1.setAttendanceDate(date);
        dto1.setIsPresent(false);
        dto1.setRemarks("Initially Absent");
        attendanceService.recordSingleAttendance(dto1);

        // Update attendance on the same date to Present
        AttendanceDto dto2 = new AttendanceDto();
        dto2.setStudentId(student.getId());
        dto2.setSubjectId(subject.getId());
        dto2.setAttendanceDate(date);
        dto2.setIsPresent(true);
        dto2.setRemarks("Corrected to Present");
        attendanceService.recordSingleAttendance(dto2);

        List<AttendanceDto> history = attendanceService.getStudentAttendanceHistory(student.getId());
        long countForDate = history.stream().filter(a -> a.getAttendanceDate().equals(date)).count();
        assertEquals(1, countForDate, "Duplicate attendance record should not exist for the same student, subject and date");

        AttendanceDto record = history.stream().filter(a -> a.getAttendanceDate().equals(date)).findFirst().orElse(null);
        assertNotNull(record);
        assertTrue(record.getIsPresent());
        assertEquals("Corrected to Present", record.getRemarks());
    }

    private void record(LocalDate date, boolean isPresent) {
        AttendanceDto dto = new AttendanceDto();
        dto.setStudentId(student.getId());
        dto.setSubjectId(subject.getId());
        dto.setAttendanceDate(date);
        dto.setIsPresent(isPresent);
        attendanceService.recordSingleAttendance(dto);
    }
}
