package com.springboot.blog.service;

import java.util.List;

import com.springboot.blog.payload.CommentDto;

public interface CommentService {
	
	CommentDto createComment(long postId  , CommentDto commentRequest);
	
	
	CommentDto updateCommentById(long postId  ,long commentId, CommentDto commentRequest);
	
	void deleteCommentById(long postId  ,long commentId);
	
	List<CommentDto> getCommentByPostId (long postId);
	
	CommentDto getCommentById (long postId , long commentId);

}
