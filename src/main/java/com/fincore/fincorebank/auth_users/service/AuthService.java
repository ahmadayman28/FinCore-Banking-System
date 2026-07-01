package com.fincore.fincorebank.auth_users.service;

import com.fincore.fincorebank.auth_users.dtos.LoginRequest;
import com.fincore.fincorebank.auth_users.dtos.LoginResponse;
import com.fincore.fincorebank.auth_users.dtos.RegisterationRequest;
import com.fincore.fincorebank.auth_users.dtos.ResetPasswordRequest;
import com.fincore.fincorebank.response.Response;

public interface AuthService {
	Response<String> register(RegisterationRequest registerationRequest);
	Response<LoginResponse> login(LoginRequest loginRequest);
	Response<?> forgetPassword(String email);
	Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);
}
