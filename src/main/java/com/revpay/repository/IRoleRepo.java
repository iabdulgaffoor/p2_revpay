package com.revpay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.Role;

@Repository
public interface IRoleRepo extends JpaRepository<Role, Long> {
	
	List<Role> findByNameContainingIgnoreCase(String name);
	
}
