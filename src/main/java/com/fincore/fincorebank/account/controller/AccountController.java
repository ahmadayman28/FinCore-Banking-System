package com.fincore.fincorebank.account.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fincore.fincorebank.account.service.AccountService;
import com.fincore.fincorebank.response.Response;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
	
	private final AccountService accountService;
	
	@GetMapping("/me")
	public ResponseEntity<Response<?>> getMyAccounts(){
		return ResponseEntity.ok(accountService.getMyAccounts());
	}
	
	@DeleteMapping("/close/{accountNumber}")
	public ResponseEntity<Response<?>> closedAccount(@PathVariable String accountNumber){
		return ResponseEntity.ok(accountService.closedAccount(accountNumber));
	}
}