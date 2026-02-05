package com.example.spring_tutorial.controller;

import com.example.spring_tutorial.dto.StudentDTO;
import com.example.spring_tutorial.dto.UserRegistrationDTO;
import com.example.spring_tutorial.model.User;
import com.example.spring_tutorial.service.AuthService;
import com.example.spring_tutorial.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Student operations.
 * Handles both student profile management and teacher operations on students.
 * Note: Students cannot create their own profiles - only update them.
 * Student profiles can ONLY be created by teachers.
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {
    
    private final StudentService studentService;
    private final AuthService authService;
    
    /**
     * Get all students (teachers only).
     */
    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        log.info("Fetching all students");
        return ResponseEntity.ok(studentService.getAllStudents());
    }
    
    /**
     * Get student by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }
    
    /**
     * Get current student profile.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentStudent() {
        try {
            String email = getCurrentUserEmail();
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
            }
            
            User user = authService.getUserByEmail(email);
            if (user.getRole() != User.Role.STUDENT) {
                return ResponseEntity.badRequest().body(Map.of("error", "User is not a student"));
            }
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Teacher creates a student profile via API.
     * POST /api/students?teacherEmail=teacher@school.edu
     */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createStudent(@Valid @RequestBody UserRegistrationDTO studentDTO,
                                          @RequestParam String teacherEmail) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            // Verify the teacher is the one making the request
            if (!auth.getName().equals(teacherEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only create students with your own account"));
            }
            
            log.info("Teacher {} creating student profile for {}", teacherEmail, studentDTO.getEmail());
            
            User student = authService.registerStudent(studentDTO, teacherEmail);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Student profile created successfully",
                "student", Map.of(
                    "id", student.getId(),
                    "email", student.getEmail(),
                    "firstName", student.getFirstName(),
                    "lastName", student.getLastName(),
                    "role", student.getRole().name()
                )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update student profile by ID.
     * Only the student themselves or the teacher who created their profile can update it.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentDTO studentDTO,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail) {
        
        // If no header provided, use authenticated user's email
        if (userEmail == null) {
            userEmail = getCurrentUserEmail();
        }
        
        return ResponseEntity.ok(studentService.updateStudent(id, studentDTO, userEmail));
    }
    
    /**
     * Update current student profile.
     * Students can only update their own profile.
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentStudent(@Valid @RequestBody UserRegistrationDTO studentDTO) {
        try {
            String email = getCurrentUserEmail();
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
            }
            
            User currentUser = authService.getUserByEmail(email);
            if (currentUser.getRole() != User.Role.STUDENT) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only students can update their profile this way"));
            }
            
            // Students can only update first name, last name, and date of birth
            User updatedUser = new User();
            updatedUser.setFirstName(studentDTO.getFirstName());
            updatedUser.setLastName(studentDTO.getLastName());
            updatedUser.setDateOfBirth(studentDTO.getDateOfBirth());
            
            User result = authService.updateProfile(email, updatedUser);
            
            log.info("Student {} updated their profile", email);
            
            return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "student", Map.of(
                    "id", result.getId(),
                    "email", result.getEmail(),
                    "firstName", result.getFirstName(),
                    "lastName", result.getLastName(),
                    "dateOfBirth", result.getDateOfBirth() != null ? result.getDateOfBirth().toString() : null
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete student profile.
     * Only the teacher who created the profile can delete it.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail) {
        
        // If no header provided, use authenticated user's email
        if (userEmail == null) {
            userEmail = getCurrentUserEmail();
        }
        
        studentService.deleteStudent(id, userEmail);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get current user email from security context.
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return null;
    }
}