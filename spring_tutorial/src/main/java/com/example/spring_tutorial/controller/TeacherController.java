package com.example.spring_tutorial.controller;

import com.example.spring_tutorial.dto.StudentDTO;
import com.example.spring_tutorial.dto.TeacherStudentDTO;
import com.example.spring_tutorial.model.Teacher;
import com.example.spring_tutorial.service.StudentService;
import com.example.spring_tutorial.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Teacher CRUD operations.
 * Also includes endpoint for teachers to create student profiles.
 */
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {
    
    private final TeacherService teacherService;
    private final StudentService studentService;
    
    /**
     * Create a new teacher.
     */
    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@Valid @RequestBody Teacher teacher) {
        Teacher createdTeacher = teacherService.createTeacher(teacher);
        return new ResponseEntity<>(createdTeacher, HttpStatus.CREATED);
    }
    
    /**
     * Get all teachers.
     */
    @GetMapping
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }
    
    /**
     * Get teacher by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }
    
    /**
     * Update teacher by ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(
            @PathVariable Long id, 
            @Valid @RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
    }
    
    /**
     * Delete teacher by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Teacher creates a student profile.
     * This is the ONLY way students can be created in the system.
     * Student cannot create their own profile.
     */
    @PostMapping("/{teacherEmail}/students")
    public ResponseEntity<StudentDTO> createStudentByTeacher(
            @PathVariable String teacherEmail,
            @Valid @RequestBody TeacherStudentDTO studentDTO) {
        StudentDTO createdStudent = teacherService.createStudentByTeacher(studentDTO, teacherEmail);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }
}

