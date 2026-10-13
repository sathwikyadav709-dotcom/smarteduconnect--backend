package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.DepartmentDto;
import com.smartcampus.entity.Department;
import com.smartcampus.repository.DepartmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllDepartments() {
        List<DepartmentDto> list = departmentRepository.findAll().stream()
                .map(d -> new DepartmentDto(d.getId(), d.getCode(), d.getName(), d.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
