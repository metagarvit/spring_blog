package com.springboot.blog.service;

import com.springboot.blog.payload.ChangePasswordDto;
import com.springboot.blog.payload.JWTAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.payload.UserDetailsDto;

public interface AuthService {
	
	JWTAuthResponse login (LoginDto loginDto);
	
	String changePassword (ChangePasswordDto changePassDto);
	
	UserDetailsDto getCurrentUserDetails ();
	
	String register (RegisterDto registerDto);

}
