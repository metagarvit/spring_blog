package com.springboot.blog.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.config.SecurityConfig;
import com.springboot.blog.controller.CategoryController;
import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.CustomUserDetailsService;
import com.springboot.blog.security.CustomUserDetailsService;
import com.springboot.blog.security.JwtAuthenticationEntryPoint;
import com.springboot.blog.security.JwtAuthenticationFilter;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.CategoryService;
import com.springboot.blog.service.impl.AuthServiceImpl;
import com.springboot.blog.service.impl.PostServiceImpl;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockitoSettings(strictness = Strictness.WARN)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
//@WebMvcTest(CategoryController.class , excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class CategoryControllerTest {

	MockMvc mockMvc;

//
//	@Mock
//	private UserDetailsService userDetailsService;
//	@Mock
//	private JwtTokenProvider jwtTokenUtil;
//	@Mock
//	private AuthenticationManager authenticationManager;
//	
	
	
	@Mock
	private CategoryRepository categoryRepository;
	
	@MockBean
	private UserRepository userRepository;

	@Mock
	private CategoryService categoryService;


	@InjectMocks
	private CategoryController categoryController;

	CategoryDto categoryDto = CategoryDto.builder().id(1L).name("Testing").description("Testing Desc").build();
	Category category = Category.builder().id(1L).name("Testing").description("Testing Desc").build();

	@BeforeAll
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(categoryController) .build();
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get all cateogry
	 */
	@DisplayName("Test Mock category controller getAllCategory method + categoryService")
	@Test
	void testGetAllCategory() throws Exception {
		
		// save all post
		when(categoryService.getAllCategories()).thenReturn(List.of(categoryDto));
		
		
		// perform the call
		 mockMvc.perform(MockMvcRequestBuilders
		            .get("/api/categories")
		            .contentType(MediaType.APPLICATION_JSON))
		            .andExpect(status().isOk())
		            .andExpect(jsonPath("$.length()").value(1))
		            .andExpect(jsonPath("$[0].name", is("Testing")))
		            .andExpect(jsonPath("$[0].id", is(1)));
	}

	/**
	 * Get category by id 
	 */
	@DisplayName("Test Mock category controller getCategory method + categoryService")
	@Test
	void testGetCategory() throws Exception {
		
		// save all post
		when(categoryService.getCategory(anyLong())).thenReturn(categoryDto);
		
		
		// perform the call
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/categories/1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name", is("Testing")))
		.andExpect(jsonPath("$.id", is(1)));
	}

	/**
	 * Get all post 
	 * @throws Exception 
	 */
	@DisplayName("Test Mock category controller addCategory method + categoryService")
	@Test
	@WithMockUser(roles = "USER")
	void testAddCategory() throws Exception {
		
		// save all post
		when(categoryService.addCategory(any(CategoryDto.class))).thenReturn(categoryDto);
		
		
		// perform the call
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/categories" )
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(categoryDto)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.name", is("Testing")))
		.andExpect(jsonPath("$.id", is(1)));
	}
	
	

	/**
	 * 
	 * Add Category
	 * TODO : PENDING
	 * @throws Exception 
	 * 
	 */
	@DisplayName("Test Mock category controller addCategory method + categoryService")
	@Test()
//	@org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested
	@WithMockUser(username = "garvit" , roles={"ADMIN"} , password = "12345678" )
	void testAddCategory_withUerRole() throws Exception {
//		String tokenAdmin = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJHYXJ2aXQiLCJpYXQiOjE2Nzc3NTMwODYsImV4cCI6MTY3ODM1Nzg4Nn0.dqDjUDOxItAD_sjBVBWp7-znS8djdrsY7vJwk_cPWdHWZ8rC_E-GYWyfvwhqsJz7";
//		// save all post
//		when(categoryService.addCategory(any(CategoryDto.class))).thenReturn(categoryDto);
//		
//		Role role = Role.builder().id(1L).name("ROLE_USER").build();
//		Set<Role> roles = new HashSet<>();
//		roles.add(role);
//		var passwordEncoder = new BCryptPasswordEncoder();
//	var	user = User.builder().id(1L).email("user@gmail.com").name("user").username("user").profileImage("User profile")
//				.password(passwordEncoder.encode("user")).roles(roles).build();
//	when(userRepository.findByUsernameOrEmail(anyString() , anyString())).thenReturn(Optional.of(user));
//	        String username = "user";
//	        String password = "password";
//	        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, password, Collections.emptyList());
//	        Authentication authentication = new UsernamePasswordAuthenticationToken(
//	                new org.springframework.security.core.userdetails.User("user", "password", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))),
//	                null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
//	        String token = "jwt token";
//	      
//	        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
//	        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
//	        when(jwtTokenUtil.generateToken(any(Authentication.class))).thenReturn(token);
//	        SecurityContextHolder.getContext().setAuthentication(authentication);
		
		// perform the call
//		mockMvc.perform(MockMvcRequestBuilders
//				.post("/api/categories" ).with(user("user").roles("USER"))
//				.contentType(MediaType.APPLICATION_JSON)
//				.accept(MediaType.APPLICATION_JSON)
//			
//				.content(asJsonString(categoryDto)))
//		.andDo(MockMvcResultHandlers.print())
//		.andExpect(status().isCreated());
		
//		RestAssured.given().port(8123)
////		.header("Authorization", "Bearer "+ tokenAdmin)
//		.contentType(ContentType.JSON).accept(ContentType.JSON)
//		.body(categoryDto)
//		.post("/api/categories")
//		.then().statusCode(401);
		
		
//		post("/api/categories")
//		.accept(MediaType.APPLICATION_JSON)
//		.content(asJsonString(categoryDto)).
	}
	
	

	/**
	 * Update Category
	 * @throws Exception 
	 */
	@DisplayName("Test Mock category controller updateCategory method + categoryService")
	@Test
	@WithMockUser(roles = "ADMIN")
	void testUpdateCategory() throws Exception {
		
		// save all post
		when(categoryService.updateCategory(any(CategoryDto.class) , anyLong())).thenReturn(categoryDto);
		
		
		// perform the call
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/categories/1" )
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(categoryDto)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name", is("Testing")))
		.andExpect(jsonPath("$.id", is(1)));
	}
	
	/**
	 * Delete Category
	 * @throws Exception 
	 */
	@DisplayName("Test Mock category controller deleteCategory method + categoryService	")
	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteCategory() throws Exception {
		
		
		// perform the call
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/categories/1" )
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", is("Deleted Successfully")));
	}
	

}
