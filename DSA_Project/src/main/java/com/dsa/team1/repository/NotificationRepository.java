package com.dsa.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.NotificationEntity;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {
	List<NotificationEntity> findByUser_UserIdOrderByCreatedAtDesc(String userId);
	boolean existsByUser_UserIdAndReadStatusFalse(String userId);
	List<NotificationEntity> findByUser_UserIdAndReadStatusFalse(String userId);
}
