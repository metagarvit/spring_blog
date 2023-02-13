package com.springboot.blog.payload;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Lob;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelfPostDto {

	private Long id;

	private String title;
	
	private String description;
	
	private String image;
	
	private Long categoryId;
	

	
	private String userProfile;
	
	protected Date createDate ; 
	
	protected String lastModifiedBy ; 
	
	protected Date lastModifiedDate ; 
	
}
