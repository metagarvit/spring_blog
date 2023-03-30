/**
 * 
 */
package com.springboot.blog.payload;

import java.sql.Date;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Garvit
 *
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

	private long id;

	

	@NotEmpty(message = "Comment should not be empty")
	@Size(min = 2, message = "Comment body must be minimum 2 characters")
	private String body;
	
	
	private String createdBy ; 
	
	private Date createDate ; 
	
	private String lastModifiedBy ; 
	
	private String lastModifiedDate ; 
	

	
	private String userProfile;

}
