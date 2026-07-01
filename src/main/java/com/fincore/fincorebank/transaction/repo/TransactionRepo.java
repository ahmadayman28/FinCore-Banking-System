package com.fincore.fincorebank.transaction.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fincore.fincorebank.account.entity.Account;
import com.fincore.fincorebank.transaction.entity.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction, Long>{
	Page<Transaction> findByAccount_AccountNumber(String accountNumber, Pageable pageable);
	List<Transaction> findByAccount_AccountNumber(String accountNumber);
	List<Transaction> findByAccount_AccountNumber(Account account);
}
