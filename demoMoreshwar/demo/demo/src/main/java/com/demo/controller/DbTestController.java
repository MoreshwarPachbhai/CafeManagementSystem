package com.demo.controller;

import com.demo.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbTestController {

    @Autowired
    private StaffRepository staffRepository;

    @GetMapping("/db-test")
    public String testDb() {
        return "Connected! Staff count = " + staffRepository.count();
    }
}
