package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.AttendanceBulkRecordDto;
import com.smartcampus.dto.AttendanceDto;
import com.smartcampus.dto.AttendanceSummaryDto;
import com.smartcampus.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@PreAuthorize("hasRole('ADMIN')")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceDto>> recordSingleAttendance(@Valid @RequestBody AttendanceDto dto) {
        AttendanceDto saved = attendanceService.recordSingleAttendance(dto);
        return new ResponseEntity<>(ApiResponse.success("Attendance recorded successfully", saved), HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> recordBulkAttendance(
            @Valid @RequestBody AttendanceBulkRecordDto bulkDto) {
        List<AttendanceDto> saved = attendanceService.recordBulkAttendance(bulkDto);
        return ResponseEntity.ok(ApiResponse.success("Class attendance saved successfully", saved));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getAttendanceBySubjectAndDate(
            @PathVariable Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDto> records = attendanceService.getAttendanceBySubjectAndDate(subjectId, date);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getStudentAttendance(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getStudentAttendanceHistory(studentId)));
    }

    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<ApiResponse<List<AttendanceSummaryDto>>> getStudentAttendanceSummary(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getStudentAttendanceSummaries(studentId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance entry deleted successfully", null));
    }
}
