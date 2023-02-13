/**
 * 
 */
package com.springboot.blog.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Garvit
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PostResponse {
	
	private List<PostPaginationResponse> content;
	private int pageNo;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean last;

}
