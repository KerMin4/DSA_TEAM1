package com.dsa.team1.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.dsa.team1.entity.NotificationEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.repository.NotificationRepository;

import jakarta.transaction.Transactional;
import lombok.Getter;
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

}
