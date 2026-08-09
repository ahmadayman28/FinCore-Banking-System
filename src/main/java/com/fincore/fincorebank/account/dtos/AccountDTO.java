package com.fincore.fincorebank.account.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fincore.fincorebank.auth_users.dtos.UserDTO;
import com.fincore.fincorebank.enums.AccountStatus;
import com.fincore.fincorebank.enums.AccountType;
import com.fincore.fincorebank.enums.Currency;
import com.fincore.fincorebank.transaction.dtos.TransactionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {
	private Long id;
	
	private String accountNumber;
	
	private BigDecimal balance;
	
	private AccountType accountType;
	
	@JsonBackReference
	private UserDTO user;
	
	private AccountStatus status;
	
	private Currency currency;
	
	@JsonManagedReference
	private List<TransactionDTO> transactions;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private  LocalDateTime closedAt;
}