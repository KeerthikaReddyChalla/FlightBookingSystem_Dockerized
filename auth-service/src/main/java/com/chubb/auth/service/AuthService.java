package com.chubb.auth.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chubb.auth.dto.ChangePasswordRequest;
import com.chubb.auth.dto.JwtResponse;
import com.chubb.auth.dto.LoginRequest;
import com.chubb.auth.dto.RegisterRequest;
import com.chubb.auth.exception.UserAlreadyExistsException;
import com.chubb.auth.models.User;
import com.chubb.auth.repository.UserRepository;
import com.chubb.auth.security.JwtUtil;

import jakarta.ws.rs.BadRequestException;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    // ========================= REGISTER =========================
    public void register(RegisterRequest req) {

        if (repo.existsByEmail(req.getEmail())) {
            throw new UserAlreadyExistsException("User already registered");
        }

        if (req.getName() == null || req.getName().isBlank()) {
            throw new BadRequestException("Name is required");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole("ROLE_USER");

        user.setPasswordLastChangedAt(LocalDateTime.now());
        user.setForcePasswordChange(false);

        repo.save(user);
    }

    // ========================= LOGIN =========================
    public JwtResponse login(LoginRequest req) {

        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        boolean forcePasswordChange = false;
        long DAYS_LIMIT = 60;

        // 🚨 IMPORTANT: DO NOT force password change for ADMIN
        if (!"ROLE_ADMIN".equalsIgnoreCase(user.getRole())) {

            LocalDateTime lastChanged = user.getPasswordLastChangedAt();

            if (lastChanged == null) {
                // legacy user safety
                forcePasswordChange = true;
                user.setPasswordLastChangedAt(LocalDateTime.now());
            } else {
                long daysSinceChange =
                        ChronoUnit.DAYS.between(lastChanged, LocalDateTime.now());
                forcePasswordChange = daysSinceChange > DAYS_LIMIT;
            }
        }

        user.setForcePasswordChange(forcePasswordChange);
        repo.save(user);

        String token = jwtUtil.generateToken(
                user.getName(),
                user.getRole(),
                user.getEmail(),
                user.isForcePasswordChange()
        );

        return new JwtResponse(token);
    }

    // ========================= CHANGE PASSWORD =========================
    public void changePassword(String email, ChangePasswordRequest req) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPassword(encoder.encode(req.getNewPassword()));
        user.setPasswordLastChangedAt(LocalDateTime.now());
        user.setForcePasswordChange(false);

        repo.save(user);
    }

    // ========================= FORGOT PASSWORD =========================
    public void forgotPassword(String email) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(
                LocalDateTime.now().plusMinutes(15)
        );

        repo.save(user);

        // DEV MODE: printed to console
        System.out.println(
            "Reset link: http://localhost:4200/reset-password?token=" + token
        );
    }


    public void resetPassword(String token, String newPassword) {

        User user = repo.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token expired");
        }

  
        if (!newPassword.matches(
                "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$")) {
            throw new RuntimeException("Password does not meet policy");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setPasswordLastChangedAt(LocalDateTime.now());
        user.setForcePasswordChange(false);

        // clear reset token
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        repo.save(user);
    }
}
