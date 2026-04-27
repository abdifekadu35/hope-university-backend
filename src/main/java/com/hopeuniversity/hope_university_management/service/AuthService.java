package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Role;
import com.hopeuniversity.hope_university_management.domain.entities.User;
import com.hopeuniversity.hope_university_management.domain.enums.RoleName;
import com.hopeuniversity.hope_university_management.domain.repositories.RoleRepository;
import com.hopeuniversity.hope_university_management.domain.repositories.UserRepository;
import com.hopeuniversity.hope_university_management.dto.request.LoginRequest;
import com.hopeuniversity.hope_university_management.dto.request.RegisterRequest;
import com.hopeuniversity.hope_university_management.dto.response.AuthResponse;
import com.hopeuniversity.hope_university_management.security.JwtTokenProvider;
import com.hopeuniversity.hope_university_management.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        boolean mustChange = user.isMustChangePassword();
        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getRole().getName().name(), mustChange);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // For student self‑registration, use StudentService.createStudent() instead.
        // This method is kept for admin creation of other roles.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        Role role = roleRepository.findByName(RoleName.STUDENT)
                .orElseThrow(() -> new RuntimeException("Default role STUDENT not found"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setActive(true);
        user.setMustChangePassword(false);
        User savedUser = userRepository.save(user);

        UserPrincipal userPrincipal = UserPrincipal.create(savedUser);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return new AuthResponse(accessToken, refreshToken, savedUser.getId(), savedUser.getEmail(), role.getName().name(), false);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String email = tokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        boolean mustChange = user.isMustChangePassword();
        return new AuthResponse(newAccessToken, refreshToken, user.getId(), user.getEmail(), user.getRole().getName().name(), mustChange);
    }
}