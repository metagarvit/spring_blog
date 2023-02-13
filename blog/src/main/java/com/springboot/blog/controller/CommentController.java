package com.springboot.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.service.CommentService;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api")
public class CommentController {

	@Autowired
	private CommentService commentService;

	@PostMapping("/posts/{postId}/comments")
	public ResponseEntity<CommentDto> createComment( @PathVariable("postId") long postId,
			@Valid @RequestBody CommentDto commentDto) {
		return new ResponseEntity<>(commentService.createComment(postId , commentDto), HttpStatus.CREATED);

	}
	
	/**
	 * Get comment list by post id
	 * @param postId
	 * @return
	 */
	@GetMapping("/posts/{postId}/comments")
	public List<CommentDto> getCommentByPostId(@PathVariable("postId") long postId)
	{
	
		return commentService.getCommentByPostId(postId);
	}
	
	
	@GetMapping("/posts/{postId}/{comments}/{commentId}")
	public ResponseEntity<CommentDto> getCommentById(@PathVariable(value = "postId" ) long postId ,@PathVariable(value = "commentId") long commentId )
	{
		CommentDto commentDto = commentService.getCommentById(postId, commentId);
		return  ResponseEntity.ok(commentDto);
	}
	
	@PutMapping("/posts/{postId}/comments/{id}")
	public ResponseEntity<CommentDto> updateComment(@PathVariable("postId") long postId , 
			@PathVariable("id") long commentId,
			@Valid @RequestBody CommentDto commentDto)
	{
		CommentDto upCommentDto = commentService.updateCommentById(postId, commentId, commentDto);
		return ResponseEntity.ok(upCommentDto);
	}
	
	@DeleteMapping("/posts/{postId}/comments/{id}")
	public ResponseEntity<String> deleteCommentById(@PathVariable("postId") long postId , 
			@PathVariable("id") long commentId)
	{
		commentService.deleteCommentById(postId, commentId);
		return ResponseEntity.ok("Successfully Deleted");
	}
}
