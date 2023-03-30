package com.springboot.blog.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.ChangePasswordDto;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.payload.JWTAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.payload.UserDetailsDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.impl.AuthServiceImpl;
import com.springboot.blog.service.impl.CommentServiceImpl;
import com.springboot.blog.service.impl.PostServiceImpl;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@MockitoSettings(strictness = Strictness.WARN)
public class AuthServiceImplTest {

	private ModelMapper modelMapper;

	@Spy
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks // auto inject Repository
	private AuthServiceImpl authServiceImpl;

	User user;

	@BeforeEach
	void setMock() {
		passwordEncoder = new BCryptPasswordEncoder();

		Role role = Role.builder().id(1L).name("ROLE_USER").build();
		Set<Role> roles = new HashSet<>();
		roles.add(role);

		// save roles
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
		user = User.builder().id(1L).email("user@gmail.com").name("user").username("user").profileImage("User profile")
				.password(passwordEncoder.encode("user")).roles(roles).build();
		// save user
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

	}

	/**
	 * Map Comment to CommentDto
	 * 
	 * @param comment
	 * @return
	 */
//	private LoginDto mapToDto(Login comment) {
//		modelMapper = new ModelMapper();
//		CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
//		return commentDto;
//	}

	/**
	 * Login
	 */
	@DisplayName("Test Mock authService createComment method + userRepository")
	@Test
	void testLogin() {
		
		
		// save 
		when(userRepository.findByUsernameOrEmail("user" , "user")).thenReturn(Optional.of(user));
		
		JWTAuthResponse jwtAuthResponse = authServiceImpl.login(LoginDto.builder().usernameOrEmail("user").password("user").build());
		//perform the call and verify
		assertEquals("user", jwtAuthResponse.getUserDetailsDto().getUsername());
		assertEquals("user@gmail.com", jwtAuthResponse.getUserDetailsDto().getEmail());
	}

	/**
	 * Login with User not exist
	 * Throw BlogAPIException
	 */
	@DisplayName("Test Mock authService login method + userRepository")
	@Test
	void testLogin_withUserNotExist() {
		
		
		// save 
		when(userRepository.findByUsernameOrEmail("user" , "user")).thenReturn(Optional.of(user));
		
		
		BlogAPIException resourceNotFoundException = assertThrows(BlogAPIException.class,
				() ->authServiceImpl.login(LoginDto.builder().usernameOrEmail("test").password("test").build()));

		// verify
		assertEquals("User does not exist!.", resourceNotFoundException.getMessage());
	}

	/**
	 * Register user
	 */
	@DisplayName("Test Mock authService register method + userRepository")
	@Test
	void testRegister() {
		
		// save 
		when(userRepository.findByUsernameOrEmail("user" , "user")).thenReturn(Optional.of(user));
		
		RegisterDto registerDto = RegisterDto.builder().email("test@gmail.com").name("test").password("test").username("test").profileImage("testimage").build();
		//perform the call 
		String response =  authServiceImpl.register(registerDto);
		//verify
		assertEquals("User registered successfully", response);
	}

	/**
	 * Register with username already exist
	 * Throw BlogAPIException
	 */
	@DisplayName("Test Mock authService register method + userRepository")
	@Test
	void testRegister_withUserNameExist() {
		
		// save 
		when(userRepository.existsByUsername("user")).thenReturn(true);
		
		RegisterDto registerDto = RegisterDto.builder().email("test@gmail.com").name("user").password("user").username("user").profileImage("testimage").build();
		
		//perform the call 
		BlogAPIException resourceNotFoundException = assertThrows(BlogAPIException.class,
				() ->authServiceImpl.register(registerDto));

		// verify
		assertEquals("Username is already exists!.", resourceNotFoundException.getMessage());
	}

	/**
	 * Register with email already exist
	 * Throw BlogAPIException
	 */
	@DisplayName("Test Mock authService register method + userRepository")
	@Test
	void testRegister_withEmailExist() {
		
		// save 
		when(userRepository.existsByEmail("user@gmail.com")).thenReturn(true);
		
		RegisterDto registerDto = RegisterDto.builder().email("user@gmail.com").name("user").password("user").username("user").profileImage("testimage").build();
		
		//perform the call 
		BlogAPIException resourceNotFoundException = assertThrows(BlogAPIException.class,
				() ->authServiceImpl.register(registerDto));
		
		// verify
		assertEquals("Email is already exists!.", resourceNotFoundException.getMessage());
	}

	/**
	 * Change Password
	 */
	@DisplayName("Test Mock authService changePassword method + userRepository")
	@Test
	@WithMockUser()
	void testChangePassword() {
		
		// save 
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
		
		ChangePasswordDto changePasswordDto = ChangePasswordDto.builder().oldPassword("user").newPassword("newpassword").build();
		
		//perform the call 
		String response =  authServiceImpl.changePassword(changePasswordDto);
		
		//verify
		assertEquals("Change Password Successful", response);
	}

	/**
	 * Change Password with user not found
	 */
	@DisplayName("Test Mock authService changePassword method + userRepository")
	@Test
	@WithAnonymousUser
	void testChangePassword_withUserNotFound() {

		// save
//		when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

		ChangePasswordDto changePasswordDto = ChangePasswordDto.builder().oldPassword("user").newPassword("newpassword")
				.build();

		// perform the call
		BlogAPIException resourceNotFoundException = assertThrows(BlogAPIException.class,
				() -> authServiceImpl.changePassword(changePasswordDto));

		// verify
		assertEquals("User does not exist!.", resourceNotFoundException.getMessage());
	}

	/**
	 * Change Password with password not match
	 */
	@DisplayName("Test Mock authService changePassword method + userRepository")
	@Test
	@WithMockUser
	void testChangePassword_withPasswordNotMatch() {
		
		// save 
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
		
		ChangePasswordDto changePasswordDto = ChangePasswordDto.builder().oldPassword("dummy").newPassword("newpassword").build();
		
		//perform the call 
		BlogAPIException resourceNotFoundException = assertThrows(BlogAPIException.class,
						() -> authServiceImpl.changePassword(changePasswordDto));
				
		// verify
		assertEquals("Password does not match", resourceNotFoundException.getMessage());
	}

	/**
	 * Get current user details
	 */
	@DisplayName("Test Mock authService getCurrentUserDetails method + userRepository")
	@Test
	@WithMockUser
	void testGetCurrentUserDetails() {
		
		// save 
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
		
		//perform the call 
		UserDetailsDto response =  authServiceImpl.getCurrentUserDetails();
				
		//verify
		assertEquals(user.getName(), response.getName());
		assertEquals(user.getEmail(), response.getEmail());
		assertEquals(user.getUsername(), response.getUsername());
		
	}
	
	/**
	 * Get current user details
	 */
	@DisplayName("Test Mock authService getCurrentUserDetails method + userRepository")
	@Test
	@WithAnonymousUser
	void testGetCurrentUserDetails_withUserNotFound() {
		
		// save 
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
		
		//perform the call 
		BlogAPIException resourceNotFoundException = assertThrows(BlogAPIException.class,
						() -> authServiceImpl.getCurrentUserDetails());
				
		// verify
		assertEquals("User does not exist!.", resourceNotFoundException.getMessage());
		
	}

}
