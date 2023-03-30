package com.springboot.blog.payload;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class PostDto {

	private Long id;

	// title should have at least 2 character and not null
	@NotEmpty
	@Size(min = 2, message = "Title length have at least 2 character ")
	private String title;

	// desc should have at least 10 character and not null
	@NotEmpty
	@Size(min = 2, message = "Post description should have at least 10 character")
	private String description;

	@NotEmpty
	private String image;


	private Set<CommentDto> comments;

	private Long categoryId;

	private String createdBy ; 
	
	private Date createDate ; 
	
	private String lastModifiedBy ; 
	
	private String lastModifiedDate ; 
	

	
	private String userProfile;
	
}
