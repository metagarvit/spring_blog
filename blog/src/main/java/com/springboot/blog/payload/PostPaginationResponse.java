package com.springboot.blog.payload;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPaginationResponse {

	private Long id;

	//title should have at least 2 character and not null
	@NotEmpty
	@Size(min = 2 , message = "Title length have at least 2 character ")
	private String title;
	
	//desc should have at least 10 character and not null
	@NotEmpty
	@Size( min =2 , message = "Post description should have at least 10 character")
	private String description;
	
	@NotEmpty
	private String image;
	
	
	
	

	
	
	private String userProfile;
	
	
	private Long categoryId;
	

	
	protected Date createDate ; 
	
	protected String lastModifiedBy ; 
	
	protected Date lastModifiedDate ; 
	
}
