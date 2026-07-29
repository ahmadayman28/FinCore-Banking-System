package com.fincore.fincorebank.auth_users.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.fincore.fincorebank.auth_users.dtos.UpdatePasswordRequest;
import com.fincore.fincorebank.auth_users.dtos.UserDTO;
import com.fincore.fincorebank.auth_users.entity.User;
import com.fincore.fincorebank.response.Response;

public interface UserService {
	User getCurrentLoggedInUser();
	Response<UserDTO> getMyProfile();
	Response<Page<UserDTO>> getAllUsers(int page, int size);
	Response<?> updatePassword(UpdatePasswordRequest updatedPasswordRequest);
	Response<?> uploadProfilePicture(MultipartFile multipartFile);
	Response<?> uploadProfilePictureToS3(MultipartFile multipartFile);
}
