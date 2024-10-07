package com.dsa.team1.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {
	
	private Integer replyId;
	private Integer postId;
	private String userId;
	private String content;
	private LocalDateTime createdAt;
}
