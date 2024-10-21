package com.dsa.team1.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.PostDTO;
import com.dsa.team1.dto.ReplyDTO;
import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.PhotoEntity;
import com.dsa.team1.entity.PostEntity;
import com.dsa.team1.entity.ReplyEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.entity.enums.PostType;
import com.dsa.team1.repository.BookmarkRepository;
import com.dsa.team1.repository.GroupHashtagRepository;
import com.dsa.team1.repository.PhotoRepository;
import com.dsa.team1.repository.PostRepository;
import com.dsa.team1.repository.ReplyRepository;
import com.dsa.team1.repository.SocialGroupRepository;
import com.dsa.team1.repository.UserGroupRepository;
import com.dsa.team1.repository.UserRepository;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.util.FileManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GroupBoardServiceImpl implements GroupBoardService {
	
	private final SocialGroupRepository socialGroupRepository;   
    private final UserRepository userRepository;                 
    private final GroupHashtagRepository groupHashtagRepository;
    private final UserGroupRepository userGroupRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PhotoRepository photoRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final FileManager fileManager;

    /**
     * 그룹 멤버인지 확인 
     */
    @Override
    public boolean isUserMemberOfGroup(String userId, Integer groupId) {
        boolean isMember = userGroupRepository.existsByUserAndGroupId(userId, groupId);
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
        
        // 그룹 리더도 멤버로 간주
        if (!isMember && group.getGroupLeader().getUserId().equals(userId)) {
            isMember = true;
        }
        return isMember;
    }
    
    /**
     * 멤버프로필 이미지 가져오기 
     */
    @Override
    public List<Map<String, String>> getMemberProfiles(Integer groupId) {
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("잘못된 그룹 ID입니다."));

        String groupLeaderId = group.getGroupLeader().getUserId();
        UserEntity groupLeader = userRepository.findByUserId(groupLeaderId)
            .orElseThrow(() -> new IllegalArgumentException("그룹 리더를 찾을 수 없습니다."));

        List<UserEntity> members = userGroupRepository.findActiveMembersByGroupId(groupId);
        if (members.stream().noneMatch(member -> member.getUserId().equals(groupLeaderId))) {
            members.add(groupLeader);
        }

        return members.stream()
            .map(member -> {
                Map<String, String> profile = new HashMap<>();
                profile.put("userId", member.getName());
                profile.put("profileImage", member.getProfileImage() != null ? member.getProfileImage() : "defaultProfile.png");
                return profile;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 공지사항 업로드
     */
    public void uploadAnnouncement(PostDTO postDTO) {
        // 그룹과 사용자 정보를 사용해 PostEntity 직접 생성
        SocialGroupEntity group = socialGroupRepository.findById(postDTO.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
        UserEntity user = userRepository.findByUserId(postDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // PostEntity 생성
        PostEntity postEntity = PostEntity.builder()
                .group(group)
                .user(user)
                .content(postDTO.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        
        postRepository.save(postEntity);
    }

    /**
     * 공지사항 수정
     */
    public void editAnnouncement(PostDTO postDTO) {
        PostEntity postEntity = postRepository.findById(postDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다."));
        
        // 공지사항 수정
        postEntity.setContent(postDTO.getContent());
        postRepository.save(postEntity);
    }

    /**
     * 공지사항 삭제
     */
    public void deleteAnnouncement(Integer postId) {
        postRepository.deleteById(postId);
    }
    
    /**
     * 앨범 포스트 업로드 
     */
    @Override
    public Integer uploadPost(MultipartFile photo, String content, Integer groupId, AuthenticatedUser user) throws IOException {
        // 사용자 인증 확인
        if (user == null) {
            throw new RuntimeException("사용자 인증 정보가 없습니다.");
        }

        // 그룹 조회
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));

        // 포스트 생성 (PostEntity를 먼저 생성)
        PostEntity post = PostEntity.builder()
            .group(group)
            .user(userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")))
            .content(content)
            .createdAt(LocalDateTime.now())
            .postType(PostType.GENERAL)
            .build();
        
        postRepository.save(post); // 포스트를 먼저 저장

        // 사진 업로드 처리
        if (photo != null && !photo.isEmpty()) {
            String fileName = fileManager.saveFile("C:/upload", photo); // 파일 저장
            log.info("파일이 성공적으로 저장되었습니다: {}", fileName);

            // PhotoEntity에 사진 정보 저장
            PhotoEntity photoEntity = PhotoEntity.builder()
                .imageName(fileName)
                .post(post)  // 포스트와 연결
                .createdAt(LocalDateTime.now())
                .group(group)
                .build();
            
            photoRepository.save(photoEntity); // 사진 저장
        } else {
            throw new RuntimeException("업로드할 파일이 없습니다.");
        }

        return post.getPostId(); // 업로드된 포스트의 postId 반환
    }
    
    /**
     * 댓글 목록 가져오기
     */ 
    @Override
    public List<ReplyDTO> getRepliesByPostId(Integer postId) {
        return replyRepository.findByPost_PostId(postId).stream()
            .map(reply -> ReplyDTO.builder()
                .replyId(reply.getReplyId())
                .postId(reply.getPost().getPostId())
                .userId(reply.getUser().getUserId())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }
    
    /**
     * 댓글 작성 
     */
    public void addReply(ReplyDTO replyDTO, AuthenticatedUser user) {
        PostEntity post = postRepository.findById(replyDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        UserEntity userEntity = userRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        ReplyEntity replyEntity = ReplyEntity.builder()
                .post(post)
                .user(userEntity)
                .content(replyDTO.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        
        replyRepository.save(replyEntity);
    }
    
    /**
     * 댓글 수정
     */
    public void editReply(ReplyDTO replyDTO) {
        ReplyEntity replyEntity = replyRepository.findById(replyDTO.getReplyId())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));
        
        replyEntity.setContent(replyDTO.getContent());
        replyRepository.save(replyEntity);
    }

    /**
     * 댓글 삭제 
     */
    public void deleteReply(Integer replyId) {
        replyRepository.deleteById(replyId);
    }

    /**
     * 그룹 업데이트 시 DB에 해당 그룹의 정보를 저장
     */
    @Override
	public void updateGroup(Integer groupId, String groupName, String description, String interest, String joinMethod,
			Integer memberLimit, String removedMembers, String hashtags, String removedHashtags,  MultipartFile profileImage) throws IOException {

	    SocialGroupEntity group = socialGroupRepository.findById(groupId)
	            .orElseThrow(() -> new IllegalArgumentException("잘못된 그룹 ID입니다."));

	    group.setGroupName(groupName);
	    group.setDescription(description);
	    group.setInterest(Interest.valueOf(interest));
	    group.setGroupJoinMethod(GroupJoinMethod.valueOf(joinMethod.toUpperCase()));
	    group.setMemberLimit(memberLimit);
	    
	    // 프로필 이미지 처리
	    if (profileImage != null && !profileImage.isEmpty()) {
	        String fileName = fileManager.saveFile("C:/upload", profileImage);  // 파일 저장
	        group.setProfileImage(fileName);  // 저장된 파일 이름을 그룹 프로필 이미지로 설정
	    }
	    
	    // 삭제된 멤버 처리
	    if (removedMembers != null && !removedMembers.trim().isEmpty()) {
	        String[] memberIds = removedMembers.split(",");
	        for (String memberId : memberIds) {
	            userGroupRepository.deleteByGroup_GroupIdAndUser_UserId(groupId, memberId.trim());
	        }
	    }
	    
	    // 새로운 해시태그 추가
	    if (hashtags != null && !hashtags.trim().isEmpty()) {
	        String[] hashtagArray = hashtags.split(",");
	        for (String hashtag : hashtagArray) {
	        	// 모든 # 기호를 제거한 후 앞에 한 번만 추가
	        	String cleanHashtag = hashtag.trim().replaceAll("^#+", ""); // 모든 # 기호를 제거하고 저장

	            // 중복이 없는 경우에만 저장
	            if (!cleanHashtag.isEmpty() && !groupHashtagRepository.existsByGroupAndName(group, cleanHashtag)) {
	                GroupHashtagEntity hashtagEntity = GroupHashtagEntity.builder()
	                    .group(group)
	                    .name(cleanHashtag)
	                    .build();
	                groupHashtagRepository.save(hashtagEntity);  // 해시태그 저장
	            }
	        }
	    }
	    
	    // 삭제된 해시태그 처리 (removedHashtags)
	    if (removedHashtags != null && !removedHashtags.trim().isEmpty()) {
	        String[] removedHashtagArray = removedHashtags.split(",");
	        for (String removedHashtag : removedHashtagArray) {
	            String cleanRemovedHashtag = removedHashtag.trim().replaceAll("^#+", ""); // # 제거한 후 처리
	            if (!cleanRemovedHashtag.isEmpty()) {
	                groupHashtagRepository.deleteByGroupAndName(group, cleanRemovedHashtag);
	            }
	        }
	    }
	}
    
}
