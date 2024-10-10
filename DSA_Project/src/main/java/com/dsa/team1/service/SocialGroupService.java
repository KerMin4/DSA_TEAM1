package com.dsa.team1.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.SocialGroupDTO;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.security.AuthenticatedUser;

public interface SocialGroupService {

	void create(List<String> interest, String groupName, String eventDate, String description, String location,
			String joinMethod, Integer memberLimit, List<String> hashtagList, MultipartFile profileImage,
			AuthenticatedUser user) throws IOException;

	List<SocialGroupDTO> getAllGroups();

}
