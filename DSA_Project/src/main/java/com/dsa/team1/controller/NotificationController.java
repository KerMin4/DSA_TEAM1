package com.dsa.team1.controller;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NotificationController {
	
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
}
