package com.dsa.team1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.PhotoDTO;
import com.dsa.team1.dto.PostDTO;
import com.dsa.team1.dto.UserDTO;
import com.dsa.team1.entity.PhotoEntity;
import com.dsa.team1.entity.PostEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.repository.BookmarkRepository;
import com.dsa.team1.repository.GroupHashtagRepository;
import com.dsa.team1.repository.PhotoRepository;
import com.dsa.team1.repository.PostRepository;
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
    private final FileManager fileManager;
    
    /**
     * 그룹 업데이트 시 DB에 해당 그룹의 정보를 저장
     * @param groupId      업데이트할 그룹의 고유 ID
     * @param groupName    그룹의 이름
     * @param description  그룹의 설명
     * @param location     그룹의 위치 정보
     * @param eventDate    그룹 이벤트 날짜 (yyyy-MM-dd 형식의 문자열)
     * @param interest     그룹의 관심사 (Interest enum 값)
     * @param joinMethod   그룹 가입 방식 (GroupJoinMethod enum 값)
     * @param memberLimit  그룹의 최대 회원 수
     * @param hashtags     그룹과 연관된 해시태그들 (콤마로 구분된 문자열)
     * @throws IllegalArgumentException 전달된 groupId에 해당하는 그룹이 존재하지 않을 경우
     */
    @Override
	public void updateGroup(Integer groupId, String groupName, String description, String location, String eventDate,
	        String interest, String joinMethod, Integer memberLimit, String hashtags) {

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

	    socialGroupRepository.save(group);
	}
    
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
        
        try {
            // 포스트 저장
            postRepository.save(post);
            log.info("포스트가 성공적으로 저장되었습니다. postId: {}", post.getPostId());
        } catch (Exception e) {
            log.error("포스트 저장 중 오류 발생", e);
            throw new RuntimeException("포스트 저장 중 오류가 발생했습니다.", e);  // 예외를 다시 던져서 컨트롤러 계층에서도 처리 가능하도록 함
        }

        try {
            if (photo != null && !photo.isEmpty()) {
                String fileName = fileManager.saveFile("C:/upload", photo); // 파일 저장
                log.info("파일이 성공적으로 저장되었습니다: {}", fileName);

                // PhotoEntity 저장
                PhotoEntity photoEntity = PhotoEntity.builder()
                    .imageName(fileName)
                    .post(post)  // 포스트와 사진 연결
                    .group(group)
                    .build();
                photoRepository.save(photoEntity);
            } else {
                throw new RuntimeException("업로드할 파일이 없습니다.");
            }
        } catch (IOException e) {
            log.error("사진 업로드 중 오류 발생", e);
            throw new RuntimeException("사진 업로드 중 오류 발생", e);
        }
        
        // 업로드된 포스트의 postId 반환
        return post.getPostId();
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
     * 특정 그룹의 앨범 포스트 상세 정보를 반환
     */
    @Override
    public PostDTO getPostDetail(Integer postId) {
    	PostEntity post = postRepository.findById(postId)
    	        .orElseThrow(() -> new IllegalArgumentException("포스트를 찾을 수 없습니다: " + postId));
    	
    	// 필요한 정보를 DTO로 변환
        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(post.getPostId());
        postDTO.setUserId(post.getUser().getUserId());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setContent(post.getContent());
        
        // 사진 리스트를 DTO로 변환
        List<PhotoDTO> photoDTOs = post.getPhotos().stream()
                .map(photo -> PhotoDTO.builder()
                        .photoId(photo.getPhotoId())
                        .imageName(photo.getImageName())
                        .build())
                .collect(Collectors.toList());
        postDTO.setPhotos(photoDTOs);
        
        return postDTO;
    }
    



}
