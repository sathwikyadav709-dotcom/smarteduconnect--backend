package com.smartcampus.controller;

import com.smartcampus.dto.AcademicTranscriptDto;
import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.AttendanceDto;
import com.smartcampus.dto.MarkResponseDto;
import com.smartcampus.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/transcript/{studentId}")
    public ResponseEntity<ApiResponse<AcademicTranscriptDto>> getStudentTranscript(@PathVariable Long studentId) {
        AcademicTranscriptDto transcript = reportService.getStudentTranscript(studentId);
        return ResponseEntity.ok(ApiResponse.success(transcript));
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getAttendanceReport(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDto> report = reportService.getAttendanceReport(departmentId, subjectId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/marks")
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> getMarksReport(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String examType) {
        List<MarkResponseDto> report = reportService.getMarksReport(departmentId, semester, subjectId, examType);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDepartmentSummaryReport() {
        List<Map<String, Object>> summary = reportService.getDepartmentSummaryReport();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
