package com.demo.repository;

import com.demo.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // --- NEW IMPORT ---

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    
    // --- THIS IS THE NEW "MAGIC" METHOD ---
    // This lets our new service find a staff member by their username
    Optional<Staff> findByUsername(String username);

}

