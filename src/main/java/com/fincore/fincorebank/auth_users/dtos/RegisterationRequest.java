package com.fincore.fincorebank.auth_users.dtos;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterationRequest {
	@NotBlank(message = "First Name is required")
	private String firstName;
	
	private String lastName;
	private String phoneNumebr;
	
	@Email
	@NotBlank(message = "Email is required")
	private String email;
	
	@NotBlank(message = "Password is required")
	private String password;
	
    private List<String> roles;
}