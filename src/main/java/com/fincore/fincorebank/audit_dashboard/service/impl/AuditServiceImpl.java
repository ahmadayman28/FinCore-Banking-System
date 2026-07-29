package com.fincore.fincorebank.audit_dashboard.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.fincore.fincorebank.account.dtos.AccountDTO;
import com.fincore.fincorebank.account.repo.AccountRepo;
import com.fincore.fincorebank.audit_dashboard.service.AuditService;
import com.fincore.fincorebank.auth_users.dtos.UserDTO;
import com.fincore.fincorebank.auth_users.repo.UserRepo;
import com.fincore.fincorebank.transaction.dtos.TransactionDTO;
import com.fincore.fincorebank.transaction.repo.TransactionRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService{
	
	private final UserRepo userRepo;
	private final AccountRepo accountRepo;
	private final TransactionRepo transactionRepo;
	private final ModelMapper modelMapper;
	
	@Override
	public Map<String, Long> getSystemTotals() {
	    Long totalUsers = userRepo.count();
	    Long totalAccounts = accountRepo.count();
	    Long totalTransactions = transactionRepo.count();
	    
	    return Map.of(
	            "totalUsers", totalUsers,
	            "totalAccounts", totalAccounts,
	            "totalTransactions", totalTransactions
	            );
	}

	@Override
	public Optional<UserDTO> findUserByEmail(String email) {
		return userRepo.findByEmail(email)
				.map(user->modelMapper.map(user, UserDTO.class));
	}

	@Override
	public Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber) {
		return accountRepo.findByAccountNumber(accountNumber)
				.map(account->modelMapper.map(account, AccountDTO.class));
	}

	@Override
	public List<TransactionDTO> findTransactionsByAccountNumber(String accountNumber) {
		return transactionRepo.findByAccount_AccountNumber(accountNumber)
				.stream()
				.map(transaction->modelMapper.map(transaction, TransactionDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public Optional<TransactionDTO> findTransactionById(Long transactionId) {
		return transactionRepo.findById(transactionId)
				.map(transaction->modelMapper.map(transaction, TransactionDTO.class));
	}

}