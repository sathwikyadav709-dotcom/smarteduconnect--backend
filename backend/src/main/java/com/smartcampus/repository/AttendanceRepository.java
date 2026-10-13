package com.smartcampus.repository;

import com.smartcampus.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findBySubjectId(Long subjectId);

    List<Attendance> findBySubjectIdAndAttendanceDate(Long subjectId, LocalDate attendanceDate);

    Optional<Attendance> findByStudentIdAndSubjectIdAndAttendanceDate(Long studentId, Long subjectId, LocalDate attendanceDate);

    boolean existsByStudentIdAndSubjectIdAndAttendanceDate(Long studentId, Long subjectId, LocalDate attendanceDate);

    long countByStudentId(Long studentId);

    long countByStudentIdAndIsPresentTrue(Long studentId);

    long countByStudentIdAndSubjectId(Long studentId, Long subjectId);

    long countByStudentIdAndSubjectIdAndIsPresentTrue(Long studentId, Long subjectId);

    long countByIsPresentTrue();

    @Query("SELECT a FROM Attendance a WHERE " +
           "(:deptId IS NULL OR a.student.department.id = :deptId) AND " +
           "(:subjectId IS NULL OR a.subject.id = :subjectId) AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) " +
           "ORDER BY a.attendanceDate DESC")
    List<Attendance> findFilteredAttendance(
            @Param("deptId") Long deptId,
            @Param("subjectId") Long subjectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
