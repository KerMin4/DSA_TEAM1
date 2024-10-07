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
public class InterestDTO {
	private Integer interestId;
	private String userId;
	private String interest;
}
