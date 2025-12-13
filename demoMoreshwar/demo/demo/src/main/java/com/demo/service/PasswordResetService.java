package com.demo.service;

import com.demo.Build_Connection_db;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Service
public class PasswordResetService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean updatePassword(String username, String newPassword) {
        // 1. Encode the new password using the same encoder defined in SecurityConfig
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 2. SQL to update the password column
        String sql = "UPDATE staff SET password = ? WHERE username = ?";

        try (Connection con = Build_Connection_db.buildConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Set the ENCODED password
            ps.setString(1, encodedPassword);
            ps.setString(2, username);

            int rows = ps.executeUpdate();
            return rows > 0; 

        } catch (Exception e) {
            System.err.println("Error updating password for user: " + username);
            e.printStackTrace();
            return false;
        }
    }
}