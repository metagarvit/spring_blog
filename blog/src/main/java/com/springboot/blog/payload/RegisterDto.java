package com.springboot.blog.payload;

import jakarta.validation.constraints.NegativeOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDto {
	private String name ; 
	private String username;
	 private String email;
	 private String password;
	 
	 
	 private String profileImage ;

}
