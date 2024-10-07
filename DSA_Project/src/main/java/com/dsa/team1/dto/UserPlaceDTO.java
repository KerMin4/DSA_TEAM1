package com.dsa.team1.dto;

import java.time.LocalDateTime;

import com.dsa.team1.entity.enums.UserPlaceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPlaceDTO {

	private Integer userPlaceId;		// PK. UserPlace ID
	private String userId;				// FK. User ID
	private Integer placeId;			// FK. Place ID
	private LocalDateTime joindAt;		// When user has joined the Place post
	private UserPlaceStatus status;		// Whether user request is approved
}
