package com.example.spring_tutorial.service;

import com.example.spring_tutorial.exception.ResourceNotFoundException;
import com.example.spring_tutorial.model.Course;
import com.example.spring_tutorial.model.Department;
import com.example.spring_tutorial.repository.CourseRepository;
import com.example.spring_tutorial.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Course CRUD operations.
 */
@Service
@RequiredArgsConstructor
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    
    /**
     * Create a new course.
     */
    @Transactional
    public Course createCourse(Course course) {
        if (courseRepository.existsByCode(course.getCode())) {
            throw new IllegalArgumentException("Course with code already exists: " + course.getCode());
        }
        
        if (course.getDepartment() != null && course.getDepartment().getId() != null) {
            Department department = departmentRepository.findById(course.getDepartment().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", course.getDepartment().getId()));
            course.setDepartment(department);
        }
        
        return courseRepository.save(course);
    }
    
    /**
     * Get all courses.
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    /**
     * Get course by ID.
     */
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }
    
    /**
     * Update course by ID.
     */
    @Transactional
    public Course updateCourse(Long id, Course courseDetails) {
        Course course = getCourseById(id);
        
        course.setName(courseDetails.getName());
        course.setCode(courseDetails.getCode());
        course.setCredits(courseDetails.getCredits());
        course.setDescription(courseDetails.getDescription());
        
        if (courseDetails.getDepartment() != null && courseDetails.getDepartment().getId() != null) {
            Department department = departmentRepository.findById(courseDetails.getDepartment().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", courseDetails.getDepartment().getId()));
            course.setDepartment(department);
        }
        
        return courseRepository.save(course);
    }
    
    /**
     * Delete course by ID.
     */
    @Transactional
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }
}

