package com.fincore.fincorebank.account.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fincore.fincorebank.account.dtos.AccountDTO;
import com.fincore.fincorebank.account.entity.Account;
import com.fincore.fincorebank.account.repo.AccountRepo;
import com.fincore.fincorebank.account.service.AccountService;
import com.fincore.fincorebank.auth_users.entity.User;
import com.fincore.fincorebank.auth_users.service.UserService;
import com.fincore.fincorebank.enums.AccountStatus;
import com.fincore.fincorebank.enums.AccountType;
import com.fincore.fincorebank.enums.Currency;
import com.fincore.fincorebank.exceptions.BadRequestException;
import com.fincore.fincorebank.exceptions.NotFoundException;
import com.fincore.fincorebank.response.Response;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService{
	
	private final AccountRepo accountRepo;
	private final UserService userService;
	private final ModelMapper modelMapper;
	private final Random random = new Random();
	
	@Override
	public Account createAccount(AccountType accountType, User user) {
		log.info("Inside account");
		String accountNumber = generateAccountNumber();
		Account account = Account.builder()
				.accountNumber(accountNumber)
				.accountType(accountType)
				.currency(Currency.USD)
				.balance(BigDecimal.ZERO)
				.status(AccountStatus.ACTIVE)
				.user(user)
				.createdAt(LocalDateTime.now())
				.build();
		return accountRepo.save(account);
	}

	@Override
	public Response<List<AccountDTO>> getMyAccounts() {
		User user = userService.getCurrentLoggedInUser();
		List<AccountDTO> accounts = accountRepo.findByUserId(user.getId())
				.stream()
				.map(account->modelMapper.map(account, AccountDTO.class))
				.toList();
		return Response.<List<AccountDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User accounts fetched successfully")
                .data(accounts)
                .build();
	}

	@Override
	public Response<?> closedAccount(String accountNumber) {
		User user = userService.getCurrentLoggedInUser();
		Account account = accountRepo.findByAccountNumber(accountNumber)
				.orElseThrow(()->new NotFoundException("Account Not Found!"));
		if (!user.getAccounts().contains(account)) {
			throw new NotFoundException("Account doesn't belong to you");
		}
		if (account.getBalance().compareTo(BigDecimal.ZERO)>0) {
			throw new BadRequestException("Account balance must be zero before closing.");
		}
		account.setStatus(AccountStatus.CLOSED);
		account.setClosedAt(LocalDateTime.now());
		accountRepo.save(account);
		return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account closed successfully")
                .build();
	}
	
	private String generateAccountNumber() {
		String accountNumber;
		do {
			accountNumber = "99" + (random.nextInt(90000000)+10000000);
		} while (accountRepo.findByAccountNumber(accountNumber).isPresent());
		log.info("account number generated {}", accountNumber);
		return accountNumber;
	}
}
