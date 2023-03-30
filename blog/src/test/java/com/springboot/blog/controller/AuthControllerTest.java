package com.springboot.blog.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.entity.Category;
import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.payload.ChangePasswordDto;
import com.springboot.blog.payload.JWTAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.payload.UserDetailsDto;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.CustomUserDetailsService;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.AuthService;
import com.springboot.blog.service.CategoryService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@MockitoSettings(strictness = Strictness.WARN)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {
	
	MockMvc mockMvc;

	@Mock
	private AuthService authService;


	@InjectMocks
	private AuthController authController;


	@BeforeAll
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(authController) .build();
	}
	
	
	public static String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}

	
	/**
	 * Login
	 */
	@DisplayName("Test Mock auth controller login method + authService")
	@Test
	void testLogin() throws Exception {
		
		// save all post
		when(authService.login(any(LoginDto.class))).thenReturn(JWTAuthResponse.builder().accessToken("Testing").build());
		
		
		// perform the call
		 mockMvc.perform(MockMvcRequestBuilders
		            .post("/api/auth/login")
		            .contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(asJsonString(new LoginDto())))
		            .andExpect(status().isOk())
		            .andExpect(jsonPath("$.accessToken").value("Testing"));
	}
	
	
	/**
	 * Change Password
	 */ 
	@DisplayName("Test Mock auth controller changePassword method + authService")
	@Test
	void testChangePassword() throws Exception {
		
		// save all post
		when(authService.changePassword(any(ChangePasswordDto.class))).thenReturn("Change Password Successful");
		
		// perform the call
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/changePassword")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(new LoginDto())))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").value("Change Password Successful"));
	}
	
	
	/**
	 * SignUp
	 */
	@DisplayName("Test Mock auth controller signUp method + authService")
	@Test
	void testSignUp() throws Exception {
		
		// save all post
		when(authService.register(any(RegisterDto.class))).thenReturn("User registered successfully");
		
		
		// perform the call
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(new RegisterDto())))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").value("User registered successfully"));
	}

	
	
	
	/**
	 * User Details
	 */
	@DisplayName("Test Mock auth controller userDetails method + authService")
	@Test
	void testuserDetails() throws Exception {
		
		// save all post
		when(authService.getCurrentUserDetails()).thenReturn(UserDetailsDto.builder().name("Testing").email("testing@gmail.com").username("testing").build());
		
		
		// perform the call
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/userDetails")
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name").value("Testing"))
		.andExpect(jsonPath("$.email").value("testing@gmail.com"));
	}
	
	
	

}
