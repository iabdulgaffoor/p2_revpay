package com.revpay.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.revpay.domain.entity.User;
import com.revpay.domain.enums.AccountType;
import com.revpay.domain.enums.UserStatus;

public class UserSpecification {
	
	public static Specification<User> hasAccountType(AccountType accountType) {
		return (root, cq, cb) -> 
					accountType == null ? null :
					cb.equal(root.get("accountType"), accountType);
	}
	
	public static Specification<User> hasUserStatus(UserStatus userStatus) {
		return (root, cq, cb) -> 
					userStatus == null ? null :
					cb.equal(root.get("status"), userStatus);
	}
	
	public static Specification<User> keywordSearch(String keyword) {
		return (root, cq, cb) -> {
			if (keyword == null || keyword.isEmpty()) {
				return null;
			}
			
			String pattern = "%" + keyword + "%";
			
			return cb.or(
					cb.like(cb.lower(root.get("fullName")), pattern),
					cb.like(cb.lower(root.get("email")), pattern),
					cb.like(cb.lower(root.get("phone")), pattern)
					);
		};
	}
	
	public static Specification<User> createdBetween(LocalDateTime start, LocalDateTime end) {
		return (root, cq, cb) -> {
			if (start == null && end == null) {
				return null;
			}
			
			final String CREATED_AT = "createdAt";
			
			if (start !=null && end != null) {
				return cb.between(root.get(CREATED_AT), start, end);
			}
			
			if (start != null) {
				return cb.greaterThanOrEqualTo(root.get(CREATED_AT), start);
			}
			
			return cb.lessThanOrEqualTo(root.get(CREATED_AT), end);
		};
	}
	
}
