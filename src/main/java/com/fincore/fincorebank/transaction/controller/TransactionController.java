package com.fincore.fincorebank.transaction.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fincore.fincorebank.response.Response;
import com.fincore.fincorebank.transaction.dtos.TransactionDTO;
import com.fincore.fincorebank.transaction.dtos.TransactionRequest;
import com.fincore.fincorebank.transaction.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
	private final TransactionService transactionService;
	
	@PostMapping
	public ResponseEntity<Response<?>> createTransaction(@RequestBody @Valid TransactionRequest transactionRequest){
		return ResponseEntity.ok(transactionService.createTransaction(transactionRequest));
	}
	
	@GetMapping("/{accountNumber}")
	public ResponseEntity<Response<List<TransactionDTO>>> getTransactionForMyAccount(@PathVariable String accountNumber, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int size){
		return ResponseEntity.ok(transactionService.getTransactionForMyAccount(accountNumber, page, size));
	}
}
