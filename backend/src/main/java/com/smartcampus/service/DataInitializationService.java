package com.smartcampus.service;

import com.smartcampus.entity.*;
import com.smartcampus.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializationService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.admin.email:campus.admin@smartcampus.edu}")
    private String adminEmail;

    @Value("${app.init.admin.password:Campus#2026!}")
    private String adminPassword;

    @Value("${app.init.admin.name:Campus Administrator}")
    private String adminName;

    public DataInitializationService(UserRepository userRepository,
                                   DepartmentRepository departmentRepository,
                                   StudentRepository studentRepository,
                                   SubjectRepository subjectRepository,
                                   StudentSubjectRepository studentSubjectRepository,
                                   AttendanceRepository attendanceRepository,
                                   MarkRepository markRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.studentSubjectRepository = studentSubjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Seed Default Departments
        Department cse = departmentRepository.findByCode("CSE").orElseGet(() ->
                departmentRepository.save(new Department("CSE", "Computer Science & Engineering", "Software Systems, AI, and Networking")));
        Department ece = departmentRepository.findByCode("ECE").orElseGet(() ->
                departmentRepository.save(new Department("ECE", "Electronics & Communication", "Circuits, Signal Processing, and Embedded Systems")));
        Department me = departmentRepository.findByCode("ME").orElseGet(() ->
                departmentRepository.save(new Department("ME", "Mechanical Engineering", "Mechanics, Thermodynamics, and Manufacturing")));
        Department ce = departmentRepository.findByCode("CE").orElseGet(() ->
                departmentRepository.save(new Department("CE", "Civil Engineering", "Structures, Infrastructure, and Environmental Engineering")));
        departmentRepository.findByCode("IT").orElseGet(() ->
                departmentRepository.save(new Department("IT", "Information Technology", "Enterprise IT, Cloud Computing, and Security")));

        // 2. Seed/repair Default Admin User.
        // Always encode the configured password on startup and update an existing admin too.
        // This repairs stale users created by older versions or imported SQL seed data.
        User admin = userRepository.findByEmail(adminEmail).orElse(null);
        if (admin == null) {
            admin = new User(adminEmail, passwordEncoder.encode(adminPassword), adminName, Role.ROLE_ADMIN);
        } else {
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFullName(adminName);
            admin.setRole(Role.ROLE_ADMIN);
        }
        userRepository.save(admin);

        // 3. Seed Demo Students & Data if no students exist yet
        if (studentRepository.count() == 0) {
            // Student 1: Rahul Sharma
            User user1 = new User("rahul.sharma@smartcampus.edu", passwordEncoder.encode("CampusStudent#2026"), "Rahul Sharma", Role.ROLE_STUDENT);
            user1 = userRepository.save(user1);

            Student student1 = new Student(user1, "21CS001", "Rahul Sharma", "rahul.sharma@smartcampus.edu", "9876543210",
                    cse, 3, 5, LocalDate.of(2022, 8, 1));
            student1 = studentRepository.save(student1);

            // Student 2: Priya Patel
            User user2 = new User("priya.patel@smartcampus.edu", passwordEncoder.encode("CampusStudent#2026"), "Priya Patel", Role.ROLE_STUDENT);
            user2 = userRepository.save(user2);

            Student student2 = new Student(user2, "21CS002", "Priya Patel", "priya.patel@smartcampus.edu", "9876543211",
                    cse, 3, 5, LocalDate.of(2022, 8, 1));
            student2 = studentRepository.save(student2);

            // Student 3: Amit Kumar
            User user3 = new User("amit.kumar@smartcampus.edu", passwordEncoder.encode("CampusStudent#2026"), "Amit Kumar", Role.ROLE_STUDENT);
            user3 = userRepository.save(user3);

            Student student3 = new Student(user3, "21EC001", "Amit Kumar", "amit.kumar@smartcampus.edu", "9876543212",
                    ece, 3, 5, LocalDate.of(2022, 8, 1));
            student3 = studentRepository.save(student3);

            // Subjects
            Subject sub1 = subjectRepository.save(new Subject("CS501", "Database Management Systems", cse, 5, 4, 100.0));
            Subject sub2 = subjectRepository.save(new Subject("CS502", "Computer Networks", cse, 5, 4, 100.0));
            Subject sub3 = subjectRepository.save(new Subject("CS503", "Theory of Computation", cse, 5, 3, 100.0));
            Subject sub4 = subjectRepository.save(new Subject("CS504", "Full Stack Web Development", cse, 5, 3, 100.0));
            Subject sub5 = subjectRepository.save(new Subject("EC501", "Digital Signal Processing", ece, 5, 4, 100.0));

            // Enrollments
            studentSubjectRepository.save(new StudentSubject(student1, sub1, LocalDate.of(2024, 7, 15), "ACTIVE"));
            studentSubjectRepository.save(new StudentSubject(student1, sub2, LocalDate.of(2024, 7, 15), "ACTIVE"));
            studentSubjectRepository.save(new StudentSubject(student1, sub3, LocalDate.of(2024, 7, 15), "ACTIVE"));
            studentSubjectRepository.save(new StudentSubject(student1, sub4, LocalDate.of(2024, 7, 15), "ACTIVE"));

            studentSubjectRepository.save(new StudentSubject(student2, sub1, LocalDate.of(2024, 7, 15), "ACTIVE"));
            studentSubjectRepository.save(new StudentSubject(student2, sub2, LocalDate.of(2024, 7, 15), "ACTIVE"));

            studentSubjectRepository.save(new StudentSubject(student3, sub5, LocalDate.of(2024, 7, 15), "ACTIVE"));

            // Sample Attendance
            LocalDate today = LocalDate.now();
            attendanceRepository.save(new Attendance(student1, sub1, today.minusDays(4), true, "Attended"));
            attendanceRepository.save(new Attendance(student1, sub1, today.minusDays(3), true, "Attended"));
            attendanceRepository.save(new Attendance(student1, sub1, today.minusDays(2), false, "Absent"));
            attendanceRepository.save(new Attendance(student1, sub1, today.minusDays(1), true, "Attended"));
            attendanceRepository.save(new Attendance(student1, sub1, today, true, "Attended"));

            attendanceRepository.save(new Attendance(student1, sub2, today.minusDays(4), true, "Attended"));
            attendanceRepository.save(new Attendance(student1, sub2, today.minusDays(3), true, "Attended"));
            attendanceRepository.save(new Attendance(student1, sub2, today.minusDays(2), true, "Attended"));
            attendanceRepository.save(new Attendance(student1, sub2, today.minusDays(1), true, "Attended"));
            attendanceRepository.save(new Attendance(student1, sub2, today, true, "Attended"));

            attendanceRepository.save(new Attendance(student2, sub1, today.minusDays(4), true, "Attended"));
            attendanceRepository.save(new Attendance(student2, sub1, today.minusDays(3), false, "Absent"));
            attendanceRepository.save(new Attendance(student2, sub1, today.minusDays(2), false, "Absent"));
            attendanceRepository.save(new Attendance(student2, sub1, today.minusDays(1), true, "Attended"));
            attendanceRepository.save(new Attendance(student2, sub1, today, true, "Attended"));

            // Sample Marks
            markRepository.save(new Mark(student1, sub1, "FINAL", 5, 88.5, 100.0, today.minusDays(10)));
            markRepository.save(new Mark(student1, sub2, "FINAL", 5, 92.0, 100.0, today.minusDays(9)));
            markRepository.save(new Mark(student1, sub3, "FINAL", 5, 78.0, 100.0, today.minusDays(8)));
            markRepository.save(new Mark(student1, sub4, "FINAL", 5, 95.0, 100.0, today.minusDays(7)));

            markRepository.save(new Mark(student2, sub1, "FINAL", 5, 82.0, 100.0, today.minusDays(10)));
            markRepository.save(new Mark(student2, sub2, "FINAL", 5, 85.0, 100.0, today.minusDays(9)));

            markRepository.save(new Mark(student3, sub5, "FINAL", 5, 74.0, 100.0, today.minusDays(10)));
        }
    }
}
