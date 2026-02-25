package com.revpay.domain.entity.user;

import java.util.ArrayList;
import java.util.List;

import com.revpay.domain.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_role")
public class Role extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String name;
	
	@OneToMany(mappedBy = "role")
	private List<UserRole> userRoles = new ArrayList<>();
	
	/*
	 * to prevent outside object creation with null fields
	 * to accessible for hibernate creating object through reflection-api
	 */
	protected Role() {}
	
	public Role(String name) {
		this.name = name;
	}
	
}
