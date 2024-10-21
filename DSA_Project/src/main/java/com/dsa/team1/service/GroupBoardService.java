package com.dsa.team1.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.PhotoDTO;
import com.dsa.team1.dto.PostDTO;
import com.dsa.team1.dto.ReplyDTO;
import com.dsa.team1.security.AuthenticatedUser;

public interface GroupBoardService {

	boolean isUserMemberOfGroup(String userId, Integer groupId);
	
	List<Map<String, String>> getMemberProfiles(Integer groupId);
	
	Integer uploadPost(MultipartFile photo, String description, Integer groupId, AuthenticatedUser user) throws IOException;
	
	void addReply(ReplyDTO replyDTO, AuthenticatedUser user);

	List<ReplyDTO> getRepliesByPostId(Integer postId);

	void deleteReply(Integer replyId);

	void editReply(ReplyDTO replyDTO);

	void updateGroup(Integer groupId, String groupName, String description, String interest, String joinMethod,
			Integer memberLimit, String removedMembers, String hashtags, String removedHashtags,
			MultipartFile profileImage) throws IOException;

}
