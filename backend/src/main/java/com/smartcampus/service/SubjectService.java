package com.smartcampus.service;

import com.smartcampus.dto.SubjectDto;
import com.smartcampus.entity.Department;
import com.smartcampus.entity.Student;
import com.smartcampus.entity.StudentSubject;
import com.smartcampus.entity.Subject;
import com.smartcampus.exception.BadRequestException;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.repository.DepartmentRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.StudentSubjectRepository;
import com.smartcampus.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    public SubjectService(SubjectRepository subjectRepository,
                          DepartmentRepository departmentRepository,
                          StudentRepository studentRepository,
                          StudentSubjectRepository studentSubjectRepository) {
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
        this.studentSubjectRepository = studentSubjectRepository;
    }

    @Transactional
    public SubjectDto createSubject(SubjectDto dto) {
        if (subjectRepository.existsByCode(dto.getCode().trim().toUpperCase())) {
            throw new BadRequestException("Subject with code '" + dto.getCode() + "' already exists.");
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + dto.getDepartmentId()));

        Subject subject = new Subject();
        subject.setCode(dto.getCode().trim().toUpperCase());
        subject.setName(dto.getName().trim());
        subject.setDepartment(department);
        subject.setSemester(dto.getSemester());
        subject.setCredits(dto.getCredits());
        subject.setMaxMarks(dto.getMaxMarks() != null ? dto.getMaxMarks() : 100.0);

        Subject saved = subjectRepository.save(subject);
        return mapToDto(saved);
    }

    @Transactional
    public SubjectDto updateSubject(Long id, SubjectDto dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));

        if (!subject.getCode().equalsIgnoreCase(dto.getCode().trim())) {
            if (subjectRepository.existsByCode(dto.getCode().trim().toUpperCase())) {
                throw new BadRequestException("Subject with code '" + dto.getCode() + "' already exists.");
            }
            subject.setCode(dto.getCode().trim().toUpperCase());
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + dto.getDepartmentId()));

        subject.setName(dto.getName().trim());
        subject.setDepartment(department);
        subject.setSemester(dto.getSemester());
        subject.setCredits(dto.getCredits());
        if (dto.getMaxMarks() != null) {
            subject.setMaxMarks(dto.getMaxMarks());
        }

        Subject updated = subjectRepository.save(subject);
        return mapToDto(updated);
    }

    @Transactional(readOnly = true)
    public SubjectDto getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
        return mapToDto(subject);
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getSubjectsByDepartment(Long deptId) {
        return subjectRepository.findByDepartmentId(deptId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getSubjectsByDepartmentAndSemester(Long deptId, Integer semester) {
        return subjectRepository.findByDepartmentIdAndSemester(deptId, semester).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with ID: " + id);
        }
        subjectRepository.deleteById(id);
    }

    @Transactional
    public void assignSubjectToStudent(Long studentId, Long subjectId) {
        if (studentSubjectRepository.existsByStudentIdAndSubjectId(studentId, subjectId)) {
            throw new BadRequestException("Student is already enrolled in this subject.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + subjectId));

        StudentSubject enrollment = new StudentSubject(student, subject, LocalDate.now(), "ACTIVE");
        studentSubjectRepository.save(enrollment);
    }

    @Transactional
    public void unassignSubjectFromStudent(Long studentId, Long subjectId) {
        StudentSubject enrollment = studentSubjectRepository.findByStudentIdAndSubjectId(studentId, subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment record not found for student and subject."));
        studentSubjectRepository.delete(enrollment);
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getEnrolledSubjectsForStudent(Long studentId) {
        return studentSubjectRepository.findByStudentId(studentId).stream()
                .map(enrollment -> mapToDto(enrollment.getSubject()))
                .collect(Collectors.toList());
    }

    public SubjectDto mapToDto(Subject subject) {
        SubjectDto dto = new SubjectDto();
        dto.setId(subject.getId());
        dto.setCode(subject.getCode());
        dto.setName(subject.getName());
        dto.setSemester(subject.getSemester());
        dto.setCredits(subject.getCredits());
        dto.setMaxMarks(subject.getMaxMarks());

        if (subject.getDepartment() != null) {
            dto.setDepartmentId(subject.getDepartment().getId());
            dto.setDepartmentCode(subject.getDepartment().getCode());
            dto.setDepartmentName(subject.getDepartment().getName());
        }

        return dto;
    }
}
