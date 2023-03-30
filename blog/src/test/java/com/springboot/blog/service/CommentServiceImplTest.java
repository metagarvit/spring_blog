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

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
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
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.CategoryRepository;
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
public class CommentServiceImplTest {

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
	private CommentRepository commentRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks // auto inject helloRepository
	private PostServiceImpl postService;

	@InjectMocks // auto inject helloRepository
	private CommentServiceImpl commentService;

	@InjectMocks // auto inject helloRepository
	private AuthServiceImpl authServiceImpl;

	Post post;

	Comment comment;

	@BeforeEach
	void setMock() {

		// create Comment object
		comment = Comment.builder().id(1).body("Comment body").userProfileImage("User profile").build();

		Role role = Role.builder().id(1L).name("ROLE_USER").build();
		Set<Role> roles = new HashSet<>();
		roles.add(role);

		// save roles
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

		// save user
		when(userRepository.findByUsername("user")).thenReturn(Optional.of(User.builder().id(1L).email("user@gmail.com")
				.name("user").username("user").profileImage("User profile").password("user").roles(roles).build()));

		// save comment to comments list
		Set<Comment> comments = new HashSet<>();
		comments.add(comment);

		// create Category object
		Category category = new Category();
		category.setId(1L);
		category.setName("name");
		// create Post object
		post = new Post(1L, "Post Title", "Post Desc", "Imagelink ", "User profile", comments, category);
		post.setCreatedBy("user");
		Optional<Post> optionalPost = Optional.of(post);
		post.setId(1L);

		// save post
		when(postRepository.findById(1L)).thenReturn(optionalPost);

	}

	/**
	 * Map Comment to CommentDto
	 * 
	 * @param comment
	 * @return
	 */
	private CommentDto mapToDto(Comment comment) {
		modelMapper = new ModelMapper();
		CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
		return commentDto;
	}

	/**
	 * Get comments list using post id
	 */
	@DisplayName("Test Mock commentService getCommentByPostId method + commentRepository")
	@Test
	@WithMockUser
	void testGetCommentByPostId() {
		
		// save
		when(commentRepository.findByPostId(1L)).thenReturn(List.of(comment));
		
		// perform the call
		List<CommentDto> commentDtos = commentService.getCommentByPostId(1L);
		// verify by size
		assertEquals(1, commentDtos.size());
		//verify comment id
		assertEquals(1, commentDtos.get(0).getId());
	}

	/**
	 * Get comment using post id and comment id
	 */
	@DisplayName("Test Mock commentService getCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testGetCommentByPostIdAndCommentId() {
		comment.setPost(post);
		// save
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

		// perform the call
		CommentDto commentDto = commentService.getCommentById(1L, 1L);
		// verify by id
		assertEquals(mapToDto(comment).getId(), commentDto.getId());
		// verify by body
		assertEquals(mapToDto(comment).getBody(), commentDto.getBody());

	}

	/**
	 * Get comment using post id and comment id
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock commentService getCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testGetCommentByPostIdAndCommentId_withPostNotFound() {
		comment.setPost(post);
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
						() -> commentService.getCommentById(2L , 1L));
		
		// verify
		assertEquals("Post not found with id : 2", resourceNotFoundException.getMessage());

		
	}
	
	/**
	 * Get comment using post id and comment id
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock commentService getCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testGetCommentByPostIdAndCommentId_withCommentIdNotFound() {
		comment.setPost(post);
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> commentService.getCommentById(1L , 2L));
		
		// verify
		assertEquals("Comment not found with id : 2", resourceNotFoundException.getMessage());
		
	}
	/**
	 * Get comment using post id and comment id
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock commentService getCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testGetCommentByPostIdAndCommentId_withCommentNotBelongToPost() {
	
		comment.setPost(new Post(2L, "Post Title", "Post Desc", "Imagelink ", "User profile", null, null));
		
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		
		// perform the call
		BlogAPIException blogAPIException = assertThrows(BlogAPIException.class,
				() -> commentService.getCommentById(1L , 1L));
		
		// verify
		assertEquals("Comment does not belong to post", blogAPIException.getMessage());
	}
	
	/**
	 * Create comment using post id 
	 */
	@DisplayName("Test Mock commentService createComment method + commentRepository")
	@Test
	@WithMockUser
	void testCreateComment() {
		
		
		// save 
		when(commentRepository.save(any(Comment.class))).thenReturn(comment);
		
		CommentDto commentDto = commentService.createComment(1L, mapToDto(comment));
		//perform the call and verify
		assertEquals(comment.getId(), commentDto.getId());
		assertEquals(comment.getBody(), commentDto.getBody());
	}
	
	
	
	
	
	/**
	 * Create comment using post id 
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock commentService createComment method + commentRepository")
	@Test
	@WithMockUser
	void testCreateComment_withPostNotFound() {
		comment.setPost(post);
		// save 
		when(commentRepository.save(any(Comment.class))).thenReturn(comment);
				
		
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
						() -> commentService.createComment(2L, mapToDto(comment)));
		
		// verify
		assertEquals("Post not found with id : 2", resourceNotFoundException.getMessage());

		
	}
	
	/**
	 * Create comment using post id 
	 */
	@DisplayName("Test Mock commentService createComment method + commentRepository")
	@Test
	@WithAnonymousUser
	void testCreateComment_withUserNotFound() {

		// save 
		when(commentRepository.save(any(Comment.class))).thenReturn(comment);
	
		
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
								() -> commentService.createComment(1L, mapToDto(comment)));
				
				// verify
				assertEquals("User not found with id : anonymous", resourceNotFoundException.getMessage());

				
	}
	
	
	
	/**
	 * Update comment using post id and comment id
	 */
	@DisplayName("Test Mock commentService updateCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testUpdateCommentById() {
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		comment.setPost(new Post(1L, "Post Title", "Post Desc", "Imagelink ", "User profile", null, null));
		// save 
		when(commentRepository.save(any(Comment.class))).thenReturn(comment);
		
		CommentDto commentDto = commentService.updateCommentById(1L,1L,  mapToDto(comment));
		//perform the call and verify
		assertEquals(comment.getId(), commentDto.getId());
		assertEquals(comment.getBody(), commentDto.getBody());
	}
	
	
	/**
	 * Update comment using post id and comment id
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock commentService getCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testUpdateCommentById_withPostNotFound() {
		comment.setPost(post);
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
						() -> commentService.updateCommentById(2L,1L,  mapToDto(comment)));
		
		// verify
		assertEquals("Post not found with id : 2", resourceNotFoundException.getMessage());

		
	}
	
	/**
	 * Update comment using post id and comment id
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock commentService getCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testUpdateCommentById_withCommentIdNotFound() {
		comment.setPost(post);
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> commentService.updateCommentById(1L,2L,  mapToDto(comment)));
		
		// verify
		assertEquals("Comment not found with id : 2", resourceNotFoundException.getMessage());
		
		
	}
	/**
	 * Update comment using post id and comment id
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock commentService getCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testUpdateCommentById_withCommentNotBelongToPost() {
	
		comment.setPost(new Post(2L, "Post Title", "Post Desc", "Imagelink ", "User profile", null, null));
		
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		
		// perform the call
		BlogAPIException blogAPIException = assertThrows(BlogAPIException.class,
				() -> commentService.updateCommentById(1L,1L,  mapToDto(comment)));
		
		// verify
		assertEquals("Comment does not belong to post", blogAPIException.getMessage());
	}
	
	
	/**
	 * Delete comment by post id and comment id
	 */
	@DisplayName("Test Mock commentService deleteCommentById method + commentService")
	@Test
	@WithMockUser
	void testDeleteCommentById() {
		comment.setPost(post);
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
				
		
		// perform the call
		commentService.deleteCommentById(1L ,1L);

		// verify the mocks
		verify(commentRepository).delete(comment);

	}
	
	
	
	/**
	 * Delete comment using post id and comment id
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock commentService getCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testDeleteCommentById_withPostNotFound() {
		comment.setPost(post);
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
						() -> commentService.deleteCommentById(2L ,1L));
		
		// verify
		assertEquals("Post not found with id : 2", resourceNotFoundException.getMessage());

		
	}
	
	/**
	 * Delete comment using post id and comment id
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock commentService getCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testDeleteCommentById_withCommentIdNotFound() {
		comment.setPost(post);
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> commentService.deleteCommentById(1L ,2L));
		
		// verify
		assertEquals("Comment not found with id : 2", resourceNotFoundException.getMessage());
		
		
	}
	/**
	 * Delete comment using post id and comment id
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock commentService getCommentById method + commentRepository")
	@Test
	@WithMockUser
	void testDeleteCommentById_withCommentNotBelongToPost() {
	
		comment.setPost(new Post(2L, "Post Title", "Post Desc", "Imagelink ", "User profile", null, null));
		
		// save 
		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		
		// perform the call
		BlogAPIException blogAPIException = assertThrows(BlogAPIException.class,
				() -> commentService.deleteCommentById(1L ,1L));
		
		// verify
		assertEquals("Comment does not belong to post", blogAPIException.getMessage());
	}
	

}
