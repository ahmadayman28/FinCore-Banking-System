package com.fincore.fincorebank.notification.service.impl;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fincore.fincorebank.auth_users.entity.User;
import com.fincore.fincorebank.enums.NotificationType;
import com.fincore.fincorebank.notification.dtos.NotificationDTO;
import com.fincore.fincorebank.notification.entity.Notification;
import com.fincore.fincorebank.notification.repo.NotificationRepo;
import com.fincore.fincorebank.notification.service.NotificationService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

	private final NotificationRepo notificationRepo;
	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;
	
	@Override
	@Async
	public void sendEmail(NotificationDTO notificationDTO, User user) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
			mimeMessageHelper.setTo(notificationDTO.getRecipient());
			mimeMessageHelper.setSubject(notificationDTO.getSubject());
			if (notificationDTO.getTemplateName()!=null) {
				Context context = new Context();
				context.setVariables(notificationDTO.getTemplateVariables());
				String htmlContent = templateEngine.process(notificationDTO.getTemplateName(), context);
				mimeMessageHelper.setText(htmlContent, true);
			}
			else {
				mimeMessageHelper.setText(notificationDTO.getBody(), true);	
			}
			javaMailSender.send(mimeMessage);
			log.info("Email Sent Out");
//			Notification notificationSave= Notification.builder()
//					.recipient(notificationDTO.getRecipient())
//					.subject(notificationDTO.getSubject())
//					.body(notificationDTO.getBody())
//					.notificationType(NotificationType.EMAIL)
//					.user(user)
//					.build();
//			notificationRepo.save(notificationSave);
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}
	}

}
