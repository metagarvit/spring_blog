package com.springboot.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto) {

		CategoryDto saveCategoryDto = categoryService.addCategory(categoryDto);

		return new ResponseEntity<CategoryDto>(saveCategoryDto, HttpStatus.CREATED);

	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoryDto> getCategory(@PathVariable("id") Long categoryId) {

		CategoryDto saveCategoryDto = categoryService.getCategory(categoryId);

		return ResponseEntity.ok(saveCategoryDto);

	}

	@GetMapping()
	public ResponseEntity<List<CategoryDto>> getAllCategory() {
		return ResponseEntity.ok(categoryService.getAllCategories());
		
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CategoryDto> updateCategory( @PathVariable("id") Long categoryId ,  @RequestBody CategoryDto categoryDto) {

		CategoryDto saveCategoryDto = categoryService.updateCategory(categoryDto , categoryId);

		return new ResponseEntity<CategoryDto>(saveCategoryDto, HttpStatus.OK);

	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteCategory( @PathVariable("id") Long categoryId ) {
		
		 categoryService.deleteCategory(  categoryId);
		
		return  ResponseEntity.ok("Deleted Successfully");
		
	}

}
