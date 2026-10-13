package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.StudentDto;
import com.smartcampus.dto.StudentResponseDto;
import com.smartcampus.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@PreAuthorize("hasRole('ADMIN')")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentResponseDto>>> searchStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer academicYear,
            @RequestParam(required = false) Integer semester,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rollNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StudentResponseDto> students = studentService.searchStudents(search, departmentId, academicYear, semester, pageable);
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StudentResponseDto>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAllStudents()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDto>> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponseDto>> createStudent(@Valid @RequestBody StudentDto dto) {
        StudentResponseDto created = studentService.createStudent(dto);
        return new ResponseEntity<>(ApiResponse.success("Student created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDto>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentDto dto) {
        StudentResponseDto updated = studentService.updateStudent(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }
}
