package com.example.spring_tutorial.service;

import com.example.spring_tutorial.dto.StudentDTO;
import com.example.spring_tutorial.exception.ResourceNotFoundException;
import com.example.spring_tutorial.exception.UnauthorizedAccessException;
import com.example.spring_tutorial.model.Department;
import com.example.spring_tutorial.model.Student;
import com.example.spring_tutorial.repository.DepartmentRepository;
import com.example.spring_tutorial.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Student operations.
 * Note: Students cannot create their own profiles. They can only update them.
 * Student profiles can ONLY be created by teachers.
 */
@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    
    /**
     * Get all students.
     */
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get student by ID.
     */
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return convertToDTO(student);
    }
    
    /**
     * Update student profile.
     * Only the student themselves or the teacher who created their profile can update it.
     * Note: Students CANNOT create profiles - only update existing ones.
     */
    @Transactional
    public StudentDTO updateStudent(Long id, StudentDTO dto, String updaterEmail) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        
        // Authorization check: Only the student or the creating teacher can update
        if (!student.getEmail().equals(updaterEmail)) {
            // If not the student, check if updater is the teacher who created this profile
            if (!updaterEmail.equals(student.getCreatedBy())) {
                throw new UnauthorizedAccessException(
                    "Only the student or the teacher who created this profile can update it");
            }
        }
        
        // Update allowed fields (limited update permissions)
        if (dto.getFirstName() != null) {
            student.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            student.setLastName(dto.getLastName());
        }
        if (dto.getDateOfBirth() != null) {
            student.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getDepartmentId() != null && 
            !dto.getDepartmentId().equals(student.getDepartment().getId())) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", dto.getDepartmentId()));
            student.setDepartment(department);
        }
        
        Student updatedStudent = studentRepository.save(student);
        return convertToDTO(updatedStudent);
    }
    
    /**
     * Delete student profile.
     * Only the teacher who created the profile can delete it.
     */
    @Transactional
    public void deleteStudent(Long id, String requesterEmail) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        
        // Only the creating teacher can delete
        if (!requesterEmail.equals(student.getCreatedBy())) {
            throw new UnauthorizedAccessException(
                "Only the teacher who created this profile can delete it");
        }
        
        studentRepository.delete(student);
    }
    
    /**
     * Get student by email.
     */
    public StudentDTO getStudentByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "email", email));
        return convertToDTO(student);
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

