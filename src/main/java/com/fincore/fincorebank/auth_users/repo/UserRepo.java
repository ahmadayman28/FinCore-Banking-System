package com.fincore.fincorebank.auth_users.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fincore.fincorebank.auth_users.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}