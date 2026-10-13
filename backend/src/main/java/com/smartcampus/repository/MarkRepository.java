package com.smartcampus.repository;

import com.smartcampus.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {

    List<Mark> findByStudentId(Long studentId);

    List<Mark> findBySubjectId(Long subjectId);

    List<Mark> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    List<Mark> findByStudentIdAndSemester(Long studentId, Integer semester);

    Optional<Mark> findByStudentIdAndSubjectIdAndExamType(Long studentId, Long subjectId, String examType);

    boolean existsByStudentIdAndSubjectIdAndExamType(Long studentId, Long subjectId, String examType);

    @Query("SELECT AVG(m.percentage) FROM Mark m")
    Double getOverallAveragePercentage();

    @Query("SELECT m FROM Mark m WHERE " +
           "(:deptId IS NULL OR m.student.department.id = :deptId) AND " +
           "(:semester IS NULL OR m.semester = :semester) AND " +
           "(:subjectId IS NULL OR m.subject.id = :subjectId) AND " +
           "(:examType IS NULL OR m.examType = :examType) " +
           "ORDER BY m.student.rollNumber ASC")
    List<Mark> findFilteredMarks(
            @Param("deptId") Long deptId,
            @Param("semester") Integer semester,
            @Param("subjectId") Long subjectId,
            @Param("examType") String examType
    );
}
