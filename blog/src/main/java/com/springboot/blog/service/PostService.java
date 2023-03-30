/**
 * 
 */
package com.springboot.blog.service;

import java.util.List;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostPaginationResponse;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.payload.SelfPostDto;

/**
 * @author Garvit
 *
 */
public interface PostService {
	/**
	 * Create Post 
	 * @param postDto
	 * @return
	 */
	PostDto createPost(PostDto postDto);
	
	/**
	 *  Update Post
	 * @param postDto
	 * @param id
	 * @return
	 */
	PostDto updatePostById(PostDto postDto , long id);
	
	
	/**
	 *  Delete Post
	 * @param id
	 * @return
	 */
	void deletePostById( long id);
	
	
	/**
	 *  Delete Post only by admin
	 * @param id
	 * @return
	 */
	void deletePostByIdByAdmin( long id);
	
	/**
	 * Get all post
	 * @return
	 */
	public List<PostDto> getAllPost();
	
	
	
	/**
	 * Get post according to pagination
	 * @param pageNo
	 * @param pageSize
	 * @param sortBy
	 * @param sortDir
	 * @return
	 */
	public PostResponse getAllPost(int pageNo , int pageSize,  String sortBy , String sortDir);
	
	
	/**
	 * Get self create post
	 * @return
	 */
	public List<SelfPostDto
	> getSelfPost();
	
	/**
	 * Get post by id
	 * @param id
	 * @return
	 */
	PostDto getPostById(long id);
	
	/**
	 * Get post by Category id
	 * @param id
	 * @return
	 */
	List<PostDto> getPostByCategoryId(long id);

}
