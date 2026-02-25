package com.revpay.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.notification.NotificationPreference;

@Repository
public interface INotificationPreferenceRepo extends JpaRepository<NotificationPreference, Long> {

}
