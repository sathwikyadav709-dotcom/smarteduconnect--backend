package com.smartcampus.service;

import com.smartcampus.dto.AttendanceBulkRecordDto;
import com.smartcampus.dto.AttendanceDto;
import com.smartcampus.dto.AttendanceSummaryDto;
import com.smartcampus.entity.Attendance;
import com.smartcampus.entity.Student;
import com.smartcampus.entity.Subject;
import com.smartcampus.exception.BadRequestException;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.repository.AttendanceRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.StudentSubjectRepository;
import com.smartcampus.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    @Value("${app.attendance.threshold-percentage:75.0}")
    private double thresholdPercentage;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             StudentRepository studentRepository,
                             SubjectRepository subjectRepository,
                             StudentSubjectRepository studentSubjectRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.studentSubjectRepository = studentSubjectRepository;
    }

    public double getThresholdPercentage() {
        return thresholdPercentage;
    }

    @Transactional
    public AttendanceDto recordSingleAttendance(AttendanceDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + dto.getStudentId()));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + dto.getSubjectId()));

        Optional<Attendance> existing = attendanceRepository.findByStudentIdAndSubjectIdAndAttendanceDate(
                dto.getStudentId(), dto.getSubjectId(), dto.getAttendanceDate());

        Attendance record;
        if (existing.isPresent()) {
            record = existing.get();
            record.setIsPresent(dto.getIsPresent());
            record.setRemarks(dto.getRemarks());
        } else {
            record = new Attendance(student, subject, dto.getAttendanceDate(), dto.getIsPresent(), dto.getRemarks());
        }

        Attendance saved = attendanceRepository.save(record);
        return mapToDto(saved);
    }

    @Transactional
    public List<AttendanceDto> recordBulkAttendance(AttendanceBulkRecordDto bulkDto) {
        Subject subject = subjectRepository.findById(bulkDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + bulkDto.getSubjectId()));

        List<AttendanceDto> results = new ArrayList<>();

        for (AttendanceBulkRecordDto.StudentAttendanceItem item : bulkDto.getRecords()) {
            Student student = studentRepository.findById(item.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + item.getStudentId()));

            Optional<Attendance> existing = attendanceRepository.findByStudentIdAndSubjectIdAndAttendanceDate(
                    student.getId(), subject.getId(), bulkDto.getAttendanceDate());

            Attendance record;
            if (existing.isPresent()) {
                record = existing.get();
                record.setIsPresent(item.getIsPresent());
                record.setRemarks(item.getRemarks());
            } else {
                record = new Attendance(student, subject, bulkDto.getAttendanceDate(), item.getIsPresent(), item.getRemarks());
            }

            Attendance saved = attendanceRepository.save(record);
            results.add(mapToDto(saved));
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceBySubjectAndDate(Long subjectId, LocalDate date) {
        return attendanceRepository.findBySubjectIdAndAttendanceDate(subjectId, date).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto> getStudentAttendanceHistory(Long studentId) {
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceSummaryDto> getStudentAttendanceSummaries(Long studentId) {
        List<Subject> subjects = studentSubjectRepository.findByStudentId(studentId).stream()
                .map(enrollment -> enrollment.getSubject())
                .collect(Collectors.toList());

        List<AttendanceSummaryDto> summaries = new ArrayList<>();
        for (Subject subject : subjects) {
            long total = attendanceRepository.countByStudentIdAndSubjectId(studentId, subject.getId());
            long present = attendanceRepository.countByStudentIdAndSubjectIdAndIsPresentTrue(studentId, subject.getId());

            double percentage = 0.0;
            if (total > 0) {
                percentage = Math.round(((double) present / total) * 10000.0) / 100.0;
            }

            boolean belowThreshold = total > 0 && percentage < thresholdPercentage;

            summaries.add(new AttendanceSummaryDto(
                    subject.getId(),
                    subject.getCode(),
                    subject.getName(),
                    total,
                    present,
                    percentage,
                    belowThreshold
            ));
        }

        return summaries;
    }

    @Transactional(readOnly = true)
    public double getStudentOverallAttendancePercentage(Long studentId) {
        long total = attendanceRepository.countByStudentId(studentId);
        if (total == 0) return 0.0;
        long present = attendanceRepository.countByStudentIdAndIsPresentTrue(studentId);
        return Math.round(((double) present / total) * 10000.0) / 100.0;
    }

    @Transactional(readOnly = true)
    public double getCampusOverallAttendancePercentage() {
        long total = attendanceRepository.count();
        if (total == 0) return 0.0;
        long present = attendanceRepository.countByIsPresentTrue();
        return Math.round(((double) present / total) * 10000.0) / 100.0;
    }

    @Transactional
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance record not found with ID: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    public AttendanceDto mapToDto(Attendance attendance) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(attendance.getId());
        dto.setAttendanceDate(attendance.getAttendanceDate());
        dto.setIsPresent(attendance.getIsPresent());
        dto.setRemarks(attendance.getRemarks());

        if (attendance.getStudent() != null) {
            dto.setStudentId(attendance.getStudent().getId());
            dto.setStudentName(attendance.getStudent().getFullName());
            dto.setRollNumber(attendance.getStudent().getRollNumber());
        }

        if (attendance.getSubject() != null) {
            dto.setSubjectId(attendance.getSubject().getId());
            dto.setSubjectCode(attendance.getSubject().getCode());
            dto.setSubjectName(attendance.getSubject().getName());
        }

        return dto;
    }
}
