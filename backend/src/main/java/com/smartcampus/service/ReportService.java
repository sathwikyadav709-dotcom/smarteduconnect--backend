package com.smartcampus.service;

import com.smartcampus.dto.AcademicTranscriptDto;
import com.smartcampus.dto.AttendanceDto;
import com.smartcampus.dto.MarkResponseDto;
import com.smartcampus.entity.Attendance;
import com.smartcampus.entity.Department;
import com.smartcampus.entity.Mark;
import com.smartcampus.entity.Student;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.repository.AttendanceRepository;
import com.smartcampus.repository.DepartmentRepository;
import com.smartcampus.repository.MarkRepository;
import com.smartcampus.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceService attendanceService;
    private final MarkService markService;

    public ReportService(StudentRepository studentRepository,
                         AttendanceRepository attendanceRepository,
                         MarkRepository markRepository,
                         DepartmentRepository departmentRepository,
                         AttendanceService attendanceService,
                         MarkService markService) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
        this.departmentRepository = departmentRepository;
        this.attendanceService = attendanceService;
        this.markService = markService;
    }

    @Transactional(readOnly = true)
    public AcademicTranscriptDto getStudentTranscript(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        AcademicTranscriptDto transcript = new AcademicTranscriptDto();
        transcript.setStudentId(student.getId());
        transcript.setRollNumber(student.getRollNumber());
        transcript.setFullName(student.getFullName());
        transcript.setEmail(student.getEmail());
        transcript.setDepartmentName(student.getDepartment() != null ? student.getDepartment().getName() : "N/A");
        transcript.setAcademicYear(student.getAcademicYear());
        transcript.setCurrentSemester(student.getSemester());

        // Attendance %
        double attendancePercent = attendanceService.getStudentOverallAttendancePercentage(studentId);
        transcript.setAttendancePercentage(attendancePercent);

        // Marks grouped by semester
        List<Mark> marks = markRepository.findByStudentId(studentId);

        Map<Integer, List<Mark>> bySemester = marks.stream()
                .collect(Collectors.groupingBy(Mark::getSemester, TreeMap::new, Collectors.toList()));

        List<AcademicTranscriptDto.TranscriptSemesterDto> semesterDtos = new ArrayList<>();
        int totalCredits = 0;
        double totalWeightedPoints = 0.0;
        double totalPercentageSum = 0.0;
        int totalSubjectsCount = 0;

        for (Map.Entry<Integer, List<Mark>> entry : bySemester.entrySet()) {
            Integer sem = entry.getKey();
            List<Mark> semMarks = entry.getValue();

            int semCredits = 0;
            double semWeightedPoints = 0.0;
            List<AcademicTranscriptDto.TranscriptSubjectItem> subjectItems = new ArrayList<>();

            for (Mark m : semMarks) {
                int credits = (m.getSubject() != null && m.getSubject().getCredits() != null)
                        ? m.getSubject().getCredits() : 3;
                double gradePoint = convertGradeToPoint(m.getGrade());

                semCredits += credits;
                semWeightedPoints += (gradePoint * credits);

                totalCredits += credits;
                totalWeightedPoints += (gradePoint * credits);
                totalPercentageSum += m.getPercentage();
                totalSubjectsCount++;

                subjectItems.add(new AcademicTranscriptDto.TranscriptSubjectItem(
                        m.getSubject() != null ? m.getSubject().getCode() : "N/A",
                        m.getSubject() != null ? m.getSubject().getName() : "N/A",
                        credits,
                        m.getMarksObtained(),
                        m.getMaxMarks(),
                        m.getPercentage(),
                        m.getGrade(),
                        gradePoint
                ));
            }

            double semGpa = (semCredits > 0) ? Math.round((semWeightedPoints / semCredits) * 100.0) / 100.0 : 0.0;
            semesterDtos.add(new AcademicTranscriptDto.TranscriptSemesterDto(sem, semGpa, subjectItems));
        }

        transcript.setSemesterTranscripts(semesterDtos);
        transcript.setTotalCredits(totalCredits);

        double overallPercent = (totalSubjectsCount > 0)
                ? Math.round((totalPercentageSum / totalSubjectsCount) * 100.0) / 100.0 : 0.0;
        transcript.setOverallPercentage(overallPercent);

        double cgpa = (totalCredits > 0)
                ? Math.round((totalWeightedPoints / totalCredits) * 100.0) / 100.0 : 0.0;
        transcript.setCgpa(cgpa);
        transcript.setOverallGrade(totalSubjectsCount > 0 ? Mark.computeGrade(overallPercent) : "N/A");

        return transcript;
    }

    private double convertGradeToPoint(String grade) {
        if (grade == null) return 0.0;
        switch (grade.toUpperCase()) {
            case "A+": return 10.0;
            case "A": return 9.0;
            case "B": return 8.0;
            case "C": return 7.0;
            case "D": return 6.0;
            case "E": return 5.0;
            default: return 0.0;
        }
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceReport(Long deptId, Long subjectId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findFilteredAttendance(deptId, subjectId, startDate, endDate);
        return attendances.stream().map(attendanceService::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MarkResponseDto> getMarksReport(Long deptId, Integer semester, Long subjectId, String examType) {
        return markService.getFilteredMarks(deptId, semester, subjectId, examType);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDepartmentSummaryReport() {
        List<Department> departments = departmentRepository.findAll();
        List<Map<String, Object>> summaries = new ArrayList<>();

        for (Department dept : departments) {
            Map<String, Object> map = new HashMap<>();
            long studentCount = studentRepository.countByDepartmentId(dept.getId());
            map.put("departmentId", dept.getId());
            map.put("code", dept.getCode());
            map.put("name", dept.getName());
            map.put("studentCount", studentCount);
            summaries.add(map);
        }

        return summaries;
    }
}
