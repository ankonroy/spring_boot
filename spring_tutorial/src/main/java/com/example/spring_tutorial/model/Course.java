package com.example.spring_tutorial.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Course entity representing academic courses.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 20)
    private String code;
    
    @Column
    private Integer credits;
    
    @Column(length = 1000)
    private String description;
    
    // Many-to-One: Course to Department
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    // Many-to-Many: Course to Students
    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
    
    // Many-to-Many: Course to Teachers
    @ManyToMany(mappedBy = "courses")
    private Set<Teacher> teachers = new HashSet<>();
}

