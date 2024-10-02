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
public class GroupHashtagDTO {

	private Integer hashtagId;	// PK. Hashtag ID
	private Integer groupId;	// FK. SocialGroup ID
	private String name;		// Hashtag's name
}
