package com.revpay.domain.entity.auth;

import com.revpay.domain.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_user_security_answer")
public class UserSecurityAnswer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String answer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "revpay_user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "revpay_security_question_id", nullable = false)
	private SecurityQuestion question;

	public UserSecurityAnswer(String answer, User user, SecurityQuestion question) {
		this.user = user;
		this.question = question;
		this.answer = answer;
	}
}
