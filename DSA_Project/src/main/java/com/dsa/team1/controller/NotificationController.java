package com.dsa.team1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dsa.team1.entity.NotificationEntity;
import com.dsa.team1.repository.NotificationRepository;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
	
	private final NotificationRepository notificationRepository;
	private final NotificationService notificationService;
	/*
	@GetMapping("/notify")
	public SseEmitter sendNotification() {
		SseEmitter emitter = new SseEmitter(60000L);
		Executors.newSingleThreadScheduledExecutor().schedule(()->{
			try {
				emitter.send("이 모임에 참여하고 싶어합니다! // 들어오는 사람 넣어야함", MediaType.TEXT_PLAIN);
				emitter.complete();
			} catch(IOException e) {
				emitter.completeWithError(e);
			}
		}, 2, TimeUnit.SECONDS);
	
		return emitter;
	}
	*/
	/*
	@GetMapping("/check")
	@ResponseBody
	public boolean checkUnreadNotifications(@AuthenticationPrincipal AuthenticatedUser user) {
		log.debug("진입완료");
		notificationRepository.existsByUser_UserIdAndReadStatusFalse(user.getId())
		return notificationService.hasUnreadNotification(user.getId());
	}
	
	
	@PostMapping("/markAsRead")
    public ResponseEntity<String> markAllAsRead(@RequestParam("userId") String userId) {
        /*List<NotificationEntity> notifications = 
            notificationRepository.findByUser_UserIdAndReadStatusFalse(userId);

        notifications.forEach(notification -> notification.setReadStatus(true));
        
        notificationService.markNotificationsAsRead(userId);
        //notificationRepository.saveAll(notifications);
        
        return ResponseEntity.ok().build();
    }*/
}
