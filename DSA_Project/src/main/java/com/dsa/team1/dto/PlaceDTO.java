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
public class PlaceDTO {

	private Integer placeId;			// PK. Place ID
	private String title;				// Name of Place post
	private String description;			// Description of Place post
	private String profileImage;
	private String category;
	private String location;			// Where the event will takes
	private LocalDateTime eventDate;	// When the event will takes
	private Integer requiredMembers;	// Minimum number of the group members
	private Integer currentMembers;		// Current number of the group members
	private Integer memberLimit;
	private Integer viewCount;
	private Integer bookmarkCount;
	private Double price;				// Price required to join the Place post
	private Integer vendorId;			// FK. Place post creator ID(User ID_
	private LocalDateTime createdAt;	// When the Place post was created
}
