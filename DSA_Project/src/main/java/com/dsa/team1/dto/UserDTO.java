package com.dsa.team1.dto;

import java.time.LocalDateTime;

import com.dsa.team1.entity.enums.JoinMethod;
import com.dsa.team1.entity.enums.UserType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	private Integer userId;				// PK. User ID
	private String userName;			// User's real name
	private String password;			// Password
	private String name;				// Nickname. User customized name
	private String phoneNumber;			// Phone number
	private String email;				// Email
	private String interests;			// Category
	private String preferredLocation;	// User prefered area
	private String joinMethod;		// joined usual(Website) or Social(Kakao) login
	private String profileImage;		// User customized profile
	private UserType userType;			// joined as individual or vendor
	private LocalDateTime createdAt;	// joined date and time
	private LocalDateTime updatedAt;
}
