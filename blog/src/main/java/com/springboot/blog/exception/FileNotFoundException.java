package com.springboot.blog.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileNotFoundException extends RuntimeException {
	
	private HttpStatus status ;
	private String message;
	/**
	 * @param status
	 * @param message
	 */
	public FileNotFoundException(HttpStatus status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}
	/**
	 * @param message
	 * @param status
	 * @param message2
	 */
	public FileNotFoundException(String message, HttpStatus status, String message2) {
		super(message);
		this.status = status;
		message = message2;
	}

	
	
	

}
