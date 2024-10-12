package com.dsa.team1.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.dsa.team1.entity.enums.UserGroupStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

	private Integer postId;
	
	// Either groupId or placeId must be not null
	private Integer groupId;
	private Integer placeId;
	
	private String userId;
	private String content;
	private LocalDateTime createdAt;
	
	private String imageName;
	private List<PhotoDTO> photos;
}
