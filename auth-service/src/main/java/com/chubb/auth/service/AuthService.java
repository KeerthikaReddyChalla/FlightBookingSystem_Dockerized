package com.chubb.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chubb.auth.ChangePasswordRequest;
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
        repo.save(user);
    }

    public JwtResponse login(LoginRequest req) {
        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken( user.getName(),  user.getRole(), user.getEmail());
        return new JwtResponse(token);
    }
    public void changePassword(String email, ChangePasswordRequest req) {

        User user = repo.findByEmail(email)
                .orElseThrow();

        if (!encoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPassword(encoder.encode(req.getNewPassword()));
        repo.save(user);
    }

}

