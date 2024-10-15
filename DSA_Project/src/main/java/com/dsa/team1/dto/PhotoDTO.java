package com.dsa.team1.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDTO {

	private Integer photoId;
	private Integer postId;
	private String imageName;
	private LocalDateTime createdAt;
	
	private List<PhotoDTO> photos;
}
