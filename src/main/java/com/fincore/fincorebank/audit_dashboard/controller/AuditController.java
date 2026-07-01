package com.fincore.fincorebank.audit_dashboard.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fincore.fincorebank.account.dtos.AccountDTO;
import com.fincore.fincorebank.audit_dashboard.service.AuditService;
import com.fincore.fincorebank.auth_users.dtos.UserDTO;
import com.fincore.fincorebank.transaction.dtos.TransactionDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AUDITOR')")
public class AuditController {
	private final AuditService auditService;
	
	@GetMapping("/totals")
	public ResponseEntity<Map<String,Long>> getSystemTotals() {
        Map<String, Long> totals = auditService.getSystemTotals();
        return ResponseEntity.ok(totals);
    }
	
	@GetMapping("/user")
	public ResponseEntity<UserDTO> findUserByEmail(@RequestParam String email) {
		return auditService.findUserByEmail(email).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/accounts")
	public ResponseEntity<AccountDTO> findAccountDetailsByAccountNumber(@RequestParam String accountNumber) {
		return auditService.findAccountDetailsByAccountNumber(accountNumber).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/transactions/account")
	public ResponseEntity<List<TransactionDTO>> findTransactionsByAccountNumber(@RequestParam String accountNumber) {
		List<TransactionDTO> transactions = auditService.findTransactionsByAccountNumber(accountNumber);
        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transactions);
	}
	
	@GetMapping("/transactions")
	public ResponseEntity<TransactionDTO> findTransactionById(@RequestParam(name = "id") Long transactionId) {
	    return auditService.findTransactionById(transactionId)
	            .map(ResponseEntity::ok)
	            .orElse(ResponseEntity.notFound().build());
	}
}
