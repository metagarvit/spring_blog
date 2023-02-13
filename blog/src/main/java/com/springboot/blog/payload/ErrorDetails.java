/**
 * 
 */
package com.springboot.blog.payload;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Garvit
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
	
	private Date timestamp;
	private String message;
	private String details;
	
	
	

}
