package com.springboot.blog.payload;

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
public class JWTAuthResponse {
	
	private String accessToken;
	private String tokenType = "Bearer";
	
	private UserDetailsDto userDetailsDto;
	
	

}
