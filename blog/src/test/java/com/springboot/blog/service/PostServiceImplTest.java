package com.springboot.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.exception.UnauthorizedUserException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.payload.SelfPostDto;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.impl.AuthServiceImpl;
import com.springboot.blog.service.impl.PostServiceImpl;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@MockitoSettings(strictness = Strictness.WARN)

public class PostServiceImplTest {

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private PostRepository postRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@MockBean
	private RoleRepository roleRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks // auto inject Repository
	private PostServiceImpl postService;

	@InjectMocks // auto inject Repository
	private AuthServiceImpl authServiceImpl;

	Post post;

	@BeforeEach
	void setMockOutput() {

		Role role = Role.builder().id(1L).name("ROLE_USER").build();
		Set<Role> roles = new HashSet<>();
		roles.add(role);

		// save roles
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

		// save user
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(User.builder().id(1L).email("user@gmail.com")
				.name("user").username("user").profileImage("user").password("user").roles(roles).build()));

//		authServiceImpl.login(LoginDto.builder().password("user").usernameOrEmail("user").build());

		Set<Comment> comments = new HashSet<>();
		Category category = new Category();
		category.setId(1L);
		category.setName("name");
		post = new Post(1L, "Post Title", "Post Desc", "Imagelink ", "User profile", comments, category);
		post.setCreatedBy("user");
		Optional<Post> optionalPost = Optional.of(post);
		post.setId(2L);
		category.setPosts(List.of(post));
		post.setId(1L);

		// save category
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

		// save post
		when(postRepository.findById(1L)).thenReturn(optionalPost);

		// save post
		when(postRepository.save(any(Post.class))).thenReturn(post);

	}

	/**
	 * Get all post 
	 */
	@DisplayName("Test Mock postService getAllPost method + postRepository")
	@Test
	void testGetAllPost() {
		
		// save all post
		when(postRepository.findAll()).thenReturn(List.of(post));
		
		
		// perform the call
		List<PostDto> getPostDtoList = postService.getAllPost();
		// verify
		assertEquals(List.of(mapToDto(post)), getPostDtoList);
	}

	/**
	 * Get post by id
	 */
	@DisplayName("Test Mock postService getPostById method + postRepository")
	@Test
	void testGetPostById() {
		// perform the call
		PostDto getPostDto = postService.getPostById(1L);
		// verify
		assertEquals(mapToDto(post), getPostDto);
	}

	/**
	 * Get post by category id
	 */
	@DisplayName("Test Mock postService getPostByCategoryId method + postRepository")
	@Test
	void testGetPostByCategoryId() {
		// save all post
		when(postRepository.findByCategoryId(1L)).thenReturn(List.of(post));
		
		
		// perform the call
		List<PostDto> getPostDtoList = postService.getPostByCategoryId(1L);
		// verify
		assertEquals(List.of(mapToDto(post)), getPostDtoList);
	}

	/**
	 * Get post by category id with Category id not found
	 */
	@DisplayName("Test Mock postService getPostByCategoryId method + postRepository")
	@Test
	void testGetPostByCategoryId_withCategoryIdNotFound() {

		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> postService.getPostByCategoryId(2L));
		// verify
		assertEquals("Category not found with id : 2", resourceNotFoundException.getMessage());

	}

	/**
	 * Get post by category id
	 */
	@DisplayName("Test Mock postService getAllPost method + postRepository")
	@Test
	void testGetAllPostByPagination() {
		// save all post
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
		Page<Post> postsList = new PageImpl(List.of(post));
		when(postRepository.findAll(pageable)).thenReturn(postsList);

		// perform the call
		PostResponse postResponse = postService.getAllPost(0, 10, "id", "asc");
		// verify page size
		assertEquals(1, postResponse.getPageSize());
		// verify post id
		assertEquals(1, postResponse.getContent().get(0).getId());
	}

	/**
	 * Get self created post
	 */
	@DisplayName("Test Mock postService getSelfPost method + postRepository")
	@Test
	@WithMockUser
	void testGetSelfPost() {
		
		//when
		when(postRepository.findByCreatedBy("user")).thenReturn(List.of(post));
		
		// perform the call
		List<SelfPostDto> getPostDtoList = postService.getSelfPost();
	
		//verify
		assertEquals(1, getPostDtoList.size());
		assertEquals(1L, getPostDtoList.get(0).getId());
	}

	/**
	 * Get post by id throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock postService getPostById method + postRepository")
	@Test
	void testGetPostById_thenThrowResourceNotFoundException() {
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> postService.getPostById(2L));
		// verify
		assertEquals("Post not found with id : 2", resourceNotFoundException.getMessage());

	}

	/**
	 * Used to convert Post model to Postdto model
	 * 
	 * @param post
	 * @return
	 */
	private PostDto mapToDto(Post post) {
		modelMapper = new ModelMapper();
		PostDto postDto = modelMapper.map(post, PostDto.class);
		return postDto;

	}

	/**
	 * Create post
	 */
	@DisplayName("Test Mock postService createPost method + postRepository")
	@Test
	@WithMockUser
	void testCreatePost() {
		PostDto postDto = PostDto.builder().id(1L).title("Post Title").image("Imagelink ").userProfile("User profile")
				.categoryId(1L).build();

		// perform the call and verify
		assertEquals(mapToDto(post).getId(), postService.createPost(postDto).getId());
	}

	/**
	 * Create post
	 */
	@DisplayName("Test Mock postService createPost method + postRepository")
	@Test
	@WithMockUser
	void testCreatePost_with() {
		PostDto postDto = PostDto.builder().id(1L).title("Post Title").image("Imagelink ").userProfile("User profile")
				.categoryId(1L).build();

		// perform the call and verify
		assertEquals(mapToDto(post).getId(), postService.createPost(postDto).getId());
	}

	/**
	 * Create post
	 */
	@DisplayName("Test Mock postService createPost method + postRepository")
	@Test
	@WithMockUser
	void testCreatePost_withCategoryIDNotFound() {
		PostDto postDto = PostDto.builder().id(1L).title("Post Title").image("Imagelink ").userProfile("User profile")
				.categoryId(2L).build();
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> postService.createPost(postDto).getId());
		// verify
		assertEquals("Category not found with id : 2", resourceNotFoundException.getMessage());

	}

	/**
	 * If user is not present then create post service will throw exception throw
	 * Null pointer exception
	 */
	@DisplayName("Test Mock postService createPost method + postRepository")
	@Test
	@WithAnonymousUser
	void testCreatePost_withUserNotFound() {
		PostDto postDto = PostDto.builder().id(1L).title("Post Title").image("Imagelink ").userProfile("User profile")
				.categoryId(1L).build();

		// perform the call and verify
		assertThrows(Exception.class, () -> postService.createPost(postDto));

	}

	/**
	 * Update post details
	 */
	@DisplayName("Test Mock postService updatePostById method + postRepository")
	@Test
	@WithMockUser
	void testUpdatePost() {

		PostDto postDto = PostDto.builder().id(1L).title("Post Title").image("Imagelink ").userProfile("User profile")
				.categoryId(1L).build();

		// perform the call and verify
		assertEquals(mapToDto(post).getId(), postService.updatePostById(postDto, 1L).getId());
		assertEquals(mapToDto(post).getDescription(), postService.updatePostById(postDto, 1L).getDescription());

	}

	/**
	 * Update post details
	 */
	@DisplayName("Test Mock postService updatePostById method + postRepository")
	@Test
	@WithMockUser
	void testUpdatePost_withPostNotFound() {
		PostDto postDto = PostDto.builder().id(10L).title("Update Post Title").image("Updated Imagelink ")
				.userProfile("User profile").categoryId(10L).build();

		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> postService.updatePostById(postDto, 10L));

		// verify
		assertEquals("Post not found with id : 10", resourceNotFoundException.getMessage());

	}

	/**
	 * Update post details
	 */
	@DisplayName("Test Mock postService updatePostById method + postRepository")
	@Test
	@WithMockUser
	void testUpdatePost_withInvalidAccess() {

		post.setCreatedBy("testing");
		Optional<Post> optionalPost = Optional.of(post);
		// save post
		when(postRepository.findById(2L)).thenReturn(optionalPost);

		PostDto postDto = PostDto.builder().id(2L).title("Update Post Title").image("Updated Imagelink ")
				.userProfile("User profile").createdBy("testing").categoryId(10L).build();

		// perform the call
		UnauthorizedUserException resourceNotFoundException = assertThrows(UnauthorizedUserException.class,
				() -> postService.updatePostById(postDto, 2L));

		// verify
		assertEquals("Invalid Acess", resourceNotFoundException.getMessage());

	}

	/**
	 * Update post details
	 */
	@DisplayName("Test Mock postService updatePostById method + postRepository")
	@Test
	@WithMockUser
	void testUpdatePost_withCategoryIdNotFound() {

		PostDto postDto = PostDto.builder().id(1L).title("Update Post Title").image("Updated Imagelink ")
				.userProfile("User profile").categoryId(10L).build();

		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> postService.updatePostById(postDto, 1L));

		// verify
		assertEquals("Category not found with id : 10", resourceNotFoundException.getMessage());

	}

	/**
	 * If user is not present then post service will throw exception throw. Null
	 * pointer exception
	 */
	@DisplayName("Test Mock postService updatePostById method + postRepository")
	@Test
	void testUpdatePost_withUserNotFound() {
		PostDto postDto = PostDto.builder().id(1L).title("Update Post Title").image("Updated Imagelink ")
				.userProfile("User profile").categoryId(1L).build();

		// verify
		assertThrows(Exception.class, () -> postService.updatePostById(postDto, 1L));

	}

	/**
	 * Delete post by id
	 */
	@DisplayName("Test Mock postService deletePostById method + postRepository")
	@Test
	@WithMockUser
	void testDeletePost() {
		// perform the call
		postService.deletePostById(1);

		// verify the mocks
		verify(postRepository).delete(post);

	}

	/**
	 * Delete post by id
	 */
	@DisplayName("Test Mock postService deletePostById method + postRepository")
	@Test
	@WithAnonymousUser
	void testDeletePost_withUserNotFound() {
		// perform the call
		UnauthorizedUserException resourceNotFoundException = assertThrows(UnauthorizedUserException.class,
				() -> postService.deletePostById(1));

		// verify
		assertEquals("Invalid Acess", resourceNotFoundException.getMessage());

	}

	/**
	 * Delete post by id by admin
	 */
	@DisplayName("Test Mock postService deletePostByIdByAdmin method + postRepository")
	@Test
	@WithMockUser
	void testDeletePostByIdByAdmin() {

		// perform the call
		postService.deletePostByIdByAdmin(1);

		// verify the mocks
		verify(postRepository).delete(post);

	}

	/**
	 * If user is not present then delete post service will throw exception throw
	 */
	@DisplayName("Test Mock postService deletePostById method + postRepository")
	@Test
	@WithMockUser
	void testDeletePost_withPostIdNotFound() {
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> postService.deletePostById(2));

		// verify
		assertEquals("Post not found with id : 2", resourceNotFoundException.getMessage());

	}

	/**
	 * If user is not present then delete post service will throw exception throw
	 */
	@DisplayName("Test Mock postService deletePostById method + postRepository")
	@Test
	@WithMockUser
	void testDeletePostByIdByAdmin_withPostIdNotFound() {

		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> postService.deletePostByIdByAdmin(2));

		// verify
		assertEquals("Post not found with id : 2", resourceNotFoundException.getMessage());

	}

}
