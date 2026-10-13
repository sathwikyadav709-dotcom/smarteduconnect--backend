package com.smartcampus.repository;

import com.smartcampus.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRollNumber(String rollNumber);

    Optional<Student> findByEmail(String email);

    Optional<Student> findByUserId(Long userId);

    boolean existsByRollNumber(String rollNumber);

    boolean existsByEmail(String email);

    @Query("SELECT s FROM Student s WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(s.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:deptId IS NULL OR s.department.id = :deptId) AND " +
           "(:academicYear IS NULL OR s.academicYear = :academicYear) AND " +
           "(:semester IS NULL OR s.semester = :semester)")
    Page<Student> searchStudents(
            @Param("search") String search,
            @Param("deptId") Long deptId,
            @Param("academicYear") Integer academicYear,
            @Param("semester") Integer semester,
            Pageable pageable
    );

    List<Student> findByDepartmentId(Long departmentId);

    List<Student> findBySemester(Integer semester);

    long countByDepartmentId(Long departmentId);
}
