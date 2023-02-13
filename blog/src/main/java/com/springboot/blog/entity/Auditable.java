package com.springboot.blog.entity;


import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
/**
 * @EntityListeners annotation-> This annotation basically enables JPA entity listener so that changes to the entity values can be tracked.
 * @author Garvit
 *
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable {
	
	@CreatedBy
	protected String createdBy ; 

	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@JsonFormat(pattern="dd-MM-yyyy")
	protected Date createDate ; 
	
	
	@LastModifiedBy
	protected String lastModifiedBy ; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@JsonFormat(pattern="dd-MM-yyyy")
	protected Date lastModifiedDate ; 
	
	

}
