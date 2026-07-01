package com.fincore.fincorebank.notification.dtos;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fincore.fincorebank.auth_users.entity.User;
import com.fincore.fincorebank.enums.NotificationType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {
	private Long id;
	
	private String subject;
	@NotBlank(message = "Recipient is required")
	private String recipient;
	
	private String body;
	
	private NotificationType notificationType;
	
	private User user;
	
	private LocalDateTime createdAt;
	
	private String templateName;
	private Map<String, Object> templateVariables;
}