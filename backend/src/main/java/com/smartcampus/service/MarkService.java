package com.smartcampus.service;

import com.smartcampus.dto.MarkDto;
import com.smartcampus.dto.MarkResponseDto;
import com.smartcampus.entity.Mark;
import com.smartcampus.entity.Student;
import com.smartcampus.entity.Subject;
import com.smartcampus.exception.BadRequestException;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.repository.MarkRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarkService {

    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public MarkService(MarkRepository markRepository,
                       StudentRepository studentRepository,
                       SubjectRepository subjectRepository) {
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public MarkResponseDto saveOrUpdateMark(MarkDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + dto.getStudentId()));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + dto.getSubjectId()));

        double maxMarks = (dto.getMaxMarks() != null && dto.getMaxMarks() > 0)
                ? dto.getMaxMarks() : subject.getMaxMarks();

        // Validation against maximum marks
        if (dto.getMarksObtained() < 0) {
            throw new BadRequestException("Marks obtained cannot be negative.");
        }
        if (dto.getMarksObtained() > maxMarks) {
            throw new BadRequestException("Marks obtained (" + dto.getMarksObtained() +
                    ") cannot exceed maximum marks (" + maxMarks + ") for subject " + subject.getCode());
        }

        String examType = (dto.getExamType() != null && !dto.getExamType().isBlank())
                ? dto.getExamType().trim().toUpperCase() : "FINAL";

        Optional<Mark> existingOpt = markRepository.findByStudentIdAndSubjectIdAndExamType(
                dto.getStudentId(), dto.getSubjectId(), examType);

        Mark mark;
        if (existingOpt.isPresent()) {
            mark = existingOpt.get();
            mark.setSemester(dto.getSemester() != null ? dto.getSemester() : subject.getSemester());
            mark.setMaxMarks(maxMarks);
            mark.setMarksObtained(dto.getMarksObtained());
            if (dto.getExamDate() != null) {
                mark.setExamDate(dto.getExamDate());
            }
            mark.calculatePercentageAndGrade();
        } else {
            mark = new Mark(
                    student,
                    subject,
                    examType,
                    dto.getSemester() != null ? dto.getSemester() : subject.getSemester(),
                    dto.getMarksObtained(),
                    maxMarks,
                    dto.getExamDate() != null ? dto.getExamDate() : LocalDate.now()
            );
        }

        Mark saved = markRepository.save(mark);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<MarkResponseDto> getMarksByStudentId(Long studentId) {
        return markRepository.findByStudentId(studentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MarkResponseDto> getMarksBySubjectId(Long subjectId) {
        return markRepository.findBySubjectId(subjectId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MarkResponseDto> getFilteredMarks(Long deptId, Integer semester, Long subjectId, String examType) {
        return markRepository.findFilteredMarks(deptId, semester, subjectId, examType).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMark(Long id) {
        if (!markRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mark record not found with ID: " + id);
        }
        markRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Double getOverallAveragePercentage() {
        Double avg = markRepository.getOverallAveragePercentage();
        return (avg != null) ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    public MarkResponseDto mapToDto(Mark mark) {
        MarkResponseDto dto = new MarkResponseDto();
        dto.setId(mark.getId());
        dto.setExamType(mark.getExamType());
        dto.setSemester(mark.getSemester());
        dto.setMarksObtained(mark.getMarksObtained());
        dto.setMaxMarks(mark.getMaxMarks());
        dto.setPercentage(mark.getPercentage());
        dto.setGrade(mark.getGrade());
        dto.setExamDate(mark.getExamDate());

        if (mark.getStudent() != null) {
            dto.setStudentId(mark.getStudent().getId());
            dto.setStudentName(mark.getStudent().getFullName());
            dto.setRollNumber(mark.getStudent().getRollNumber());
        }

        if (mark.getSubject() != null) {
            dto.setSubjectId(mark.getSubject().getId());
            dto.setSubjectCode(mark.getSubject().getCode());
            dto.setSubjectName(mark.getSubject().getName());
        }

        return dto;
    }
}
