package com.dsa.team1.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.SocialGroupDTO;
import com.dsa.team1.entity.enums.GroupJoinMethod;

public interface SocialGroupService {

	void create(SocialGroupDTO socialGroupDTO, String uploadPath, MultipartFile upload, String hashtags, GroupJoinMethod joinMethod) throws IOException;

}
