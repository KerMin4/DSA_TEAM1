package com.dsa.team1.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
	
	private Integer notificationId;		// PK. Notification ID
	private Integer userId;				// FK. User ID
	private String message;				// Content of notification
	private Boolean readStatus;			// Whether the user had read the notification
	private LocalDateTime createdAt;	// When the notification was created

}
