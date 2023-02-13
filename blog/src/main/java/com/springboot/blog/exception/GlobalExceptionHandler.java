package com.springboot.blog.exception;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.springboot.blog.payload.ErrorDetails;

import jakarta.validation.UnexpectedTypeException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Handle specific exception
	 * 
	 * @param exception
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception,
			WebRequest webRequest) {

		ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(),
				webRequest.getDescription(false));

		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handle specific exception
	 * 
	 * @param exception
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(BlogAPIException.class)
	public ResponseEntity<ErrorDetails> handleBlogAPIException(BlogAPIException exception, WebRequest webRequest) {

		ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(),
				webRequest.getDescription(false));
		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Handle UnauthorizedvUser Exception 
	 * 
	 * @param exception
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(UnauthorizedUserException.class)
	public ResponseEntity<ErrorDetails> handleUnauthorizedUserException(UnauthorizedUserException exception, WebRequest webRequest) {
		
		ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(),
				webRequest.getDescription(false));
		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Global exception
	 * 
	 * @param exception
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> handleAllExceptions(Exception exception, WebRequest webRequest) {

		ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(),
				webRequest.getDescription(false));

		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * File not found exception
	 * 
	 * @param exc
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<ErrorDetails> handleFileNotFoundException(FileNotFoundException exc, WebRequest webRequest) {

		ErrorDetails err = new ErrorDetails(new Date(), exc.getMessage(), webRequest.getDescription(false));

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
	}

	/**
	 * File Storage Exception
	 * 
	 * @param exc
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(FileStorageException.class)
	public ResponseEntity<ErrorDetails> handleFileStorageException(FileStorageException exc, WebRequest webRequest) {

		ErrorDetails err = new ErrorDetails(new Date(),exc.getMessage(), webRequest.getDescription(false));

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
	}

	/**
	 * Max Upload Size Exceeded Exception
	 * 
	 * @param exc
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorDetails> handleMaxSizeException(MaxUploadSizeExceededException exc,
			WebRequest webRequest) {

		ErrorDetails err = new ErrorDetails(new Date(), exc.getMessage(), webRequest.getDescription(false));

		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(err);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.put(fieldName, message);
		});

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle Access Denied Exception
	 * 
	 * @param exception
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException exception,
			WebRequest webRequest) {

		ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(),
				webRequest.getDescription(false));

		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.UNAUTHORIZED);
	}

}
