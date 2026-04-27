package com.hopeuniversity.hope_university_management.controller;

import com.hopeuniversity.hope_university_management.dto.request.LoginRequest;
import com.hopeuniversity.hope_university_management.dto.request.RegisterRequest;
import com.hopeuniversity.hope_university_management.dto.response.AuthResponse;
import com.hopeuniversity.hope_university_management.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String refreshToken) {
        // extract token from Bearer
        String token = refreshToken.substring(7);
        return ResponseEntity.ok(authService.refreshToken(token));
    }
}