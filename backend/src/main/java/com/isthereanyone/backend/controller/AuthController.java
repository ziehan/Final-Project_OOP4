package com.isthereanyone.backend.controller;

import com.isthereanyone.backend.dto.*;
import com.isthereanyone.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse authResponse = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Registrasi berhasil", authResponse));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<AuthResponse>> signin(@Valid @RequestBody SigninRequest request) {
        AuthResponse authResponse = authService.signin(request);
        return ResponseEntity.ok(ApiResponse.success("Login berhasil", authResponse));
    }

    @GetMapping("/check/username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@PathVariable String username) {
        boolean exists = authService.isUsernameExists(username);
        String message = exists ? "Username sudah digunakan" : "Username tersedia";
        return ResponseEntity.ok(new ApiResponse<>(true, message, exists));
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@PathVariable String email) {
        boolean exists = authService.isEmailExists(email);
        String message = exists ? "Email sudah terdaftar" : "Email tersedia";
        return ResponseEntity.ok(new ApiResponse<>(true, message, exists));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String username) {
        UserResponse userResponse = authService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("User ditemukan", userResponse));
    }
}

