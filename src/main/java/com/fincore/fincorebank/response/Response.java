package com.fincore.fincorebank.response;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
	private int statusCode;
	private String message;
	private T data;
	private Map<String, Serializable> meta;
}