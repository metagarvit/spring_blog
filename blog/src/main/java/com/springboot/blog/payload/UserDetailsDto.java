/**
 * 
 */
package com.springboot.blog.payload;

import java.util.Set;

import com.springboot.blog.entity.Role;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Garvit
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsDto {
	
	private String name ;
	private String username ;
	private String email;
	
	private String profileImage;
	private Set<Role> roles;

}
