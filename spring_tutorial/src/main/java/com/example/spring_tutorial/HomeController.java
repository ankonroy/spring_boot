package com.example.spring_tutorial; // CHANGE THIS to your actual package name!

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "🎉 Hello world, Hello Spring Boot";
    }

    @GetMapping("/test")
    public String test() {
        return "✅ This is a second test endpoint. Everything works!";
    }
}