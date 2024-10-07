package com.dsa.team1.dto;

import java.time.LocalDateTime;

import com.dsa.team1.entity.enums.GroupJoinMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialGroupDTO {

	private Integer groupId;					// PK. Social Group ID
	private String groupName;					// Social Group's name
	private String description;					// Description of the group
	private String profileImage;				// Image representing the group
	private String location;					// Group members' preferred region
	private String groupLeaderId;				// FK. Group Leader ID
	private GroupJoinMethod groupJoinMethod;	// Whether approval need or not when joining the group
	private Integer memberLimit;				// Maximum number of the group members
	private Integer viewCount;					// How many users have seen the group post
	private Integer bookmarkCount;				// How many users have bookmarked the group post
	private LocalDateTime eventDate;			// When the group event takes
	private LocalDateTime createdAt;			// When the group is created
	
}
