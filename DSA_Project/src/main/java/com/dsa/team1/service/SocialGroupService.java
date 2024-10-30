package com.dsa.team1.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.BookmarkDTO;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.security.AuthenticatedUser;

public interface SocialGroupService {

	void create(List<String> interest, String groupName, LocalDateTime eventDateTime, String description, String location,
			String joinMethod, Integer memberLimit, List<String> hashtagList, MultipartFile profileImage,
			AuthenticatedUser user) throws IOException;
	
	int getMemberCountByGroup(SocialGroupEntity group);
	
	List<SocialGroupEntity> findAllGroups();

	public List<SocialGroupEntity> searchGroups(String query, String category, String location, String sort);

	void toggleBookmark(UserEntity userEntity, SocialGroupEntity group);

	SocialGroupEntity findById(Integer groupId);

	void requestApprovalToJoinGroup(String id, Integer groupId);

	void addMemberToGroup(String id, Integer groupId);

	void rejectJoinRequest(Integer userId, Integer groupId);

	void addMemberToGroup(Integer userId, Integer groupId);

	boolean isUserMemberOfGroup(String userId, Integer groupId);
	
	
	// 내꺼 -나연-
	void deleteGroupById(Integer groupId, String userId);
	Map<Interest, Long> getInterestGroupStatistics(String userId);
	List<SocialGroupEntity> getGroupsCreatedByUser(String userId);
	List<SocialGroupEntity> getJoinedGroupsByUser(String userId);
    void leaveGroup(String userId, Integer groupId);
	List<BookmarkDTO> getBookmarksByUserId(String userId);
	void removeBookmark(String userId, Integer groupId);
	void addBookmark(String userId, Integer groupId);
	boolean isBookmarked(String userId, Integer groupId);
	// ---------

}
