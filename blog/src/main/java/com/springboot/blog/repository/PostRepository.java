package com.springboot.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.SelfPostDto;

/**
 * Repository Interface
 * @author Garvit
 *
 */


public interface PostRepository extends JpaRepository<Post, Long> {
	
	List<Post> findByCategoryId(Long categoryId);	
	
	List<Post> findByCreatedBy(String username);

}
