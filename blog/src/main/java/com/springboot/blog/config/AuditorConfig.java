package com.springboot.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Controller;

import com.springboot.blog.service.impl.AuditorAwareImpl;

/**
 * This class is used to enable JPA auditing 
 *  @EnableJpaAuditing specify that it will be using an instance of AuditorAware interface.
 * @author Garvit
 *
 */
@Configuration
@EnableJpaAuditing
public class AuditorConfig {
	
	@Bean
	public AuditorAware<String>  auditorAware(){
		return new AuditorAwareImpl();
	}

}
