package com.fincore.fincorebank.transaction.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fincore.fincorebank.account.entity.Account;
import com.fincore.fincorebank.account.repo.AccountRepo;
import com.fincore.fincorebank.auth_users.entity.User;
import com.fincore.fincorebank.auth_users.service.UserService;
import com.fincore.fincorebank.enums.TransactionStatus;
import com.fincore.fincorebank.enums.TransactionType;
import com.fincore.fincorebank.exceptions.BadRequestException;
import com.fincore.fincorebank.exceptions.InsufficientBalanceException;
import com.fincore.fincorebank.exceptions.InvalidTransactionException;
import com.fincore.fincorebank.exceptions.NotFoundException;
import com.fincore.fincorebank.notification.dtos.NotificationDTO;
import com.fincore.fincorebank.notification.service.NotificationService;
import com.fincore.fincorebank.response.Response;
import com.fincore.fincorebank.transaction.dtos.TransactionDTO;
import com.fincore.fincorebank.transaction.dtos.TransactionRequest;
import com.fincore.fincorebank.transaction.entity.Transaction;
import com.fincore.fincorebank.transaction.repo.TransactionRepo;
import com.fincore.fincorebank.transaction.service.TransactionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService{
	
	private final TransactionRepo transactionRepo;
	private final AccountRepo accountRepo;
	private final NotificationService notificationService;
	private final UserService userService;
	private final ModelMapper modelMapper;
	
	@Override
	@Transactional
	public Response<?> createTransaction(TransactionRequest transactionRequest) {
	    if (transactionRequest.getTransactionType() == TransactionType.TRANSFER) {
	        handleTransfer(transactionRequest);
	    } else {
	        Transaction transaction = new Transaction();
	        transaction.setTransactionType(transactionRequest.getTransactionType());
	        transaction.setAmount(transactionRequest.getAmount());
	        transaction.setDescription(transactionRequest.getDescription());
	        transaction.setStatus(TransactionStatus.COMPLETED);

	        switch (transactionRequest.getTransactionType()) {
	            case DEPOSIT -> handleDeposite(transactionRequest, transaction);
	            case WITHDRAWAL -> handleWithdrawal(transactionRequest, transaction);
	            default -> throw new InvalidTransactionException("Invalid transaction type");
	        }

	        Transaction savedTransaction = transactionRepo.save(transaction);
	        sendTransactionNotification(savedTransaction);
	    }

	    return Response.builder()
	            .statusCode(HttpStatus.OK.value())
	            .message("Transaction successful")
	            .build();
	}

	@Override
	@Transactional
	public Response<List<TransactionDTO>> getTransactionForMyAccount(String accountNumber, int page, int size) {
	    User user = userService.getCurrentLoggedInUser();
	    
	    Account account = accountRepo.findByAccountNumber(accountNumber)
	            .orElseThrow(() -> new NotFoundException("Account not found"));
	            
	    if (!account.getUser().getId().equals(user.getId())) {
	        throw new BadRequestException("Account doesn't belong to the authenticated user");       
	    }
	    
	    Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
	    
	    Page<Transaction> transactions = transactionRepo.findByAccount_AccountNumber(accountNumber, pageable);
	    
	    List<TransactionDTO> transactionDTOs = transactions.getContent()
	            .stream()
	            .map(transaction -> {
	                TransactionDTO dto = modelMapper.map(transaction, TransactionDTO.class);
	                dto.setSourceAccount(transaction.getSourceAccount());
	                dto.setDestinationAccount(transaction.getDestinationAccount());
	                return dto;
	            })
	            .toList();

	    return Response.<List<TransactionDTO>>builder()
	            .statusCode(HttpStatus.OK.value())
	            .message("Transactions retrieved")
	            .data(transactionDTOs)
	            .meta(Map.of(
	                    "currentPage", transactions.getNumber(),
	                    "totalItems", transactions.getTotalElements(),
	                    "totalPages", transactions.getTotalPages(),
	                    "pageSize", transactions.getSize()
	            ))
	            .build();
	}
	
	private void handleTransfer(TransactionRequest transactionRequest) {
	    Account sourceAccount = accountRepo.findByAccountNumber(transactionRequest.getAccountNumber())
	            .orElseThrow(() -> new NotFoundException("Source account not found"));
	            
	    Account destinationAccount = accountRepo.findByAccountNumber(transactionRequest.getDestinationAccountNumber())
	            .orElseThrow(() -> new NotFoundException("Destination account not found"));

	    if (sourceAccount.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
	        throw new InsufficientBalanceException("Balance not enough");
	    }

	    LocalDateTime now = LocalDateTime.now();

	    sourceAccount.setBalance(sourceAccount.getBalance().subtract(transactionRequest.getAmount()));
	    accountRepo.save(sourceAccount);

	    destinationAccount.setBalance(destinationAccount.getBalance().add(transactionRequest.getAmount()));
	    accountRepo.save(destinationAccount);

	    Transaction senderTx = Transaction.builder()
	            .account(sourceAccount)
	            .amount(transactionRequest.getAmount())
	            .transactionType(TransactionType.TRANSFER)
	            .transactionDate(now)
	            .description(transactionRequest.getDescription() != null && !transactionRequest.getDescription().isBlank() 
	                    ? transactionRequest.getDescription() 
	                    : "Transfer to " + destinationAccount.getAccountNumber())
	            .sourceAccount(sourceAccount.getAccountNumber())
	            .destinationAccount(destinationAccount.getAccountNumber())
	            .status(TransactionStatus.COMPLETED)
	            .build();
	    transactionRepo.save(senderTx);

	    Transaction receiverTx = Transaction.builder()
	            .account(destinationAccount)
	            .amount(transactionRequest.getAmount())
	            .transactionType(TransactionType.DEPOSIT) 
	            .transactionDate(now)
	            .description("Transfer from " + sourceAccount.getAccountNumber() + 
	                    (transactionRequest.getDescription() != null && !transactionRequest.getDescription().isBlank() 
	                    ? " - " + transactionRequest.getDescription() : ""))
	            .sourceAccount(sourceAccount.getAccountNumber())
	            .destinationAccount(destinationAccount.getAccountNumber())
	            .status(TransactionStatus.COMPLETED)
	            .build();
	    transactionRepo.save(receiverTx);

	    sendTransactionNotification(senderTx);
	    sendTransactionNotification(receiverTx);
	}

	private void handleWithdrawal(TransactionRequest transactionRequest, Transaction transaction) {
		Account account = accountRepo.findByAccountNumber(transactionRequest.getAccountNumber())
				.orElseThrow(()-> new NotFoundException("Account not found"));
		if (account.getBalance().compareTo(transactionRequest.getAmount())<0) {
			throw new InsufficientBalanceException("Balance not enuogh");
		}
		account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
		transaction.setAccount(account);
		accountRepo.save(account);
	}

	private void handleDeposite(TransactionRequest transactionRequest, Transaction transaction) {
		Account account = accountRepo.findByAccountNumber(transactionRequest.getAccountNumber())
				.orElseThrow(()-> new NotFoundException("Account not found"));
		account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
		transaction.setAccount(account);
		accountRepo.save(account);
		
	}
	
	private void sendTransactionNotification(Transaction transaction) {
	    User user = transaction.getAccount().getUser();
	    String subject, template;
	    Map<String, Object> templateVariableMap = new HashMap<>();
	    templateVariableMap.put("name", user.getFirstName());
	    templateVariableMap.put("amount", transaction.getAmount());
	    templateVariableMap.put("account number", transaction.getAccount().getAccountNumber());
	    templateVariableMap.put("date", transaction.getTransactionDate());
	    templateVariableMap.put("balance", transaction.getAccount().getBalance());

	    if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
	        subject = "Credit Alert";
	        template = "credit-alert";
	    } else {
	        subject = "Debit Alert";
	        template = "debit-alert";
	    }

	    NotificationDTO notificationEmailToSentOut = NotificationDTO.builder()
	            .recipient(user.getEmail())
	            .subject(subject)
	            .templateName(template)
	            .templateVariables(templateVariableMap)
	            .build();

	    notificationService.sendEmail(notificationEmailToSentOut, user);
	}

}
