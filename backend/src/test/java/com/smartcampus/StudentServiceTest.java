package com.smartcampus;

import com.smartcampus.dto.StudentDto;
import com.smartcampus.dto.StudentResponseDto;
import com.smartcampus.entity.Department;
import com.smartcampus.exception.BadRequestException;
import com.smartcampus.repository.DepartmentRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department dept;

    @BeforeEach
    void setUp() {
        dept = departmentRepository.findByCode("CSE").orElseGet(() ->
                departmentRepository.save(new Department("CSE", "Computer Science", "CSE Dept")));
    }

    @Test
    void testCreateStudentSuccess() {
        StudentDto dto = new StudentDto();
        dto.setRollNumber("TEST_ROLL_101");
        dto.setFullName("John Doe");
        dto.setEmail("john.doe.test@smartcampus.edu");
        dto.setPhone("9988776655");
        dto.setDepartmentId(dept.getId());
        dto.setAcademicYear(2);
        dto.setSemester(3);
        dto.setEnrollmentDate(LocalDate.now());
        dto.setCreateLoginAccount(false);

        StudentResponseDto created = studentService.createStudent(dto);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("TEST_ROLL_101", created.getRollNumber());
        assertEquals("John Doe", created.getFullName());
    }

    @Test
    void testDuplicateRollNumberThrowsException() {
        StudentDto dto1 = new StudentDto();
        dto1.setRollNumber("TEST_DUP_ROLL");
        dto1.setFullName("Student One");
        dto1.setEmail("stu1@smartcampus.edu");
        dto1.setDepartmentId(dept.getId());
        dto1.setAcademicYear(1);
        dto1.setSemester(1);
        dto1.setEnrollmentDate(LocalDate.now());
        dto1.setCreateLoginAccount(false);

        studentService.createStudent(dto1);

        StudentDto dto2 = new StudentDto();
        dto2.setRollNumber("TEST_DUP_ROLL");
        dto2.setFullName("Student Two");
        dto2.setEmail("stu2@smartcampus.edu");
        dto2.setDepartmentId(dept.getId());
        dto2.setAcademicYear(1);
        dto2.setSemester(1);
        dto2.setEnrollmentDate(LocalDate.now());
        dto2.setCreateLoginAccount(false);

        assertThrows(BadRequestException.class, () -> studentService.createStudent(dto2));
    }

    @Test
    void testDuplicateEmailThrowsException() {
        StudentDto dto1 = new StudentDto();
        dto1.setRollNumber("ROLL_EMAIL_1");
        dto1.setFullName("Student One");
        dto1.setEmail("same.email@smartcampus.edu");
        dto1.setDepartmentId(dept.getId());
        dto1.setAcademicYear(1);
        dto1.setSemester(1);
        dto1.setEnrollmentDate(LocalDate.now());
        dto1.setCreateLoginAccount(false);

        studentService.createStudent(dto1);

        StudentDto dto2 = new StudentDto();
        dto2.setRollNumber("ROLL_EMAIL_2");
        dto2.setFullName("Student Two");
        dto2.setEmail("same.email@smartcampus.edu");
        dto2.setDepartmentId(dept.getId());
        dto2.setAcademicYear(1);
        dto2.setSemester(1);
        dto2.setEnrollmentDate(LocalDate.now());
        dto2.setCreateLoginAccount(false);

        assertThrows(BadRequestException.class, () -> studentService.createStudent(dto2));
    }
}
