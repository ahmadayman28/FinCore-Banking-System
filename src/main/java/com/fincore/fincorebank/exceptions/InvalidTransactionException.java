package com.fincore.fincorebank.exceptions;

public class InvalidTransactionException extends RuntimeException {
	public InvalidTransactionException(String message) {
		super(message);
	}
}