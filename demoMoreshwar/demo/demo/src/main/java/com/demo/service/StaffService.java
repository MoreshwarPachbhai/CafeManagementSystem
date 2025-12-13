package com.demo.service;

import com.demo.dto.StaffRequest;
import com.demo.model.Staff;
import com.demo.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; // --- NEW IMPORT ---

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Transactional
    public Staff createStaff(StaffRequest request) {
        if (staffRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username '" + request.getUsername() + "' is already taken.");
        }

        Staff newStaff = new Staff();
        newStaff.setName(request.getName());
        newStaff.setUsername(request.getUsername());
        newStaff.setPassword(request.getPassword());

        return staffRepository.save(newStaff);
    }

    // --- NEW METHODS FOR DELETE FEATURE ---

    /**
     * Returns a list of all staff members.
     */
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    /**
     * Deletes a staff member by ID.
     */
    @Transactional
    public void deleteStaff(Integer staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new RuntimeException("Staff member not found with ID: " + staffId);
        }
        staffRepository.deleteById(staffId);
    }

    // --- NEW METHOD FOR EDIT FEATURE ---
    @Transactional
    public Staff updateStaff(Integer staffId, StaffRequest request) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff member not found with ID: " + staffId));

        // Update basic fields
        staff.setName(request.getName());
        staff.setUsername(request.getUsername());

        // Update password ONLY if a new one is provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            staff.setPassword(request.getPassword());
        }

        return staffRepository.save(staff);
    }
}