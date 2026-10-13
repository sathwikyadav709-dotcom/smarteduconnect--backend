package com.smartcampus.service;

import com.smartcampus.dto.StudentDto;
import com.smartcampus.dto.StudentResponseDto;
import com.smartcampus.entity.Department;
import com.smartcampus.entity.Role;
import com.smartcampus.entity.Student;
import com.smartcampus.entity.User;
import com.smartcampus.exception.BadRequestException;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.repository.DepartmentRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          DepartmentRepository departmentRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public StudentResponseDto createStudent(StudentDto dto) {
        // 1. Validate duplicate roll number
        if (studentRepository.existsByRollNumber(dto.getRollNumber().trim())) {
            throw new BadRequestException("A student with roll number '" + dto.getRollNumber() + "' already exists.");
        }

        // 2. Validate duplicate email
        if (studentRepository.existsByEmail(dto.getEmail().trim())) {
            throw new BadRequestException("A student with email '" + dto.getEmail() + "' already exists.");
        }

        // 3. Find department
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + dto.getDepartmentId()));

        // 4. Optionally create login account for student
        User studentUser = null;
        if (Boolean.TRUE.equals(dto.getCreateLoginAccount())) {
            String initialPass = (dto.getInitialPassword() != null && !dto.getInitialPassword().isBlank())
                    ? dto.getInitialPassword() : "CampusStudent#2026";

            if (userRepository.existsByEmail(dto.getEmail().trim())) {
                studentUser = userRepository.findByEmail(dto.getEmail().trim()).orElse(null);
            } else {
                studentUser = new User(
                        dto.getEmail().trim(),
                        passwordEncoder.encode(initialPass),
                        dto.getFullName().trim(),
                        Role.ROLE_STUDENT
                );
                studentUser = userRepository.save(studentUser);
            }
        }

        // 5. Create student entity
        Student student = new Student();
        student.setUser(studentUser);
        student.setRollNumber(dto.getRollNumber().trim().toUpperCase());
        student.setFullName(dto.getFullName().trim());
        student.setEmail(dto.getEmail().trim());
        student.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        student.setDepartment(department);
        student.setAcademicYear(dto.getAcademicYear());
        student.setSemester(dto.getSemester());
        student.setEnrollmentDate(dto.getEnrollmentDate() != null ? dto.getEnrollmentDate() : LocalDate.now());

        Student saved = studentRepository.save(student);
        return mapToDto(saved);
    }

    @Transactional
    public StudentResponseDto updateStudent(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        // Validate roll number if changed
        if (!student.getRollNumber().equalsIgnoreCase(dto.getRollNumber().trim())) {
            if (studentRepository.existsByRollNumber(dto.getRollNumber().trim())) {
                throw new BadRequestException("A student with roll number '" + dto.getRollNumber() + "' already exists.");
            }
            student.setRollNumber(dto.getRollNumber().trim().toUpperCase());
        }

        // Validate email if changed
        if (!student.getEmail().equalsIgnoreCase(dto.getEmail().trim())) {
            if (studentRepository.existsByEmail(dto.getEmail().trim())) {
                throw new BadRequestException("A student with email '" + dto.getEmail() + "' already exists.");
            }
            student.setEmail(dto.getEmail().trim());

            // If user login account exists, sync email
            if (student.getUser() != null) {
                student.getUser().setEmail(dto.getEmail().trim());
                userRepository.save(student.getUser());
            }
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + dto.getDepartmentId()));

        student.setFullName(dto.getFullName().trim());
        student.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        student.setDepartment(department);
        student.setAcademicYear(dto.getAcademicYear());
        student.setSemester(dto.getSemester());
        if (dto.getEnrollmentDate() != null) {
            student.setEnrollmentDate(dto.getEnrollmentDate());
        }

        Student updated = studentRepository.save(student);
        return mapToDto(updated);
    }

    @Transactional(readOnly = true)
    public StudentResponseDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return mapToDto(student);
    }

    @Transactional(readOnly = true)
    public StudentResponseDto getStudentByRollNumber(String rollNumber) {
        Student student = studentRepository.findByRollNumber(rollNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with roll number: " + rollNumber));
        return mapToDto(student);
    }

    @Transactional(readOnly = true)
    public StudentResponseDto getStudentByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No student profile linked to user ID: " + userId));
        return mapToDto(student);
    }

    @Transactional(readOnly = true)
    public Page<StudentResponseDto> searchStudents(String search, Long deptId, Integer academicYear, Integer semester, Pageable pageable) {
        Page<Student> page = studentRepository.searchStudents(search, deptId, academicYear, semester, pageable);
        return page.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        User user = student.getUser();
        studentRepository.delete(student);

        if (user != null) {
            userRepository.delete(user);
        }
    }

    public StudentResponseDto mapToDto(Student student) {
        StudentResponseDto dto = new StudentResponseDto();
        dto.setId(student.getId());
        dto.setRollNumber(student.getRollNumber());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setAcademicYear(student.getAcademicYear());
        dto.setSemester(student.getSemester());
        dto.setEnrollmentDate(student.getEnrollmentDate());

        if (student.getDepartment() != null) {
            dto.setDepartmentId(student.getDepartment().getId());
            dto.setDepartmentCode(student.getDepartment().getCode());
            dto.setDepartmentName(student.getDepartment().getName());
        }

        if (student.getUser() != null) {
            dto.setUserId(student.getUser().getId());
            dto.setHasLoginAccount(true);
        } else {
            dto.setHasLoginAccount(false);
        }

        return dto;
    }
}
