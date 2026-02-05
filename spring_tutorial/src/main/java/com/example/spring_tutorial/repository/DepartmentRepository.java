package com.example.spring_tutorial.repository;

import com.example.spring_tutorial.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Department entity operations.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    Optional<Department> findByName(String name);
    
    Optional<Department> findByCode(String code);
    
    boolean existsByName(String name);
    
    boolean existsByCode(String code);
}

