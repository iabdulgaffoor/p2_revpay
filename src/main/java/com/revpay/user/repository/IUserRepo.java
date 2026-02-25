package com.revpay.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.user.User;

@Repository
public interface IUserRepo extends JpaRepository<User, Long>, 
									JpaSpecificationExecutor<User> {
	
	Optional<User> findByEmail(String email);
	Optional<User> findByPhone(String phone);
	
	boolean existsByEmail(String mail);
	boolean existsByPhone(String phone);
	
	
}
