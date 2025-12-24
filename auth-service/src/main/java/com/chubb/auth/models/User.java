package com.chubb.auth.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Column;
import lombok.Data;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;  
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    private String role;
    private LocalDateTime passwordLastChangedAt;
    private boolean forcePasswordChange;
    private String resetToken;
    private LocalDateTime resetTokenExpiry;
}
