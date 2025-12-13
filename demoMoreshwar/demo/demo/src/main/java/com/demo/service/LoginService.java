package com.demo.service;

import com.demo.Build_Connection_db;
import java.sql.*;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM staff WHERE username = ? AND password = ?";

        try (Connection con = Build_Connection_db.buildConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // ✅ returns true if found
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
