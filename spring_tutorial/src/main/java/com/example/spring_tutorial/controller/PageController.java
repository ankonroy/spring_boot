package com.example.spring_tutorial.controller;

import com.example.spring_tutorial.dto.UserRegistrationDTO;
import com.example.spring_tutorial.model.User;
import com.example.spring_tutorial.repository.UserRepository;
import com.example.spring_tutorial.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to serve static HTML pages with authentication awareness.
 * Uses the users table for both teachers and students (via role field).
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class PageController {
    
    private final AuthService authService;
    private final UserRepository userRepository;
    
    /**
     * Home page.
     */
    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }
    
    /**
     * Departments page - requires authentication.
     */
    @GetMapping("/departments.html")
    public String departments(Model model) {
        return "departments";
    }
    
    /**
     * Teachers page - requires authentication.
     * Fetches all teachers from the users table where role = TEACHER.
     */
    @GetMapping("/teachers.html")
    public String teachers(Model model) {
        List<User> teachers = userRepository.findByRole(User.Role.TEACHER);
        model.addAttribute("teachers", teachers);
        return "teachers";
    }
    
    /**
     * Students page - requires authentication.
     * Fetches all students from the users table where role = STUDENT.
     */
    @GetMapping("/students.html")
    public String students(Model model) {
        List<User> students = userRepository.findByRole(User.Role.STUDENT);
        model.addAttribute("students", students);
        return "students";
    }
    
    /**
     * Courses page - requires authentication.
     */
    @GetMapping("/courses.html")
    public String courses(Model model) {
        return "courses";
    }
    
    /**
     * Show add student page - requires TEACHER role.
     */
    @GetMapping("/students/add")
    @PreAuthorize("hasRole('TEACHER')")
    public String showAddStudent(Model model) {
        model.addAttribute("registrationDTO", new UserRegistrationDTO());
        return "student-form";
    }
    
    /**
     * Process student creation - requires TEACHER role.
     */
    @PostMapping("/students/add")
    @PreAuthorize("hasRole('TEACHER')")
    public String processAddStudent(@Valid @ModelAttribute UserRegistrationDTO registrationDTO,
                                    BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationDTO", registrationDTO);
            return "student-form";
        }
        
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("registrationDTO", registrationDTO);
            return "student-form";
        }
        
        if (registrationDTO.getPassword().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters");
            model.addAttribute("registrationDTO", registrationDTO);
            return "student-form";
        }
        
        try {
            String teacherEmail = getCurrentUserEmail();
            authService.registerStudent(registrationDTO, teacherEmail);
            log.info("Teacher {} created student profile for {}", teacherEmail, registrationDTO.getEmail());
            model.addAttribute("message", "Student profile created successfully! The student can now login.");
            model.addAttribute("registrationDTO", new UserRegistrationDTO());
            return "student-form";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registrationDTO", registrationDTO);
            return "student-form";
        }
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

