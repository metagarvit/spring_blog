/**
 * 
 */
package com.springboot.blog.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * Unauthorized User Exception
 * @author Garvit
 *
 */
@Setter
@Getter
public class UnauthorizedUserException  extends RuntimeException {
	private HttpStatus status ;
	private String message;
	/**
	 * @param status
	 * @param message
	 */
	public UnauthorizedUserException(HttpStatus status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}
	
}
