package com.demo.controller;

import com.demo.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    @SuppressWarnings("unused")
    @Autowired
    private LoginService loginService;

    // ✅ Just to confirm API is running
    @GetMapping("/test-login")
    public String testLogin() {
        return "✅ Login API is working!";
    }
}
