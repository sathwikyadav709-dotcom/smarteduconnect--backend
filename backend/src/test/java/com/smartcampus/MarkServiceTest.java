package com.smartcampus;

import com.smartcampus.dto.MarkDto;
import com.smartcampus.dto.MarkResponseDto;
import com.smartcampus.entity.Department;
import com.smartcampus.entity.Mark;
import com.smartcampus.entity.Student;
import com.smartcampus.entity.Subject;
import com.smartcampus.exception.BadRequestException;
import com.smartcampus.repository.DepartmentRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.SubjectRepository;
import com.smartcampus.service.MarkService;
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
class MarkServiceTest {

    @Autowired
    private MarkService markService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Student student;
    private Subject subject;

    @BeforeEach
    void setUp() {
        Department dept = departmentRepository.findByCode("CSE").orElseGet(() ->
                departmentRepository.save(new Department("CSE", "Computer Science", "CSE Dept")));

        student = studentRepository.save(new Student(null, "MARK_STU_01", "Marks Test Student",
                "mark.stu@smartcampus.edu", "9988112244", dept, 3, 5, LocalDate.now()));

        subject = subjectRepository.save(new Subject("CS_MARK_01", "Operating Systems", dept, 5, 4, 100.0));
    }

    @Test
    void testMarksObtainedExceedingMaxMarksThrowsException() {
        MarkDto dto = new MarkDto();
        dto.setStudentId(student.getId());
        dto.setSubjectId(subject.getId());
        dto.setExamType("FINAL");
        dto.setSemester(5);
        dto.setMaxMarks(100.0);
        dto.setMarksObtained(105.0); // Exceeds 100.0

        assertThrows(BadRequestException.class, () -> markService.saveOrUpdateMark(dto));
    }

    @Test
    void testNegativeMarksThrowsException() {
        MarkDto dto = new MarkDto();
        dto.setStudentId(student.getId());
        dto.setSubjectId(subject.getId());
        dto.setExamType("FINAL");
        dto.setSemester(5);
        dto.setMaxMarks(100.0);
        dto.setMarksObtained(-10.0);

        assertThrows(BadRequestException.class, () -> markService.saveOrUpdateMark(dto));
    }

    @Test
    void testGradeAssignmentCalculations() {
        assertEquals("A+", Mark.computeGrade(95.0));
        assertEquals("A+", Mark.computeGrade(90.0));
        assertEquals("A", Mark.computeGrade(85.0));
        assertEquals("A", Mark.computeGrade(80.0));
        assertEquals("B", Mark.computeGrade(75.0));
        assertEquals("B", Mark.computeGrade(70.0));
        assertEquals("C", Mark.computeGrade(65.0));
        assertEquals("D", Mark.computeGrade(55.0));
        assertEquals("E", Mark.computeGrade(45.0));
        assertEquals("F", Mark.computeGrade(35.0));
    }

    @Test
    void testSaveMarkSuccessAndCalculation() {
        MarkDto dto = new MarkDto();
        dto.setStudentId(student.getId());
        dto.setSubjectId(subject.getId());
        dto.setExamType("FINAL");
        dto.setSemester(5);
        dto.setMaxMarks(100.0);
        dto.setMarksObtained(85.5);
        dto.setExamDate(LocalDate.now());

        MarkResponseDto saved = markService.saveOrUpdateMark(dto);
        assertNotNull(saved);
        assertEquals(85.5, saved.getMarksObtained());
        assertEquals(85.5, saved.getPercentage());
        assertEquals("A", saved.getGrade());
    }
}
