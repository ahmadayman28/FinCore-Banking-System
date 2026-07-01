package com.fincore.fincorebank.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fincore.fincorebank.response.Response;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response<?>> handleAllUnknownExceptions(Exception exception) {
		Response<?> response = Response.builder().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).message(exception.getMessage()).build();
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Response<?>> handleNotFoundExceptions(NotFoundException exception) {
		Response<?> response = Response.builder().statusCode(HttpStatus.NOT_FOUND.value()).message(exception.getMessage()).build();
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<Response<?>> handleInsufficientBalance(InsufficientBalanceException exception) {
		Response<?> response = Response.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage()).build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidTransactionException.class)
	public ResponseEntity<Response<?>> handleInvalidTransactionException(InvalidTransactionException exception) {
		Response<?> response = Response.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage()).build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Response<?>> handleBadRequestException(BadRequestException exception) {
		Response<?> response = Response.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage()).build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}