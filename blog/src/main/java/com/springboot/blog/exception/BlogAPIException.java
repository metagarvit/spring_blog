/**
 * 
 */
package com.springboot.blog.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * We throw this exception whenever we write some business logic or validate request parameters
 * @author Garvit
 *
 */

@Getter
@Setter
public class BlogAPIException  extends RuntimeException {
	
	private HttpStatus status ;
	private String message;
	/**
	 * @param status
	 * @param message
	 */
	public BlogAPIException(HttpStatus status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}
	

	
	
	

}
