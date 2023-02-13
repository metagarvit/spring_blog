package com.springboot.blog.service.impl;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public class AuditorAwareImpl  implements AuditorAware<String> {
	
	 @Override
	    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	    public Optional<String>  getCurrentAuditor() {
	        Authentication authentication = 
	        SecurityContextHolder.getContext().getAuthentication();
	        
	        if (authentication == null || !authentication.isAuthenticated()) {
				return null;
			}
	        
	        Optional<String> username = Optional.ofNullable(authentication.getPrincipal().toString()).filter(s -> !s.isEmpty());
	        return username ;
	    }

	}
