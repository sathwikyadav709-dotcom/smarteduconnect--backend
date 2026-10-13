package com.smartcampus.controller;

import com.smartcampus.dto.*;
import com.smartcampus.entity.Student;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.UnauthorizedException;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.security.UserPrincipal;
import com.smartcampus.service.AttendanceService;
import com.smartcampus.service.MarkService;
import com.smartcampus.service.ReportService;
import com.smartcampus.service.StudentService;
import com.smartcampus.service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/portal/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentPortalController {

    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final AttendanceService attendanceService;
    private final MarkService markService;
    private final ReportService reportService;

    public StudentPortalController(StudentRepository studentRepository,
                                  StudentService studentService,
                                  SubjectService subjectService,
                                  AttendanceService attendanceService,
                                  MarkService markService,
                                  ReportService reportService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
        this.subjectService = subjectService;
        this.attendanceService = attendanceService;
        this.markService = markService;
        this.reportService = reportService;
    }

    private Long resolveStudentId(UserPrincipal principal) {
        if (principal == null) {
            throw new UnauthorizedException("User session is invalid or expired.");
        }
        if (principal.getStudentId() != null) {
            return principal.getStudentId();
        }
        Student student = studentRepository.findByUserId(principal.getId())
                .or(() -> studentRepository.findByEmail(principal.getUsername()))
                .orElseThrow(() -> new ResourceNotFoundException("No student academic record found for current user account."));
        return student.getId();
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StudentResponseDto>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long studentId = resolveStudentId(principal);
        StudentResponseDto profile = studentService.getStudentById(studentId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getMySubjects(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long studentId = resolveStudentId(principal);
        List<SubjectDto> subjects = subjectService.getEnrolledSubjectsForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(subjects));
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getMyAttendance(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long studentId = resolveStudentId(principal);
        List<AttendanceDto> attendance = attendanceService.getStudentAttendanceHistory(studentId);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }

    @GetMapping("/attendance/summary")
    public ResponseEntity<ApiResponse<List<AttendanceSummaryDto>>> getMyAttendanceSummary(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long studentId = resolveStudentId(principal);
        List<AttendanceSummaryDto> summary = attendanceService.getStudentAttendanceSummaries(studentId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/marks")
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> getMyMarks(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long studentId = resolveStudentId(principal);
        List<MarkResponseDto> marks = markService.getMarksByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success(marks));
    }

    @GetMapping("/transcript")
    public ResponseEntity<ApiResponse<AcademicTranscriptDto>> getMyTranscript(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long studentId = resolveStudentId(principal);
        AcademicTranscriptDto transcript = reportService.getStudentTranscript(studentId);
        return ResponseEntity.ok(ApiResponse.success(transcript));
    }
}
