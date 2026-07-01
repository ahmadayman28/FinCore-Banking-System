package com.fincore.fincorebank.auth_users.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fincore.fincorebank.auth_users.entity.PasswordResetCode;

public interface PasswordResetCodeRepo extends JpaRepository<PasswordResetCode, Long> {
	Optional<PasswordResetCode> findByCode(String code);
	void deleteByUserId(long userId);
}