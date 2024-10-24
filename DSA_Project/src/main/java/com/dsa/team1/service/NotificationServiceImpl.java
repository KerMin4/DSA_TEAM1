package com.dsa.team1.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsa.team1.entity.NotificationEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.repository.NotificationRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService{

	private final NotificationRepository notificationRepository;
	@Override
	public void sendNotification(UserEntity user, String message) {
		NotificationEntity notification = NotificationEntity.builder()
				.user(user)
				.message(message)
				.readStatus(false)
				.createdAt(LocalDateTime.now())
				.build();
		
		notificationRepository.save(notification);
	}
	@Override
	public boolean hasUnreadNotification(String id) {
		return notificationRepository.existsByUser_UserIdAndReadStatusFalse(id);
	}
	@Override
	public void markNotificationsAsRead(String userId) {
		List<NotificationEntity> notifications = notificationRepository.findByUser_UserIdAndReadStatusFalse(userId);
		for(NotificationEntity notification : notifications) {
			notification.setReadStatus(true);
		}
		notificationRepository.saveAll(notifications);
	}
	@Override
	public void notificationDelete(Integer notificationId) {
		NotificationEntity entity = notificationRepository.findById(notificationId).orElseThrow(()-> new EntityNotFoundException("알림이 없습니다."));
		notificationRepository.delete(entity);
	}

}
