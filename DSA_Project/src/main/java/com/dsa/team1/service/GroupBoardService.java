package com.dsa.team1.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.PhotoDTO;
import com.dsa.team1.dto.PostDTO;
import com.dsa.team1.dto.ReplyDTO;
import com.dsa.team1.security.AuthenticatedUser;

public interface GroupBoardService {

	void updateGroup(Integer groupId, String groupName, String description, String location, String eventDate,
			String interest, String joinMethod, Integer memberLimit, String hashtags, MultipartFile profileImage) throws IOException;

	Integer uploadPost(MultipartFile photo, String description, Integer groupId, AuthenticatedUser user) throws IOException;
	
	List<PhotoDTO> getPhotosByGroupId(Integer groupId);

	PostDTO getPostDetail(Integer postId);

	void addReply(ReplyDTO replyDTO, AuthenticatedUser user);

	List<ReplyDTO> getRepliesByPostId(Integer postId);

	void deleteReply(Integer replyId);

	void editReply(ReplyDTO replyDTO);
	
	List<PostDTO> getAnnouncementsByGroupId(Integer groupId);
	
	List<PostDTO> getAlbumPostsByGroupId(Integer groupId);

}
