package com.fincore.fincorebank;

import java.beans.BeanProperty;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.fincore.fincorebank.auth_users.entity.User;
import com.fincore.fincorebank.enums.NotificationType;
import com.fincore.fincorebank.notification.dtos.NotificationDTO;
import com.fincore.fincorebank.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class FincorebankApplication {
//	private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(FincorebankApplication.class, args);
	}
	
//	@Bean
//	CommandLineRunner runner() {
//		return args->{
//			NotificationDTO notificationDTO = NotificationDTO.builder()
//					.recipient("ahmedayman778770@gmail.com")
//					.subject("Hello Testing Email")
//					.body("Hi, This is testing mail")
//					.notificationType(NotificationType.EMAIL)
//					.build();
//			notificationService.sendEmail(notificationDTO, new User());
//		};
//	}
}
