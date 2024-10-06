package com.dsa.team1.dto;

import java.time.LocalDateTime;

import com.dsa.team1.entity.enums.UserGroupStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupDTO {

	private Integer userGroupId;			// PK. UserGroup ID
	private Integer userId;					// FK. User ID
	private Integer groupId;				// FK. Social Group ID
	private LocalDateTime joinedAt;			// Date and time User joined the group
	private UserGroupStatus userStatus;		// Whether user is authorized to join the group
}
