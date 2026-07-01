package com.fincore.fincorebank.transaction.service;

import java.util.List;

import com.fincore.fincorebank.response.Response;
import com.fincore.fincorebank.transaction.dtos.TransactionDTO;
import com.fincore.fincorebank.transaction.dtos.TransactionRequest;

public interface TransactionService {
	Response<?> createTransaction(TransactionRequest transactionRequest);
	Response<List<TransactionDTO>> getTransactionForMyAccount(String accountNumber, int page, int size);
}