package com.smartcampus.security;

import com.smartcampus.entity.Role;
import com.smartcampus.entity.Student;
import com.smartcampus.entity.User;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public CustomUserDetailsService(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Long studentId = null;
        String rollNumber = null;

        if (user.getRole() == Role.ROLE_STUDENT) {
            Optional<Student> studentOpt = studentRepository.findByUserId(user.getId());
            if (studentOpt.isEmpty()) {
                studentOpt = studentRepository.findByEmail(user.getEmail());
            }
            if (studentOpt.isPresent()) {
                Student s = studentOpt.get();
                studentId = s.getId();
                rollNumber = s.getRollNumber();
            }
        }

        return UserPrincipal.create(user, studentId, rollNumber);
    }
}
