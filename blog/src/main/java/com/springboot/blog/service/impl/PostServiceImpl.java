package com.springboot.blog.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.util.buf.UDecoder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.exception.UnauthorizedUserException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostPaginationResponse;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.payload.SelfPostDto;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.service.PostService;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ModelMapper mapper;

/**
 * Try to pass modelMapper in constructor
 */
	public PostServiceImpl() {
		mapper = new ModelMapper();
	}

	@Override
	public PostDto createPost(PostDto postDto) {

		Category category = categoryRepository.findById(postDto.getCategoryId()).orElseThrow(
				() -> new ResourceNotFoundException("Category", "id", String.valueOf(postDto.getCategoryId())));

		// getting current user
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", String.valueOf(username)));
		;

		// convert Dto to entity
		Post post = mapToEntity(postDto);
		post.setUserProfile(user.getProfileImage());
		post.setCategory(category);
		Post newPost = postRepository.save(post);

		// convert entity to dto
		PostDto postResponse = mapToDto(newPost);

		return postResponse;
	}

	@Override
	public List<PostDto> getAllPost() {
		List<Post> posts = postRepository.findAll();
		return posts.stream().map(post -> mapToDto(post)).collect(Collectors.toList());
	}

	/**
	 * Convert entity to dto
	 * 
	 * @param post
	 * @return
	 */
	private PostDto mapToDto(Post post) {
		mapper = new ModelMapper();
		PostDto postDto = mapper.map(post, PostDto.class);
		return postDto;

	}

	/**
	 * Convert entity to self post dto
	 * 
	 * @param post
	 * @return
	 */
	private SelfPostDto mapToSelfDto(Post post) {
		mapper = new ModelMapper();
		SelfPostDto postDto = mapper.map(post, SelfPostDto.class);
		return postDto;

	}

	/**
	 * Convert entity to dto
	 * 
	 * @param post
	 * @return
	 */
	private PostPaginationResponse mapToPageDto(Post post) {

		PostPaginationResponse postDto = new PostPaginationResponse();
		postDto.setId(post.getId());
		postDto.setImage(post.getImage());
		postDto.setUserProfile(post.getUserProfile());
		postDto.setTitle(post.getTitle());
		postDto.setDescription(post.getDescription());
		postDto.setCreateDate(post.getCreateDate());
		postDto.setLastModifiedBy(post.getLastModifiedBy());
		postDto.setLastModifiedDate(post.getLastModifiedDate());
		return postDto;

	}

	/**
	 * Convert Dto to entity
	 * 
	 * @param postDto
	 * @return
	 */
	private Post mapToEntity(PostDto postDto) {
		Post post = mapper.map(postDto, Post.class);
		post = Post.builder().id(postDto.getId()).title(postDto.getTitle()).description(postDto.getDescription())
				.image(postDto.getImage()).build();
		return post;

	}

	@Override
	public PostDto getPostById(long id) {
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));
		return mapToDto(post);
	}

	@Override
	public PostDto updatePostById(PostDto postDto, long id) {

		// getting current user
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// get post by id from the db
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));
//		User user = userRepository.findByUsername(username);
		if (!username.equals(post.getCreatedBy())) {
			throw new UnauthorizedUserException(HttpStatus.UNAUTHORIZED, "Invalid Acess");
		}
		Category category = categoryRepository.findById(postDto.getCategoryId()).orElseThrow(
				() -> new ResourceNotFoundException("Category", "id", String.valueOf(postDto.getCategoryId())));

		post.setTitle(postDto.getTitle());
		post.setDescription(postDto.getDescription());
		post.setImage(postDto.getImage());
		post.setCategory(category);
		// Updating post
		Post updatePost = postRepository.save(post);
		return mapToDto(updatePost);
	}

	@Override
	public void deletePostById(long id) {
		// getting current user
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// get post by id from the db
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));

		if (!username.equals(post.getCreatedBy())) {
			throw new UnauthorizedUserException(HttpStatus.UNAUTHORIZED, "Invalid Acess");
		}

		postRepository.delete(post);

		return;
	}

	@Override
	public PostResponse getAllPost(int pageNo, int pageSize, String sortBy, String sortDir) {

		// Create sort object
//		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
//				: Sort.by(sortBy).descending();

		// Create pageable instance
//		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		// Sort by descending
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());

		Page<Post> posts = postRepository.findAll(pageable);

		// get content for page object
		List<Post> listOfPost = posts.getContent();

		List<PostPaginationResponse> content = listOfPost.stream().map(post -> mapToPageDto(post))
				.collect(Collectors.toList());

		PostResponse postResponse = new PostResponse();
		postResponse.setContent(content);
		postResponse.setPageNo(posts.getNumber());
		postResponse.setPageSize(posts.getSize());
		postResponse.setTotalElements(posts.getTotalElements());
		postResponse.setTotalPages(posts.getTotalPages());
		postResponse.setLast(posts.isLast());

		return postResponse;

	}

	@Override
	public List<PostDto> getPostByCategoryId(long categoryId) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "id", String.valueOf(categoryId)));

		List<Post> posts = postRepository.findByCategoryId(categoryId);

		return posts.stream().map(post -> mapToDto(post)).collect(Collectors.toList());

	}

	@Override
	public List<SelfPostDto> getSelfPost() {
		// getting current user
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		List<Post> posts = postRepository.findByCreatedBy(username);

		return posts.stream().map(post -> mapToSelfDto(post)).collect(Collectors.toList());

	}

	@Override
	public void deletePostByIdByAdmin(long id) {
		// get post by id from the db
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));

		postRepository.delete(post);

	}

}
