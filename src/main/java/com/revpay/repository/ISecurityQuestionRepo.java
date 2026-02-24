package com.revpay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.SecurityQuestion;

@Repository
public interface ISecurityQuestionRepo extends JpaRepository<SecurityQuestion, Long> {
	
	Optional<String> findByQuestion(String question);
	
}
