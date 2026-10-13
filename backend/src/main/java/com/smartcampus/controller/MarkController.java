package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.MarkDto;
import com.smartcampus.dto.MarkResponseDto;
import com.smartcampus.service.MarkService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marks")
@PreAuthorize("hasRole('ADMIN')")
public class MarkController {

    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MarkResponseDto>> saveOrUpdateMark(@Valid @RequestBody MarkDto dto) {
        MarkResponseDto saved = markService.saveOrUpdateMark(dto);
        return ResponseEntity.ok(ApiResponse.success("Mark saved and graded successfully", saved));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> getMarksByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(markService.getMarksByStudentId(studentId)));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> getMarksBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(ApiResponse.success(markService.getMarksBySubjectId(subjectId)));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> getFilteredMarks(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String examType) {
        List<MarkResponseDto> results = markService.getFilteredMarks(departmentId, semester, subjectId, examType);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMark(@PathVariable Long id) {
        markService.deleteMark(id);
        return ResponseEntity.ok(ApiResponse.success("Mark record deleted successfully", null));
    }
}
