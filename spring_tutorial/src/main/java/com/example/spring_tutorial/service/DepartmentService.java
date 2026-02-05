package com.example.spring_tutorial.service;

import com.example.spring_tutorial.exception.ResourceNotFoundException;
import com.example.spring_tutorial.model.Department;
import com.example.spring_tutorial.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Department CRUD operations.
 */
@Service
@RequiredArgsConstructor
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    
    /**
     * Create a new department.
     */
    @Transactional
    public Department createDepartment(Department department) {
        if (departmentRepository.existsByCode(department.getCode())) {
            throw new IllegalArgumentException("Department with code already exists: " + department.getCode());
        }
        if (departmentRepository.existsByName(department.getName())) {
            throw new IllegalArgumentException("Department with name already exists: " + department.getName());
        }
        return departmentRepository.save(department);
    }
    
    /**
     * Get all departments.
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    
    /**
     * Get department by ID.
     */
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }
    
    /**
     * Update department by ID.
     */
    @Transactional
    public Department updateDepartment(Long id, Department departmentDetails) {
        Department department = getDepartmentById(id);
        
        department.setName(departmentDetails.getName());
        department.setCode(departmentDetails.getCode());
        department.setEstablishedDate(departmentDetails.getEstablishedDate());
        department.setDescription(departmentDetails.getDescription());
        
        return departmentRepository.save(department);
    }
    
    /**
     * Delete department by ID.
     */
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }
}

