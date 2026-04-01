package com.example.devicemanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
     @GetMapping("/")
    public String home() {
        System.out.println("HomeController: Received request for home page");
        return "Hello Welcome to Device Management System";
    }
}
