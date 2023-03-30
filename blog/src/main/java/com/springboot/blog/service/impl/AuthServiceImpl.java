package com.springboot.blog.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.ChangePasswordDto;
import com.springboot.blog.payload.JWTAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.payload.UserDetailsDto;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;
	private JwtTokenProvider jwtTokenProvider;

	/**
	 * 
	 * @param authenticationManager
	 * @param userRepository
	 * @param roleRepository
	 * @param passwordEncoder
	 * @param jwtTokenProvider
	 */
	public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
			RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public JWTAuthResponse login(LoginDto loginDto) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtTokenProvider.generateToken(authentication);
		
		User user = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(), loginDto.getUsernameOrEmail()).orElseThrow( ()-> new BlogAPIException(HttpStatus.BAD_REQUEST, "User does not exist!."));
		
		UserDetailsDto userDetailsDto = new UserDetailsDto();
		userDetailsDto.setEmail(user.getEmail());
		userDetailsDto.setName(user.getName());
		userDetailsDto.setRoles(user.getRoles());
		userDetailsDto.setProfileImage(user.getProfileImage());
		userDetailsDto.setUsername(user.getUsername());
		
		JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
		jwtAuthResponse.setAccessToken(token);
		jwtAuthResponse.setUserDetailsDto(userDetailsDto);
		
		
		return jwtAuthResponse;
	}

	@Override
	public String register(RegisterDto registerDto) {

		// add check for username exists in db
		if (userRepository.existsByUsername(registerDto.getUsername())) {
			var blog = new BlogAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!.");
			throw blog;
		}

		if (userRepository.existsByEmail(registerDto.getEmail())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!.");

		}

		User user = new User();
		user.setName(registerDto.getName());
		user.setUsername(registerDto.getUsername());
		user.setProfileImage(registerDto.getProfileImage());
		user.setEmail(registerDto.getEmail());
		user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

		Set<Role> roles = new HashSet<>();
		Role userRole = roleRepository.findByName("ROLE_USER").get();
		roles.add(userRole);
		user.setRoles(roles);

		userRepository.save(user);

		return "User registered successfully";
	}

	@Override
	public String changePassword(ChangePasswordDto changePassDto) {
		passwordEncoder = new BCryptPasswordEncoder();
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username).orElseThrow( ()-> new BlogAPIException(HttpStatus.BAD_REQUEST, "User does not exist!."));
		System.out.println("user pass->" + user.getPassword());
		System.out.println("->>>" + changePassDto.getOldPassword());
		if(!passwordEncoder.matches(changePassDto.getOldPassword(), user.getPassword()))
		{
			
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Password does not match");	
		}
		
		user.setPassword(passwordEncoder.encode(changePassDto.getNewPassword()));
		userRepository.save(user);

		
		return "Change Password Successful";
		
	}

	@Override
	public UserDetailsDto getCurrentUserDetails() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username).orElseThrow( ()-> new BlogAPIException(HttpStatus.BAD_REQUEST, "User does not exist!."));
		UserDetailsDto userDetailsDto = new UserDetailsDto();
		userDetailsDto.setEmail(user.getEmail());
		userDetailsDto.setName(user.getName());
		userDetailsDto.setProfileImage(user.getProfileImage());
		userDetailsDto.setRoles(user.getRoles());
		userDetailsDto.setUsername(user.getUsername());
		return  userDetailsDto;
	}

}
