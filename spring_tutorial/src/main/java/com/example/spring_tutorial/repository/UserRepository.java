package com.example.spring_tutorial.repository;

import com.example.spring_tutorial.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    /**
     * Find all users with a specific role.
     */
    List<User> findByRole(User.Role role);
    
    /**
     * Count users with a specific role.
     */
    long countByRole(User.Role role);
}
