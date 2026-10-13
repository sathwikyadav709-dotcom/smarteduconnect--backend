package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.AuthRequest;
import com.smartcampus.dto.AuthResponse;
import com.smartcampus.security.UserPrincipal;
import com.smartcampus.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("User not authenticated"));
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userPrincipal.getId());
        userData.put("email", userPrincipal.getUsername());
        userData.put("fullName", userPrincipal.getFullName());
        userData.put("role", userPrincipal.getRole().name());
        userData.put("studentId", userPrincipal.getStudentId());
        userData.put("rollNumber", userPrincipal.getRollNumber());

        return ResponseEntity.ok(ApiResponse.success(userData));
    }
}
