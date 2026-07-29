package com.fincore.fincorebank.auth_users.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fincore.fincorebank.auth_users.dtos.UpdatePasswordRequest;
import com.fincore.fincorebank.auth_users.dtos.UserDTO;
import com.fincore.fincorebank.auth_users.service.UserService;
import com.fincore.fincorebank.response.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;

	@GetMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Response<Page<UserDTO>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
		return ResponseEntity.ok(userService.getAllUsers(page, size));
	}
	
	@GetMapping("/me")
	public ResponseEntity<Response<UserDTO>> getCurrentUser() {
		return ResponseEntity.ok(userService.getMyProfile());
	}
	
	@PutMapping("/update-password")
	public ResponseEntity<Response<?>> updatePassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest) {
		return ResponseEntity.ok(userService.updatePassword(updatePasswordRequest));
	}
	
	@PutMapping("/profile-picture")
    public ResponseEntity<Response<?>> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadProfilePictureToS3(file));
    }
	
}
