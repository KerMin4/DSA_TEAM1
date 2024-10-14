package com.dsa.team1.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.PhotoDTO;
import com.dsa.team1.dto.PostDTO;
import com.dsa.team1.dto.ReplyDTO;
import com.dsa.team1.entity.PhotoEntity;
import com.dsa.team1.entity.PostEntity;
import com.dsa.team1.entity.ReplyEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.entity.enums.Interest;
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
     * 그룹 업데이트 시 DB에 해당 그룹의 정보를 저장
     */
    @Override
	public void updateGroup(Integer groupId, String groupName, String description, String location, String eventDate,
	        String interest, String joinMethod, Integer memberLimit, String hashtags, MultipartFile profileImage) throws IOException {

	    SocialGroupEntity group = socialGroupRepository.findById(groupId)
	            .orElseThrow(() -> new IllegalArgumentException("잘못된 그룹 ID입니다."));

	    group.setGroupName(groupName);
	    group.setDescription(description);
	    group.setLocation(location);

	    // LocalDate를 LocalDateTime으로 변환
	    LocalDateTime eventDateTime = LocalDate.parse(eventDate).atStartOfDay();
	    group.setEventDate(eventDateTime);  // 변환한 LocalDateTime을 설정

	    group.setInterest(Interest.valueOf(interest));
	    group.setGroupJoinMethod(GroupJoinMethod.valueOf(joinMethod.toUpperCase()));
	    group.setMemberLimit(memberLimit);
	    
	    // 프로필 이미지 처리
	    if (profileImage != null && !profileImage.isEmpty()) {
	        String fileName = fileManager.saveFile("C:/upload", profileImage);  // 파일 저장
	        group.setProfileImage(fileName);  // 저장된 파일 이름을 그룹 프로필 이미지로 설정
	    }

	    socialGroupRepository.save(group);
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
     * 공지사항에서 텍스트로만 이루어진 게시물을 필터링
     */
    public List<PostDTO> getAnnouncementsByGroupId(Integer groupId) {
        return postRepository.findByGroup_GroupId(groupId).stream()
            .filter(post -> photoRepository.findByPost_PostId(post.getPostId()).isEmpty())  // 해당 포스트에 사진이 없는지 확인
            .map(post -> PostDTO.builder()
                .postId(post.getPostId())
                .groupId(post.getGroup().getGroupId())
                .userId(post.getUser().getUserId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }
//    public List<PostDTO> getAnnouncementsByGroupId(Integer groupId) {
//        SocialGroupEntity group = socialGroupRepository.findById(groupId)
//            .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
//
//        return postRepository.findByGroup_GroupId(groupId).stream()
//        	.filter(post -> photoRepository.findByPost_PostId(post.getPostId()).isEmpty())  // 사진이 없는 포스트만
//            .map(post -> PostDTO.builder()
//                .postId(post.getPostId())
//                .groupId(post.getGroup().getGroupId())
//                .userId(post.getUser().getUserId())
//                .content(post.getContent())
//                .createdAt(post.getCreatedAt())
//                .build())
//            .collect(Collectors.toList());
//    }

    /**
     * 앨범 포스트 업로드 
     */
    @Override
    public Integer uploadPost(MultipartFile photo, String description, Integer groupId, AuthenticatedUser user) throws IOException {
        // 사용자 인증 확인
        if (user == null) {
            throw new RuntimeException("사용자 인증 정보가 없습니다.");
        }

        // 그룹 조회
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));

        // 게시글 생성
        PostEntity post = PostEntity.builder()
            .group(group)
            .user(userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")))
            .content(description)
            .createdAt(LocalDateTime.now())
            .build();
        
        postRepository.save(post); // 포스트 저장

        // 사진 업로드 처리
        if (photo != null && !photo.isEmpty()) {
            String fileName = fileManager.saveFile("C:/upload", photo); // 파일 저장
            log.info("파일이 성공적으로 저장되었습니다: {}", fileName);

            // PhotoEntity에 사진 정보 저장
            PhotoEntity photoEntity = PhotoEntity.builder()
                .imageName(fileName)
                .post(post)  // 포스트와 연결
                .group(group)
                .build();
            photoRepository.save(photoEntity); // 사진 저장
        } else {
            throw new RuntimeException("업로드할 파일이 없습니다.");
        }
        
        return post.getPostId(); // 업로드된 포스트의 postId 반환
    }
    
    /**
     * 앨범에서는 사진이 있는 게시물만 조회하는 서비스 로직 
     */
    public List<PostDTO> getAlbumPostsByGroupId(Integer groupId) {
        return photoRepository.findByGroup_GroupId(groupId).stream()
            .filter(photo -> photo.getPost() != null)  // PostEntity가 null이 아닌 경우
            .map(PhotoEntity::getPost)  // 사진에 연결된 PostEntity 가져오기
            .distinct()  // 중복된 포스트 제거
            .map(post -> PostDTO.builder()
                .postId(post.getPostId())
                .groupId(post.getGroup().getGroupId())
                .userId(post.getUser().getUserId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }

    
    /**
     * 특정 그룹의 앨범 사진 목록을 반환
     */
    @Override
    public List<PhotoDTO> getPhotosByGroupId(Integer groupId) {
    	return photoRepository.findByGroup_GroupId(groupId).stream()
    	        .map(photo -> {
    	            // PhotoEntity에서 필요한 필드들을 DTO로 변환
    	            return PhotoDTO.builder()
    	                .photoId(photo.getPhotoId())
    	                .imageName(photo.getImageName())
    	                .postId(photo.getPost() != null ? photo.getPost().getPostId() : null)  // PostEntity에서 postId 가져옴
    	                .build();
    	        })
    	        .collect(Collectors.toList());
    }
    
    /**
     * 포스트의 ID로 앨범 사진을 조회 
     */
    public List<PhotoDTO> getPhotosByPostId(Integer postId) {
        return photoRepository.findByPost_PostId(postId).stream()
            .map(photo -> PhotoDTO.builder()
                .photoId(photo.getPhotoId())
                .imageName(photo.getImageName())
                .build())
            .collect(Collectors.toList());
    }

    /**
     *  Post 상세 정보 가져오기
     */
    @Override
    public PostDTO getPostDetail(Integer postId) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("포스트를 찾을 수 없습니다: " + postId));

        List<PhotoDTO> photoDTOs = photoRepository.findByPost_PostId(postId).stream()
            .map(photo -> PhotoDTO.builder()
                .photoId(photo.getPhotoId())
                .imageName(photo.getImageName())
                .build())
            .collect(Collectors.toList());

        // PostDTO 반환 (사진 포함)
        return PostDTO.builder()
            .postId(post.getPostId())
            .userId(post.getUser().getUserId())
            .createdAt(post.getCreatedAt())
            .content(post.getContent())
            .photos(photoDTOs)  // 추가된 사진 리스트
            .build();
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

}
