package com.springboot.blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.blog.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(String email);
	
	Optional<User> findByUsernameOrEmail(String username , String email);
	
	Optional<User> findByUsername(String username );
	
	Boolean existsByUsername(String username);
	
	Boolean existsByEmail(String email);

}
