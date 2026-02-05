package com.example.spring_tutorial.service;

import com.example.spring_tutorial.dto.UserRegistrationDTO;
import com.example.spring_tutorial.exception.ResourceNotFoundException;
import com.example.spring_tutorial.model.User;
import com.example.spring_tutorial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service for user authentication and registration.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user as a teacher.
     */
    @Transactional
    public User registerTeacher(UserRegistrationDTO registrationDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("User with email already exists: " + registrationDTO.getEmail());
        }
        
        // Check if passwords match
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(User.Role.TEACHER);
        user.setDateOfBirth(registrationDTO.getDateOfBirth());
        
        return userRepository.save(user);
    }
    
    /**
     * Register a new student (only by teachers).
     */
    @Transactional
    public User registerStudent(UserRegistrationDTO registrationDTO, String teacherEmail) {
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("User with email already exists: " + registrationDTO.getEmail());
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(User.Role.STUDENT);
        user.setDateOfBirth(registrationDTO.getDateOfBirth());
        
        return userRepository.save(user);
    }
    
    /**
     * Authenticate user and return user entity.
     */
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        return user;
    }
    
    /**
     * Get user by email.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
    
    /**
     * Update user profile.
     */
    @Transactional
    public User updateProfile(String email, User userDetails) {
        User user = getUserByEmail(email);
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        
        return userRepository.save(user);
    }
    
    /**
     * Change user password.
     */
    @Transactional
    public void changePassword(String email, String newPassword) {
        User user = getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Verify password against encoded password.
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

