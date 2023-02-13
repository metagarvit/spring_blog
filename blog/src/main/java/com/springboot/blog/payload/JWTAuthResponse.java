package com.springboot.blog.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTAuthResponse {
	
	private String accessToken;
	private String tokenType = "Bearer";
	
	private UserDetailsDto userDetailsDto;
	
	

}
