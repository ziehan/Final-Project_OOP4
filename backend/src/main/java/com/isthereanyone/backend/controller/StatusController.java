package com.isthereanyone.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    @GetMapping("/api/status")
    public String checkStatus(){
        return "Server is Running!";
    }
}
