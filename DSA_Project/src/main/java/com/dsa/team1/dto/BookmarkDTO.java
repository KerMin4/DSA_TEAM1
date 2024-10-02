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
public class BookmarkDTO {
	
	private Integer bookmarkId;			// PK. Bookmark ID
	private Integer userId;				// FK. User ID
	private Integer placeId;			// FK. Place ID
	private Integer groupId;			// FK. SocialGroup ID
	private LocalDateTime createdAt;	// When user has marked
}
