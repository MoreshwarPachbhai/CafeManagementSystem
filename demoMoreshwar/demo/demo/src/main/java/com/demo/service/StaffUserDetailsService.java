package com.demo.service;

import com.demo.model.Staff;
import com.demo.repository.StaffRepository; // We created this earlier
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class StaffUserDetailsService implements UserDetailsService {

    @Autowired
    private StaffRepository staffRepository; // We created this repository

    /**
     * This is the method Spring Security will call.
     * It looks for a user in your "staff" table by their username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Find the staff member in your database
        Staff staff = staffRepository.findByUsername(username) // We need to add this method
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 2. Convert your "Staff" object into a Spring Security "User" object
        return new User(
            staff.getUsername(),
            staff.getPassword(),
            new ArrayList<>() // An empty list of roles/authorities
        );
    }
}

