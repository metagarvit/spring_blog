package com.springboot.blog.service.impl;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private ModelMapper mapper;

	@Override
	public CommentDto createComment(long postId, CommentDto commentDto) {

		// getting current user
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", String.valueOf(username)));

		Comment comment = mapToEntity(commentDto);

		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(postId)));

		// set post to comment entity
		comment.setPost(post);
		comment.setUserProfileImage(user.getProfileImage());
		// save comment entity to db

		Comment newComment = commentRepository.save(comment);

		return mapToDto(newComment);
	}

	/**
	 * Map Comment to CommentDto
	 * 
	 * @param comment
	 * @return
	 */
	private CommentDto mapToDto(Comment comment) {
		mapper  = new ModelMapper();
		CommentDto commentDto = mapper.map(comment, CommentDto.class);
		return commentDto;
	}

	/**
	 * Map CommentDto to Comment
	 * 
	 * @param commentRequest
	 * @return
	 */
	private Comment mapToEntity(CommentDto commentRequest) {
		mapper  = new ModelMapper();
		Comment comment = mapper.map(commentRequest, Comment.class);

		return comment;
	}

	/**
	 * Get comment list by post id
	 */
	@Override
	public List<CommentDto> getCommentByPostId(long postId) {

		// retrieve comments by postid
		List<Comment> comments = commentRepository.findByPostId(postId);

		// convert list of comment entities to list of comments dto's
		return comments.stream().map(comment -> mapToDto(comment)).collect(Collectors.toList());

	}

	@Override
	public CommentDto getCommentById(long postId, long commentId) {

		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(postId)));

		// retrive comment by id

		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));

		
		if (!comment.getPost().getId().equals(post.getId())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
		}

		return mapToDto(comment);
	}

	@Override
	public CommentDto updateCommentById(long postId, long commentId, CommentDto commentRequest) {

		//retrive post by id
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(postId)));

		// retrive comment by id

		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));

		if (!comment.getPost().getId().equals(post.getId())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
		}

		comment.setBody(commentRequest.getBody());

		Comment updatedComment = commentRepository.save(comment);

		return mapToDto(updatedComment);
	}

	@Override
	public void deleteCommentById(long postId, long commentId) {

		// retrive post by id
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(postId)));

		// retrive comment by id
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));

		if (!comment.getPost().getId().equals(post.getId())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
		}

		commentRepository.delete(comment);

	}

}
