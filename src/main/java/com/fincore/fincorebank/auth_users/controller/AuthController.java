package com.fincore.fincorebank.auth_users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fincore.fincorebank.auth_users.dtos.LoginRequest;
import com.fincore.fincorebank.auth_users.dtos.LoginResponse;
import com.fincore.fincorebank.auth_users.dtos.RegisterationRequest;
import com.fincore.fincorebank.auth_users.dtos.ResetPasswordRequest;
import com.fincore.fincorebank.auth_users.service.AuthService;
import com.fincore.fincorebank.response.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<Response<String>> register(@RequestBody @Valid RegisterationRequest registerationRequest){
		return ResponseEntity.ok(authService.register(registerationRequest));
	}
	
	@PostMapping("/login")
	public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest){
		return ResponseEntity.ok(authService.login(loginRequest));
	}
	
	@PostMapping("/forget-password")
	public ResponseEntity<Response<?>> forgetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
		return ResponseEntity.ok(authService.forgetPassword(resetPasswordRequest.getEmail()));
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<Response<?>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
		return ResponseEntity.ok(authService.updatePasswordViaResetCode(resetPasswordRequest));
	}
	
}