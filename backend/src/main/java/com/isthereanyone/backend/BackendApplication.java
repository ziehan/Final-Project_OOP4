package com.isthereanyone.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("==========================================");
        System.out.println("  Is There Anyone - Game Backend");
        System.out.println("  Server running on http://localhost:9090");
        System.out.println("==========================================");
    }
}