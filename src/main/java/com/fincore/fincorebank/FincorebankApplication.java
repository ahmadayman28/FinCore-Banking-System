package com.fincore.fincorebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

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
