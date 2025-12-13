package com.demo.controller;

import com.demo.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/reset-password-page")
    public String showResetPage() {
        return "reset_password"; 
    }

    @PostMapping("/reset-password")
    public String handleReset(
            @RequestParam String username,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "reset_password";
        }

        // --- FIXED LOGIC: Call the service to update the password ---
        boolean success = passwordResetService.updatePassword(username, newPassword);

        if (success) {
            model.addAttribute("message", "Password reset successful! Please log in.");
            // Redirect to the login page on success
            return "login"; 
        } else {
            model.addAttribute("error", "Password reset failed. User may not exist or database error occurred.");
            return "reset_password"; 
        }
    }
}