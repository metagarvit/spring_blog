package com.springboot.blog.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.payload.SelfPostDto;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.service.FileSytemStorageService;
import com.springboot.blog.service.PostService;
import com.springboot.blog.utils.AppConstants;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	@Autowired
	FileSytemStorageService fileSytemStorage;

	@Autowired
	private PostService postService;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Create blog post rest api
	 * 
	 * @param postDto
	 * @return
	 */
	@PostMapping
	public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto) {

		return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
	}

	/**
	 * Create blog post rest api
	 * 
	 * @param postDto
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/v2")
	public ResponseEntity<PostDto> createPostV2(@Valid @RequestParam("postData") String data,
			@RequestParam("file") MultipartFile file) throws IOException {
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
		PostDto postDto = mapper.readValue(data, PostDto.class);
		postDto.setImage(uri);

		return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
	}

	@PostMapping("/uploadfile")
	public ResponseEntity<String> uploadSingleFile(@RequestParam("file") MultipartFile file) {

		String upfile = fileSytemStorage.saveFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/images/")
				.path(upfile).toUriString();

		String uri = fileSytemStorage.getFilePath(upfile);
		return ResponseEntity.status(HttpStatus.OK).body(uri);

	}

	/**
	 * Get all posts rest api
	 * 
	 * @return
	 */
//	@GetMapping
//	public List<PostDto> getAllPost(){
//		return postService.getAlPost();
//	}

	/**
	 * Get posts by pagination rest api
	 * 
	 * @return
	 */
	@GetMapping
	public PostResponse getAllPost(
			@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pagesize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy

			,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
		return postService.getAllPost(pageNo, pagesize, sortBy, sortDir);
	}

	/**
	 * Get posts by pagination rest api
	 * 
	 * @return
	 */
	@GetMapping("/myblog")
	public List<SelfPostDto> getSelfPost() {
		return postService.getSelfPost();
	}

	/**
	 * Get posts by id rest api
	 * 
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<PostDto> getPostById(@PathVariable("id") Long id) {
		return ResponseEntity.ok(postService.getPostById(id));

	}

	/**
	 * Update post by id rest api
	 * 
	 * @param id
	 * @param postDto
	 * @return
	 */
	@PutMapping("/{id}")
	public ResponseEntity<PostDto> updatePost(@PathVariable("id") Long id, @Valid @RequestBody PostDto postDto) {
		return new ResponseEntity<>(postService.updatePostById(postDto, id), HttpStatus.OK);
	}

	/**
	 * Delete post by id rest api
	 * 
	 * @param id
	 * @param postDto
	 * @return
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePost(@PathVariable("id") Long id) {
		postService.deletePostById(id);
		return new ResponseEntity<>("Post entity deleted successfully", HttpStatus.OK);
	}

	/**
	 * Delete post by id rest api
	 * 
	 * @param id
	 * @param postDto
	 * @return
	 */
	@DeleteMapping("/admin/{id}")
	@PreAuthorize("hasRole('ADMIN')" )
	public ResponseEntity<String> deletePostByAdmin(@PathVariable("id") Long id )
	{
		postService.deletePostById(  id);
		return new ResponseEntity<>( "Post entity deleted successfully", HttpStatus.OK );
	}

	/**
	 * Get posts by id rest api
	 * 
	 * @return
	 */
	@GetMapping("/category/{categoryd}")
	public ResponseEntity<List<PostDto>> getPostByCategoryId(@PathVariable("categoryId") Long categoryd) {
		return ResponseEntity.ok(postService.getPostByCategoryId(categoryd));

	}

}
