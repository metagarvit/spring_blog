package com.springboot.blog.payload;

import jakarta.validation.constraints.NegativeOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
	private String name ; 
	private String username;
	 private String email;
	 private String password;
	 
	 
	 private String profileImage ;

}
