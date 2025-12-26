package com.chubb.auth.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chubb.auth.config.RabbitConfig;
import com.chubb.auth.dto.ChangePasswordRequest;
import com.chubb.auth.dto.JwtResponse;
import com.chubb.auth.dto.LoginRequest;
import com.chubb.auth.dto.PasswordResetToken;
import com.chubb.auth.dto.RegisterRequest;
import com.chubb.auth.dto.ResetPasswordMessage;
import com.chubb.auth.dto.ResetPasswordRequest;
import com.chubb.auth.exception.UserAlreadyExistsException;
import com.chubb.auth.models.User;
import com.chubb.auth.repository.PasswordResetTokenRepository;
import com.chubb.auth.repository.UserRepository;
import com.chubb.auth.security.JwtUtil;

import jakarta.ws.rs.BadRequestException;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordResetTokenRepository Tokenrepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AmqpTemplate amqpTemplate;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtUtil jwtUtil, PasswordResetTokenRepository Tokenrepo,
    		AmqpTemplate amqpTemplate) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.Tokenrepo=Tokenrepo;
        this.amqpTemplate = amqpTemplate;
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


        if (!"ROLE_ADMIN".equalsIgnoreCase(user.getRole())) {

            LocalDateTime lastChanged = user.getPasswordLastChangedAt();

            if (lastChanged == null) {
         
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

    	Optional<User> userOpt = repo.findByEmail(email);

    	if (userOpt.isEmpty()) {
    	    return; 
    	}

    	User user = userOpt.get();
        if (user == null) return; 

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken(
            token,
            email,
            LocalDateTime.now().plusMinutes(15)
        );

        Tokenrepo.save(resetToken);

        String resetLink =
            "http://localhost:4200/reset-password?token=" + token;

        ResetPasswordMessage msg =
                new ResetPasswordMessage(email, resetLink);

        amqpTemplate.convertAndSend(
        		 "reset.password.queue",
                msg
        );

    }



    public void resetPassword(String token, String newPassword) {

        System.out.println("RESET TOKEN RECEIVED IN SERVICE: " + token);

        PasswordResetToken resetToken =
                Tokenrepo.findById(token).orElse(null);

        System.out.println("TOKEN FOUND IN DB: " + resetToken);

        if (resetToken == null) {
            throw new RuntimeException("Invalid reset token");
        }

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token expired");
        }

        User user = repo.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(encoder.encode(newPassword));
        user.setForcePasswordChange(false);

       repo.save(user);

        Tokenrepo.deleteById(token);
    }

}
