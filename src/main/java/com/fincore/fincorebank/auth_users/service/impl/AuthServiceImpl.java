package com.fincore.fincorebank.auth_users.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fincore.fincorebank.account.entity.Account;
import com.fincore.fincorebank.account.service.AccountService;
import com.fincore.fincorebank.auth_users.dtos.LoginRequest;
import com.fincore.fincorebank.auth_users.dtos.LoginResponse;
import com.fincore.fincorebank.auth_users.dtos.RegisterationRequest;
import com.fincore.fincorebank.auth_users.dtos.ResetPasswordRequest;
import com.fincore.fincorebank.auth_users.entity.PasswordResetCode;
import com.fincore.fincorebank.auth_users.entity.User;
import com.fincore.fincorebank.auth_users.repo.PasswordResetCodeRepo;
import com.fincore.fincorebank.auth_users.repo.UserRepo;
import com.fincore.fincorebank.auth_users.service.AuthService;
import com.fincore.fincorebank.auth_users.service.CodeGenerator;
import com.fincore.fincorebank.enums.AccountType;
import com.fincore.fincorebank.enums.Currency;
import com.fincore.fincorebank.exceptions.BadRequestException;
import com.fincore.fincorebank.exceptions.NotFoundException;
import com.fincore.fincorebank.notification.dtos.NotificationDTO;
import com.fincore.fincorebank.notification.service.NotificationService;
import com.fincore.fincorebank.response.Response;
import com.fincore.fincorebank.role.entity.Role;
import com.fincore.fincorebank.role.repo.RoleRepo;
import com.fincore.fincorebank.security.TokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.servicemetadata.AccountServiceMetadata;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
	
	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final NotificationService notificationService;
	private final CodeGenerator codeGenerator;
	private final PasswordResetCodeRepo passwordResetCodeRepo;
	private final AccountService accountService;
	
	@Value("${password.reset.link}")
	private String resetLink;
	
	@Override
	public Response<String> register(RegisterationRequest request) {

        List<Role> roles;

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            Role defaultRole = roleRepo.findByName("CUSTOMER")
                    .orElseThrow(() -> new NotFoundException("CUSTOMER ROLE NOT FOUND"));

            roles = Collections.singletonList(defaultRole);
        } else {
            roles = request.getRoles().stream()
                    .map(roleName -> roleRepo.findByName(roleName)
                            .orElseThrow(() -> new NotFoundException("ROLE NOT FOUND" + roleName)))
                    .toList();
        }

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email Already Present");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumebr())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .active(true)
                .build();

        User savedUser = userRepo.save(user);

        Account savedAccount = accountService.createAccount(AccountType.SAVINGS, savedUser);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", savedUser.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Welcome to finCore Bank 🎉")
                .templateName("welcome")
                .templateVariables(vars)
                .build();

        notificationService.sendEmail(notificationDTO, savedUser);


        Map<String, Object> accountVars = new HashMap<>();
        accountVars.put("name", savedUser.getFirstName());
        accountVars.put("accountNumber", savedAccount.getAccountNumber());
        accountVars.put("accountType", AccountType.SAVINGS.name());
        accountVars.put("currency", Currency.USD);

        NotificationDTO accountCreatedEmail = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Your New Bank Account Has Been Created ✅")
                .templateName("account-created")
                .templateVariables(accountVars)
                .build();

        notificationService.sendEmail(accountCreatedEmail, savedUser);

        return Response.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your account has been created successfully")
                .data("Email of your account details has been sent to you. Your account number is: " + savedAccount.getAccountNumber())
                .build();
    }

	@Override
	public Response<LoginResponse> login(LoginRequest loginRequest) {
		String email = loginRequest.getEmail();
		String password = loginRequest.getPassword();
		User user = userRepo.findByEmail(email).orElseThrow(()-> new NotFoundException("Email not found"));
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadRequestException("Password doesn't match");
		}
		String token =  tokenService.generateToken(user.getEmail());
		LoginResponse loginResponse = LoginResponse.builder()
				.roles(user.getRoles().stream().map(Role::getName).toList())
				.token(token)
				.build();
		
		return Response.<LoginResponse>builder()
				.statusCode(HttpStatus.OK.value())
				.message("Login successful")
				.data(loginResponse)
				.build();
	}

	@Override
	@Transactional
	public Response<?> forgetPassword(String email) {
		User user = userRepo.findByEmail(email).orElseThrow(()-> new NotFoundException("User not found"));
		passwordResetCodeRepo.deleteByUserId(user.getId());
		String code = codeGenerator.generateUniqueCode(); 
		PasswordResetCode resetCode = PasswordResetCode.builder()
				.user(user)
				.code(code)
				.expiryDate(calculateExpiryDate())
				.used(false)
				.build();
		passwordResetCodeRepo.save(resetCode);
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("name", user.getFirstName());
		tempMap.put("resetLink", resetLink + code);
		NotificationDTO notificationDTO = NotificationDTO.builder()
				.recipient(user.getEmail())
				.subject("Password Reset Code")
				.templateName("password-reset")
				.templateVariables(tempMap)
				.build();
		notificationService.sendEmail(notificationDTO, user);
		return Response.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Password reset code sent to your email")
				.build();
	}

	private LocalDateTime calculateExpiryDate() {
		return LocalDateTime.now().plusHours(3);
	}

	@Override
	@Transactional
	public Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {
		String code = resetPasswordRequest.getCode();
		String newPassword = resetPasswordRequest.getNewPassword();
		PasswordResetCode resetCode = passwordResetCodeRepo.findByCode(code)
				.orElseThrow(()->new BadRequestException("Invalid reset code"));
		if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
			passwordResetCodeRepo.delete(resetCode);
			throw new BadRequestException("Reset code has expired");
		}
		User user = resetCode.getUser();
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepo.save(user);
		passwordResetCodeRepo.delete(resetCode);
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("name", user.getFirstName());
		NotificationDTO notificationDTO = NotificationDTO.builder()
				.recipient(user.getEmail())
				.subject("Password Upated Successfully")
				.templateName("password-update-confirmation")
				.templateVariables(tempMap)
				.build();
		notificationService.sendEmail(notificationDTO, user);
		return Response.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Password updated successfully")
				.build();
	}

}