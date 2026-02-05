package com.example.spring_tutorial.controller;

import com.example.spring_tutorial.dto.LoginDTO;
import com.example.spring_tutorial.dto.UserRegistrationDTO;
import com.example.spring_tutorial.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication operations.
 */
@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    
    /**
     * Show login page.
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }
    
    /**
     * Show registration page.
     */
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("registrationDTO", new UserRegistrationDTO());
        return "register";
    }
    
    /**
     * Process registration (as Teacher).
     */
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute UserRegistrationDTO registrationDTO,
                                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationDTO", registrationDTO);
            return "register";
        }
        
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("registrationDTO", registrationDTO);
            return "register";
        }
        
        try {
            authService.registerTeacher(registrationDTO);
            log.info("New teacher registered: {}", registrationDTO.getEmail());
            model.addAttribute("message", "Registration successful! Please login.");
            model.addAttribute("loginDTO", new LoginDTO());
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registrationDTO", registrationDTO);
            return "register";
        }
    }
    
    /**
     * Process logout.
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout=true";
    }
    
    /**
     * API endpoint for authentication (for REST clients).
     */
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> apiLogin(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
            
            // Verify password
            if (!authService.verifyPassword(loginDTO.getPassword(), userDetails.getPassword())) {
                return ResponseEntity.status(401).body(java.util.Map.of("error", "Invalid credentials"));
            }
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            com.example.spring_tutorial.model.User user = authService.getUserByEmail(loginDTO.getEmail());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "message", "Login successful",
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "fullName", user.getFullName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(java.util.Map.of("error", "Invalid credentials"));
        }
    }
    
    /**
     * API endpoint for registration (as Teacher).
     */
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> apiRegister(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Passwords do not match"));
            }
            
            com.example.spring_tutorial.model.User user = authService.registerTeacher(registrationDTO);
            
            return ResponseEntity.status(201).body(java.util.Map.of(
                "message", "Registration successful",
                "email", user.getEmail(),
                "role", user.getRole().name()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}

