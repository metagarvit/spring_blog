package com.springboot.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.impl.AuthServiceImpl;
import com.springboot.blog.service.impl.CategoryServiceImpl;
import com.springboot.blog.service.impl.CommentServiceImpl;
import com.springboot.blog.service.impl.PostServiceImpl;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@MockitoSettings(strictness = Strictness.WARN)
public class CategoryServiceImplTest {

	@Spy
	private ModelMapper modelMapper;

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks // auto inject helloRepository
	private CategoryServiceImpl categoryService;

	CategoryDto categoryDto = CategoryDto.builder().id(1L).name("Testing").description("Testing Desc").build();
	Category category = Category.builder().id(1L).name("Testing").description("Testing Desc").build();

	/**
	 * Get all categories
	 */
	@DisplayName("Test Mock categoryService getAllCategories method + categoryRepository")
	@Test
	void testGetAllCategories() {
		
		// save
		when(categoryRepository.findAll()).thenReturn(List.of(category));
		
		// perform the call
		List<CategoryDto> categoryDtos = categoryService.getAllCategories();
		// verify by size
		assertEquals(1, categoryDtos.size());
		//verify comment id
		assertEquals(1, categoryDtos.get(0).getId());
	}

	/**
	 * Get categories by id
	 */
	@DisplayName("Test Mock categoryService getCategory method + categoryRepository")
	@Test
	void testGetCategory() {
		
		// save
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		
		// perform the call
		CategoryDto categoryDto = categoryService.getCategory(1L);
		// verify 
		assertEquals(this.categoryDto, categoryDto);
	}

	/**
	 * Get categories by id with category id not found
	 * Throw ResourceNotFoundException
	 */
	@DisplayName("Test Mock categoryService getCategory method + categoryRepository")
	@Test
	void testGetCategory_withCategoryIdNotFound() {
		
		// save
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		

		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
								() ->categoryService.getCategory(2L));
				
		// verify
		assertEquals("Category not found with id : 2", resourceNotFoundException.getMessage());

	}

	/**
	 * Add Category
	 */
	@DisplayName("Test Mock categoryService addCategory method + categoryRepository")
	@Test
	void testAddCategory() {
		
		// save
		when(categoryRepository.save(any(Category.class))).thenReturn(category);
		
		// perform the call
		CategoryDto categoryDto = categoryService.addCategory(this.categoryDto);
		// verify 
		assertEquals(this.categoryDto, categoryDto);
	}

	/**
	 * Update Category 
	 */
	@DisplayName("Test Mock categoryService updateCategory method + categoryRepository")
	@Test
	void testUpdateCategory() {
		// save
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
				
		// save
		when(categoryRepository.save(any(Category.class))).thenReturn(category);
		
		// perform the call
		CategoryDto categoryDto = categoryService.updateCategory(this.categoryDto , 1L);
		// verify 
		assertEquals(this.categoryDto, categoryDto);
	}

	/**
	 * Update Category with Category Id not found
	 */
	@DisplayName("Test Mock categoryService updateCategory method + categoryRepository")
	@Test
	void testUpdateCategory_withCategoryIdNotFound() {
		
		// save
		when(categoryRepository.save(any(Category.class))).thenReturn(category);
		
		
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
										() ->categoryService.updateCategory(this.categoryDto , 1L));
						
		// verify
		assertEquals("Category not found with id : 1", resourceNotFoundException.getMessage());

		
	}
	
	
	/**
	 * Delete category 
	 */
	@DisplayName("Test Mock categoryService deleteCategory method + categoryRepository")
	@Test
	@WithMockUser
	void testDeleteCommentById() {
		
		// save 
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
				
		
		// perform the call
		categoryService.deleteCategory(1L);

		// verify the mocks
		verify(categoryRepository).delete(category);

	}
	
	/**
	 * Delete category with Category Id not found
	 * 
	 */
	@DisplayName("Test Mock categoryService deleteCategory method + categoryRepository")
	@Test
	@WithMockUser
	void testDeleteCommentById_withCategoryIdNotFound() {
		
		// perform the call
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
										() ->categoryService.deleteCategory(1L));
						
		// verify
		assertEquals("Category not found with id : 1", resourceNotFoundException.getMessage());

		
	}
	

}
