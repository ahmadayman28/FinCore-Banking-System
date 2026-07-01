package com.fincore.fincorebank.account.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fincore.fincorebank.auth_users.entity.User;
import com.fincore.fincorebank.enums.AccountStatus;
import com.fincore.fincorebank.enums.AccountType;
import com.fincore.fincorebank.enums.Currency;
import com.fincore.fincorebank.transaction.entity.Transaction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 16)
	private String accountNumber;
	
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal balance = BigDecimal.ZERO;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountType accountType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Enumerated(EnumType.STRING)
	private AccountStatus status;
	
	@Enumerated(EnumType.STRING)
	private Currency currency;
	
	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL ,orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Transaction> transactions = new ArrayList<>();
	
	private LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime updatedAt;
	private  LocalDateTime closedAt;
}