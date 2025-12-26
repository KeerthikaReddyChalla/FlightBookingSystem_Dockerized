package com.chubb.auth.repository;

import com.chubb.auth.dto.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordResetTokenRepository
        extends MongoRepository<PasswordResetToken, String> {
}
