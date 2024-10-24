package com.dsa.team1.service;

import com.dsa.team1.entity.UserEntity;

public interface NotificationService {
	public void sendNotification(UserEntity user, String message);

	public boolean hasUnreadNotification(String id);

	public void markNotificationsAsRead(String userId);

	public void notificationDelete(Integer notificationId);
}
