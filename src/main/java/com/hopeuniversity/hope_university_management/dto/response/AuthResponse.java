package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String role;
    private boolean mustChangePassword;

    // Constructor without mustChangePassword (for backward compatibility)
    public AuthResponse(String accessToken, String refreshToken, Long userId, String email, String role) {
        this(accessToken, refreshToken, "Bearer", userId, email, role, false);
    }

    // Full constructor (used in AuthService)
    public AuthResponse(String accessToken, String refreshToken, Long userId, String email, String role, boolean mustChangePassword) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.mustChangePassword = mustChangePassword;
    }
}