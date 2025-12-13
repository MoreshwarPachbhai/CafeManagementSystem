package com.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // --- THIS METHOD WAS DELETED ---
    // @GetMapping("/")
    // public String homeRedirect() { ... }
    // --- Spring Security will now handle the root "/" URL ---


    // --- THIS IS THE NEW, CORRECT METHOD FOR YOUR LOGIN PAGE ---
    @GetMapping("/login")
    public String showLoginPage() {
        // This tells Spring to find and return "login.html"
        // from your /templates folder
        return "login";
    }

    // 🍔 Show dashboard page (from templates/menu_dashboard.html)
    @GetMapping("/menu_dashboard")
    public String showDashboardPage() {
        return "menu_dashboard";
    }

    // --- THIS METHOD WAS DELETED ---
    // @GetMapping("/error")
    // public String handleErrorPage() { ... }
    // --- This was causing the redirect loop ---

    // --- Your other page methods are correct ---
    @GetMapping("/orders_dashboard")
    public String showOrdersPage() {
        return "orders_dashboard"; 
    }

    @GetMapping("/history_dashboard")
    public String showHistoryPage() {
        return "history_dashboard";
    }

    @GetMapping("/settings_dashboard")
    public String showSettingsPage() {
        // This tells Spring to find and return "settings_dashboard.html"
        // from your /templates folder
        return "settings_dashboard";
    }

    @GetMapping("/reset_password")
    public String showResetPasswordPage() {
        // This looks for "resetpassword.html" in src/main/resources/templates/
        return "reset_password"; 
    }
}

