package com.dtb.tdd.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionControllerAdvice {
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
		return new ResponseEntity<String>(ex.getMessage(), ex.getStatus());
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(code= HttpStatus.BAD_REQUEST)
	public List<String> handle(MethodArgumentNotValidException ex){
		return ex.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
	}
	@ExceptionHandler(BindException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public List<String> handleBindException(BindException ex){
		return ex.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
	}
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public String handleMissingServletParameterException(MissingServletRequestParameterException ex){
		String message = ex.getParameterName();
		return "Param needed for query '"+message+"' is missing";
	}
}
