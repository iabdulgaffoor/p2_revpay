package com.revpay.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.notification.Notification;

@Repository
public interface INotificationRepo extends JpaRepository<Notification, Long> {

}
