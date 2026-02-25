package com.revpay.domain.entity.notification;


import com.revpay.domain.entity.BaseEntity;
import com.revpay.domain.entity.user.User;
import com.revpay.domain.enums.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revpay_notification")
public class Notification extends BaseEntity {

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;
	
	@Column(nullable = false)
	private String message;
	
	private Boolean isRead = false;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "revpay_user_id", nullable = false)
	private User user;
}
