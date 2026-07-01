package com.fincore.fincorebank.audit_dashboard.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fincore.fincorebank.account.dtos.AccountDTO;
import com.fincore.fincorebank.auth_users.dtos.UserDTO;
import com.fincore.fincorebank.transaction.dtos.TransactionDTO;

public interface AuditService {
	Map<String, Long> getSystemTotals();
	Optional<UserDTO> findUserByEmail(String email);
	Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber);
	List<TransactionDTO> findTransactionsByAccountNumber(String accountNumber);
	Optional<TransactionDTO> findTransactionById(Long transactionId);
}