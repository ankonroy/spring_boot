package com.example.spring_tutorial.controller;

import com.example.spring_tutorial.dto.UserRegistrationDTO;
import com.example.spring_tutorial.model.User;
import com.example.spring_tutorial.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for profile management.
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    
    private final AuthService authService;
    
    /**
     * Show user profile.
     */
    @GetMapping
    public String showProfile(Model model, HttpServletRequest request) {
        String email = getCurrentUserEmail();
        if (email == null) {
            return "redirect:/login";
        }
        
        User user = authService.getUserByEmail(email);
        model.addAttribute("user", user);
        
        // Add department and student info if available
        if (user.getRole() == User.Role.STUDENT) {
            try {
                com.example.spring_tutorial.model.Student student = 
                    request.getServletContext().getAttribute("studentRepository") != null ?
                    ((com.example.spring_tutorial.repository.StudentRepository) 
                        request.getServletContext().getAttribute("studentRepository")).findByEmail(email).orElse(null) : null;
                if (student != null) {
                    model.addAttribute("student", student);
                }
            } catch (Exception e) {
                // Student might not exist yet
            }
        }
        
        return "profile";
    }
    
    /**
     * Show edit profile form.
     */
    @GetMapping("/edit")
    public String showEditProfile(Model model) {
        String email = getCurrentUserEmail();
        if (email == null) {
            return "redirect:/login";
        }
        
        User user = authService.getUserByEmail(email);
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setDateOfBirth(user.getDateOfBirth());
        
        model.addAttribute("user", user);
        model.addAttribute("registrationDTO", dto);
        
        return "edit-profile";
    }
    
    /**
     * Process profile update.
     */
    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute UserRegistrationDTO registrationDTO,
                               BindingResult bindingResult, Model model) {
        String currentEmail = getCurrentUserEmail();
        if (currentEmail == null) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationDTO", registrationDTO);
            return "edit-profile";
        }
        
        try {
            User user = authService.getUserByEmail(currentEmail);
            
            // Update user fields
            user.setFirstName(registrationDTO.getFirstName());
            user.setLastName(registrationDTO.getLastName());
            user.setDateOfBirth(registrationDTO.getDateOfBirth());
            
            authService.updateProfile(currentEmail, user);
            
            log.info("Profile updated for user: {}", currentEmail);
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registrationDTO", registrationDTO);
            return "edit-profile";
        }
    }
    
    /**
     * Show change password form.
     */
    @GetMapping("/change-password")
    public String showChangePassword(Model model) {
        String email = getCurrentUserEmail();
        if (email == null) {
            return "redirect:/login";
        }
        model.addAttribute("email", email);
        return "change-password";
    }
    
    /**
     * Process password change.
     */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Model model) {
        String email = getCurrentUserEmail();
        if (email == null) {
            return "redirect:/login";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("email", email);
            return "change-password";
        }
        
        if (newPassword.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters");
            model.addAttribute("email", email);
            return "change-password";
        }
        
        try {
            authService.changePassword(email, newPassword);
            log.info("Password changed for user: {}", email);
            return "redirect:/profile?message=Password changed successfully";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "change-password";
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

