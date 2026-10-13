package com.smartcampus.repository;

import com.smartcampus.entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {

    List<StudentSubject> findByStudentId(Long studentId);

    List<StudentSubject> findBySubjectId(Long subjectId);

    Optional<StudentSubject> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    boolean existsByStudentIdAndSubjectId(Long studentId, Long subjectId);

    void deleteByStudentIdAndSubjectId(Long studentId, Long subjectId);
}
