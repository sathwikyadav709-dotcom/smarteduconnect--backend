package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.SubjectDto;
import com.smartcampus.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getAllSubjects() {
        return ResponseEntity.ok(ApiResponse.success(subjectService.getAllSubjects()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectDto>> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(subjectService.getSubjectById(id)));
    }

    @GetMapping("/department/{deptId}")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getSubjectsByDepartment(@PathVariable Long deptId) {
        return ResponseEntity.ok(ApiResponse.success(subjectService.getSubjectsByDepartment(deptId)));
    }

    @GetMapping("/department/{deptId}/semester/{sem}")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getSubjectsByDepartmentAndSemester(
            @PathVariable Long deptId, @PathVariable Integer sem) {
        return ResponseEntity.ok(ApiResponse.success(subjectService.getSubjectsByDepartmentAndSemester(deptId, sem)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectDto>> createSubject(@Valid @RequestBody SubjectDto dto) {
        SubjectDto created = subjectService.createSubject(dto);
        return new ResponseEntity<>(ApiResponse.success("Subject created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectDto>> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectDto dto) {
        SubjectDto updated = subjectService.updateSubject(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Subject updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully", null));
    }

    @PostMapping("/{subjectId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignSubjectToStudent(
            @PathVariable Long subjectId, @PathVariable Long studentId) {
        subjectService.assignSubjectToStudent(studentId, subjectId);
        return ResponseEntity.ok(ApiResponse.success("Subject assigned to student successfully", null));
    }

    @DeleteMapping("/{subjectId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> unassignSubjectFromStudent(
            @PathVariable Long subjectId, @PathVariable Long studentId) {
        subjectService.unassignSubjectFromStudent(studentId, subjectId);
        return ResponseEntity.ok(ApiResponse.success("Subject unassigned from student successfully", null));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getEnrolledSubjectsForStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(subjectService.getEnrolledSubjectsForStudent(studentId)));
    }
}
