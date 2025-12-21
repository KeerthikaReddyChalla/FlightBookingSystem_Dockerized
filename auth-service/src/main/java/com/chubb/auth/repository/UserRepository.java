package com.chubb.auth.repository;

import com.chubb.auth.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
	 boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
