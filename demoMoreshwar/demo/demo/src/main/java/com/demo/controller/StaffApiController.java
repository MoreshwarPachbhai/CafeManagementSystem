package com.demo.controller;

import com.demo.dto.StaffRequest;
import com.demo.model.Staff;
import com.demo.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Imports GetMapping, DeleteMapping, etc.

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffApiController {

    @Autowired
    private StaffService staffService;

    @PostMapping("/add")
    public ResponseEntity<String> addStaff(@RequestBody StaffRequest staffRequest) {
        try {
            Staff newStaff = staffService.createStaff(staffRequest);
            return new ResponseEntity<>("Staff member added successfully with ID: " + newStaff.getStaffId(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // =================================================================
    // --- NEW ENDPOINTS FOR DELETE FEATURE ---
    // =================================================================

    /**
     * Fetches the list of all staff members.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Staff>> getAllStaff() {
        return new ResponseEntity<>(staffService.getAllStaff(), HttpStatus.OK);
    }

    /**
     * Deletes a staff member by ID.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStaff(@PathVariable Integer id) {
        try {
            staffService.deleteStaff(id);
            return new ResponseEntity<>("Staff deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // --- NEW ENDPOINT FOR EDIT ---
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateStaff(@PathVariable Integer id, @RequestBody StaffRequest staffRequest) {
        try {
            staffService.updateStaff(id, staffRequest);
            return new ResponseEntity<>("Staff updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}