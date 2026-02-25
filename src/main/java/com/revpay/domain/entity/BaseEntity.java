package com.revpay.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/*
 * @MappedSuperclass annotation used to define
 * - a parent class inherited by child entities without creating table of this class
 * The base-entity marked as abstract because to prevent the accidental instantiation
 * readability for others and also following oops
 * @PrePersist is a life-cycle annotation, it runs before new object inserted into database
 * @PreUpdate is a life-cycle annotation, it runs before updating the existed existed entity in the database
 * 
 * */

@MappedSuperclass
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;
	
	protected LocalDateTime createdAt;
	protected LocalDateTime updatedAt;
	
	@PrePersist
	protected void beforeInsert() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void beforeUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
