package com.fincore.fincorebank.notification.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fincore.fincorebank.notification.entity.Notification;

public interface NotificationRepo extends JpaRepository<Notification, Long>{

}