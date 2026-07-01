package com.fincore.fincorebank.auth_users.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fincore.fincorebank.account.dtos.AccountDTO;
import com.fincore.fincorebank.role.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

	private Long id;
	
	private String firstName;
	private String lastName;
	private String phoneNumber;
	
	private String email;
	
	@JsonIgnore
	private String password;
	
	private String profilePictureUrl;
	private boolean active;
	
	private List<Role> roles;
	
	@JsonManagedReference
	private List<AccountDTO> accounts;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}