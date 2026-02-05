package com.example.spring_tutorial.service;

import com.example.spring_tutorial.dto.StudentDTO;
import com.example.spring_tutorial.dto.TeacherStudentDTO;
import com.example.spring_tutorial.exception.ResourceNotFoundException;
import com.example.spring_tutorial.model.Department;
import com.example.spring_tutorial.model.Student;
import com.example.spring_tutorial.model.Teacher;
import com.example.spring_tutorial.repository.DepartmentRepository;
import com.example.spring_tutorial.repository.StudentRepository;
import com.example.spring_tutorial.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for Teacher CRUD operations.
 * Teachers can also create student profiles.
 */
@Service
@RequiredArgsConstructor
public class TeacherService {
    
    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    
    /**
     * Create a new teacher.
     */
    @Transactional
    public Teacher createTeacher(Teacher teacher) {
        if (teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new IllegalArgumentException("Teacher with email already exists: " + teacher.getEmail());
        }
        if (teacher.getDepartment() != null && teacher.getDepartment().getId() != null) {
            Department department = departmentRepository.findById(teacher.getDepartment().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", teacher.getDepartment().getId()));
            teacher.setDepartment(department);
        }
        return teacherRepository.save(teacher);
    }
    
    /**
     * Get all teachers.
     */
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }
    
    /**
     * Get teacher by ID.
     */
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));
    }
    
    /**
     * Get teacher by email.
     */
    public Teacher getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher", "email", email));
    }
    
    /**
     * Update teacher by ID.
     */
    @Transactional
    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = getTeacherById(id);
        
        teacher.setFirstName(teacherDetails.getFirstName());
        teacher.setLastName(teacherDetails.getLastName());
        teacher.setDateOfBirth(teacherDetails.getDateOfBirth());
        teacher.setHireDate(teacherDetails.getHireDate());
        teacher.setSpecialization(teacherDetails.getSpecialization());
        
        if (teacherDetails.getDepartment() != null && teacherDetails.getDepartment().getId() != null) {
            Department department = departmentRepository.findById(teacherDetails.getDepartment().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", teacherDetails.getDepartment().getId()));
            teacher.setDepartment(department);
        }
        
        return teacherRepository.save(teacher);
    }
    
    /**
     * Delete teacher by ID.
     */
    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = getTeacherById(id);
        teacherRepository.delete(teacher);
    }
    
    /**
     * Teacher creates a student profile.
     * This is the ONLY way students can be created in the system.
     */
    @Transactional
    public StudentDTO createStudentByTeacher(TeacherStudentDTO dto, String teacherEmail) {
        // Verify teacher exists
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with email: " + teacherEmail));
        
        // Check if student already exists
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Student with email already exists: " + dto.getEmail());
        }
        
        if (studentRepository.existsByStudentId(dto.getStudentId())) {
            throw new IllegalArgumentException("Student ID already exists: " + dto.getStudentId());
        }
        
        // Get department
        Department department = departmentRepository.findById(dto.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", dto.getDepartmentId()));
        
        // Create student
        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setStudentId(dto.getStudentId());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setEnrollmentDate(LocalDate.now());
        student.setDepartment(department);
        student.setCreatedBy(teacherEmail);
        
        Student savedStudent = studentRepository.save(student);
        return convertToDTO(savedStudent);
    }
    
    /**
     * Convert Student entity to StudentDTO.
     */
    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setStudentId(student.getStudentId());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setEnrollmentDate(student.getEnrollmentDate());
        if (student.getDepartment() != null) {
            dto.setDepartmentId(student.getDepartment().getId());
            dto.setDepartmentName(student.getDepartment().getName());
        }
        dto.setCreatedBy(student.getCreatedBy());
        return dto;
    }
}

