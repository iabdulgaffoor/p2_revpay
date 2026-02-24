package com.revpay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.Notification;

@Repository
public interface INotificationRepo extends JpaRepository<Notification, Long> {

	List<Notification> findByType(String type);
	
	List<Notification> findByIsRead(Boolean isRead);
	
}
