package com.fincore.fincorebank.notification.service;

import com.fincore.fincorebank.auth_users.entity.User;
import com.fincore.fincorebank.notification.dtos.NotificationDTO;

public interface NotificationService {
	void sendEmail(NotificationDTO notificationDTO, User user);
}