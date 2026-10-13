package com.smartcampus.service;

import com.smartcampus.dto.AuthRequest;
import com.smartcampus.dto.AuthResponse;
import com.smartcampus.entity.Role;
import com.smartcampus.entity.Student;
import com.smartcampus.entity.User;
import com.smartcampus.exception.BadRequestException;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.security.JwtTokenProvider;
import com.smartcampus.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       UserRepository userRepository,
                       StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse authenticateUser(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail().trim(),
                        authRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return new AuthResponse(
                jwt,
                principal.getId(),
                principal.getUsername(),
                principal.getFullName(),
                principal.getRole().name(),
                principal.getStudentId(),
                principal.getRollNumber()
        );
    }

    @Transactional
    public User registerUser(String email, String rawPassword, String fullName, Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("User already exists with email: " + email);
        }

        User user = new User(
                email.trim(),
                passwordEncoder.encode(rawPassword),
                fullName.trim(),
                role
        );
        return userRepository.save(user);
    }
}
