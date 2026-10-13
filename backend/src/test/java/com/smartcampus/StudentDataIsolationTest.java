package com.smartcampus;

import com.smartcampus.entity.Department;
import com.smartcampus.entity.Role;
import com.smartcampus.entity.Student;
import com.smartcampus.entity.User;
import com.smartcampus.repository.DepartmentRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.security.JwtTokenProvider;
import com.smartcampus.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudentDataIsolationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String student1Token;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        Department dept = departmentRepository.findByCode("CSE").orElseGet(() ->
                departmentRepository.save(new Department("CSE", "Computer Science", "CSE Dept")));

        // Create Student 1
        User user1 = userRepository.save(new User("stu1.isolation@smartcampus.edu",
                passwordEncoder.encode("Secret123"), "Isolation Student One", Role.ROLE_STUDENT));
        student1 = studentRepository.save(new Student(user1, "ISO_001", "Isolation Student One",
                "stu1.isolation@smartcampus.edu", "9111111111", dept, 3, 5, LocalDate.now()));

        // Create Student 2
        User user2 = userRepository.save(new User("stu2.isolation@smartcampus.edu",
                passwordEncoder.encode("Secret123"), "Isolation Student Two", Role.ROLE_STUDENT));
        student2 = studentRepository.save(new Student(user2, "ISO_002", "Isolation Student Two",
                "stu2.isolation@smartcampus.edu", "9222222222", dept, 3, 5, LocalDate.now()));

        UserPrincipal principal1 = UserPrincipal.create(user1, student1.getId(), student1.getRollNumber());
        student1Token = tokenProvider.generateTokenForUser(principal1);
    }

    @Test
    void testStudentCanOnlyAccessOwnProfile() throws Exception {
        // Calling /api/portal/student/profile should return ONLY student1's details
        mockMvc.perform(get("/api/portal/student/profile")
                        .header("Authorization", "Bearer " + student1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rollNumber").value("ISO_001"))
                .andExpect(jsonPath("$.data.fullName").value("Isolation Student One"));
    }

    @Test
    void testStudentCannotAccessAdminEndpoints() throws Exception {
        // Student token trying to access Admin Student Management endpoint should receive 403 Forbidden
        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer " + student1Token))
                .andExpect(status().isForbidden());
    }
}
