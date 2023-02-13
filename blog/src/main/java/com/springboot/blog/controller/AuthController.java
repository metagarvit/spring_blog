package com.springboot.blog.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.payload.ChangePasswordDto;
import com.springboot.blog.payload.JWTAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.payload.UserDetailsDto;
import com.springboot.blog.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	

	@Autowired
	private ObjectMapper mapper;
	
	//Build Login Rest API
	@PostMapping(value = {"auth/login" , "auth/signin"})
	public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto)
	{
		
		return ResponseEntity.ok(authService.login(loginDto));
	}
	
	//Build Register Rest API
	@PostMapping(value = {"auth/signup" , "auth/register"})
	public ResponseEntity<String> signUp(@RequestBody RegisterDto registerDto)
	{
		String response = authService.register(registerDto);
		return ResponseEntity.ok(response);
	}
	
	//Build Register Rest API
	@PostMapping(value = {"auth/v2/signup" , "auth/v2/register"})
	public ResponseEntity<String> signUp(@Valid @RequestParam("data") String data,
			@RequestParam("file") MultipartFile file) throws IOException
	{
		
		// File path
		String UPLOAD_DIR = new ClassPathResource("public/images").getFile().getAbsolutePath();
		
		String fileName =  (  "" + System.currentTimeMillis() + file.getOriginalFilename() );
		// Save file
		Files.copy(file.getInputStream(), Paths.get(UPLOAD_DIR + File.separator + fileName),
				StandardCopyOption.REPLACE_EXISTING);

		// get file uri
		String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/")
				.path(fileName).toUriString();

		// converting string to json
		RegisterDto registerDto = mapper.readValue(data, RegisterDto.class);
		registerDto.setProfileImage(uri);
		
		String response = authService.register(registerDto);
		return ResponseEntity.ok(response);
	}
	
	
	//Change Password API
	@PostMapping(value = {"/changePassword" })
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto)
	{
		
		return ResponseEntity.ok(authService.changePassword(changePasswordDto));
	}
	
	
	//User Details
	@GetMapping(value = {"/userDetails" })
	public ResponseEntity<UserDetailsDto> userDetails()
	{
		
		return ResponseEntity.ok(authService.getCurrentUserDetails());
	}
	

	
}
