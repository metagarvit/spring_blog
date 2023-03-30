/**
 * 
 */
package com.springboot.blog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom ResourceNotFound Exception
 * 
 * @author Garvit
 *
 */

//Used to respond with the specified HTTP status code whenever this exception is thrown from you controller
@ResponseStatus(value = HttpStatus.NOT_FOUND) 
public class ResourceNotFoundException extends RuntimeException {

	private String resouceName;
	private String fieldName;
	private String fieldValue;

	/**
	 * @param resouceName
	 * @param fieldName
	 * @param fieldValue
	 */
	public ResourceNotFoundException(String resouceName, String fieldName, String fieldValue) {
		super(String.format("%s not found with %s : %s", resouceName, fieldName, fieldValue));
		this.resouceName = resouceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	

	

	/**
	 * @return the resouceName
	 */
	public String getResouceName() {
		return resouceName;
	}

	/**
	 * @param resouceName the resouceName to set
	 */
	public void setResouceName(String resouceName) {
		this.resouceName = resouceName;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the fieldValue
	 */
	public String getFieldValue() {
		return fieldValue;
	}

	/**
	 * @param fieldValue the fieldValue to set
	 */
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	
}
