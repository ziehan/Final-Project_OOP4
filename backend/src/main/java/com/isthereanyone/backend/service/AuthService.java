package com.isthereanyone.backend.service;

import com.isthereanyone.backend.dto.*;
import com.isthereanyone.backend.entity.User;
import com.isthereanyone.backend.exception.InvalidOperationException;
import com.isthereanyone.backend.exception.ResourceNotFoundException;
import com.isthereanyone.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new InvalidOperationException("Username sudah digunakan");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidOperationException("Email sudah terdaftar");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName() != null ?
                request.getDisplayName() : request.getUsername());

        User savedUser = userRepository.save(user);
        UserResponse userResponse = toUserResponse(savedUser);
        return new AuthResponse("Registrasi berhasil", userResponse);
    }

    public AuthResponse signin(SigninRequest request) {
        User user = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(),
                request.getUsernameOrEmail()
        ).orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidOperationException("Password salah");
        }

        UserResponse userResponse = toUserResponse(user);
        return new AuthResponse("Login berhasil", userResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt()
        );
    }
}

