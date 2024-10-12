package com.dsa.team1.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.PhotoDTO;
import com.dsa.team1.dto.PostDTO;
import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.PhotoEntity;
import com.dsa.team1.entity.PostEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.repository.GroupHashtagRepository;
import com.dsa.team1.repository.PhotoRepository;
import com.dsa.team1.repository.PostRepository;
import com.dsa.team1.repository.SocialGroupRepository;
import com.dsa.team1.repository.UserGroupRepository;
import com.dsa.team1.repository.UserRepository;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.GroupBoardService;
import com.dsa.team1.service.SocialGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("socialgroup")
public class GroupBoardController {
	
	private final SocialGroupService socialGroupService;
	private final GroupBoardService groupBoardService;
    private final SocialGroupRepository socialGroupRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PostRepository postRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupHashtagRepository groupHashtagRepository;
    
    /**
     * 그룹 게시판 페이지로 이동
     * 조회수 카운트
     */
    @GetMapping("/groupBoard")
    public String groupBoard(
    		@RequestParam("groupId") Integer groupId,
    		@AuthenticationPrincipal AuthenticatedUser user,
    		Model model) {
    	
        // 해당 그룹을 조회
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 그룹 ID입니다."));
        
        // 조회수 증가
        if (group.getViewCount() == null) {
            group.setViewCount(0);
        }
        group.setViewCount(group.getViewCount() + 1);
        socialGroupRepository.save(group);
        
        // SocialGroupEntity에서 groupLeaderId를 가져옴
        String groupLeaderId = group.getGroupLeader().getUserId();  // groupLeader에서 userId를 가져옴
        
        // 그룹 리더 정보 조회
        UserEntity groupLeader = userRepository.findByUserId(groupLeaderId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 리더를 찾을 수 없습니다."));
        // 그룹 멤버 조회
        List<UserEntity> members = userGroupRepository.findActiveMembersByGroupId(groupId);
        // 그룹 리더가 이미 멤버 목록에 포함되어 있는지 확인 후, 포함되지 않았다면 추가
        boolean leaderInMembers = members.stream().anyMatch(member -> member.getUserId().equals(groupLeaderId));
        if (!leaderInMembers) {
            members.add(groupLeader);
        }
        // 멤버 목록 및 프로필 이미지 추가
        List<Map<String, String>> memberProfiles = members.stream()
                .map(member -> {
                    Map<String, String> profile = new HashMap<>();
                    profile.put("userId", member.getName());
                    profile.put("profileImage", member.getProfileImage() != null ? member.getProfileImage() : "defaultProfile.png");
                    return profile;
                })
                .collect(Collectors.toList());
        
        // 현재 로그인한 유저가 그룹의 멤버인지 확인
        boolean isMember = userGroupRepository.existsByUserAndGroupId(user.getId(), groupId);
        // 그룹 리더도 멤버로 간주
        if (!isMember && group.getGroupLeader().getUserId().equals(user.getId())) {
            isMember = true;
        }
        model.addAttribute("isMember", isMember);
        
        // 그룹 멤버일 때
        if (isMember) {
            model.addAttribute("group", group);
            model.addAttribute("members", memberProfiles);
            model.addAttribute("groupLeaderId", groupLeader.getUserId());
            return "socialgroup/groupBoard";  // 멤버일 때 그룹 게시판으로 이동
        } else {
            // 비회원일 경우
            model.addAttribute("group", group);
            return "socialgroup/joinGroupInvitation";  // 비회원일 경우 가입 폼 표시
        }
    }
    
    /**
     * 공지사항 탭으로 이동
     * 공지사항 목록 조회
     */
    @GetMapping("/announcement")
    public String announcementPage(
        @RequestParam("groupId") Integer groupId,
        Model model) {

	    // groupId를 모델에 추가하여 Thymeleaf 템플릿에서 사용 가능하도록 설정
	    model.addAttribute("groupId", groupId);
	    return "socialgroup/announcement";
    }
    
    @GetMapping("/announcement/list")
    public ResponseEntity<List<PostDTO>> getAnnouncements(@RequestParam("groupId") Integer groupId) {
        List<PostEntity> announcements = postRepository.findByGroup_GroupId(groupId);

        // PostEntity를 PostDTO로 변환
        List<PostDTO> announcementDTOs = announcements.stream()
            .map(announcement -> PostDTO.builder()
                .postId(announcement.getPostId())
                .groupId(announcement.getGroup().getGroupId())
                .userId(announcement.getUser().getUserId())
                .content(announcement.getContent())
                .createdAt(announcement.getCreatedAt())
                .build())
            .collect(Collectors.toList());

        // 반드시 배열 형태로 반환
        return ResponseEntity.ok(announcementDTOs);
    }

    
    /**
     * 공지사항 등록
     */
    @PostMapping("/announcement/post")
    public ResponseEntity<?> uploadAnnouncement(
        @RequestParam("groupId") Integer groupId,
        @RequestParam("content") String content,
        @AuthenticationPrincipal AuthenticatedUser user) {
        
    	if (groupId == null || content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
        }
        
        // 그룹과 사용자 정보 가져오기
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));
        UserEntity userEntity = userRepository.findById(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    	// 공지사항 엔티티 생성 (PostDTO 대신 직접 변환)
        PostEntity announcementPost = PostEntity.builder()
            .group(group)
            .user(userEntity)
            .content(content)
            .createdAt(LocalDateTime.now())
            .build();

        // 공지사항 저장
        postRepository.save(announcementPost);

        return ResponseEntity.ok("공지사항이 성공적으로 등록되었습니다.");
    }
    
    /**
     * 공지사항 수정
     */
    @PostMapping("/announcement/edit")
    public ResponseEntity<?> editAnnouncement(
        @RequestParam("postId") Integer postId,
        @RequestParam("content") String content) {
        
        // 기존 공지사항 조회
        PostEntity postEntity = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다."));

        // 공지사항 내용 수정
        postEntity.setContent(content);
        postRepository.save(postEntity);

        return ResponseEntity.ok("공지사항이 성공적으로 수정되었습니다.");
    }
    
    /**
     * 공지사항 삭제
     */
    @PostMapping("/announcement/delete")
    public ResponseEntity<?> deleteAnnouncement(
        @RequestParam("postId") Integer postId) {

        // 공지사항 삭제
        postRepository.deleteById(postId);

        return ResponseEntity.ok("공지사항이 성공적으로 삭제되었습니다.");
    }

    /**
     * 일정 탭 클릭 시 일정 HTML 반환
     */
    @GetMapping("/schedule")
    public String scheduleTab() {
        return "socialgroup/schedule";
    }

    /**
     * 앨범 탭 클릭 시 앨범 HTML 반환
     */
    @GetMapping("/album")
    public String albumTab() {
        return "socialgroup/album";
    }
    
    /**
     * 앨범 탭 포스트 업로드
     */ 
    @PostMapping("/uploadPost")
    public ResponseEntity<?> uploadPost(
        @RequestParam("photo") MultipartFile photo,
        @RequestParam("description") String description,
        @RequestParam("groupId") Integer groupId,
        @AuthenticationPrincipal AuthenticatedUser user) {

    	try {
            // 포스트 업로드 서비스 호출
            Integer postId = groupBoardService.uploadPost(photo, description, groupId, user);

            // 성공적으로 업로드한 포스트의 postId를 반환
            Map<String, Object> response = new HashMap<>();
            response.put("message", "포스트 업로드 성공");
            response.put("postId", postId);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            // IOException이 발생할 경우, 적절한 에러 메시지를 반환
            log.error("포스트 업로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("포스트 업로드 중 오류가 발생했습니다.");
        }
    }

    /**
     * 그룹 ID로 포스트 리스트 가져오기
     */
    @GetMapping("/album/photos")
    public ResponseEntity<List<PhotoDTO>> getPhotosByGroupId(@RequestParam("groupId") Integer groupId) {
        List<PhotoDTO> photos = groupBoardService.getPhotosByGroupId(groupId);
        
        photos.forEach(photo -> {
            String imageUrl = "/kkirikkiri/upload/" + photo.getImageName();  // 저장된 파일 이름에 경로 추가
            photo.setImageName(imageUrl);  // PhotoDTO에 imageUrl 필드를 추가해야 함
        });
        
        return new ResponseEntity<>(photos, HttpStatus.OK);
    }
    
	/**
	 * 사진 클릭 시 포스트 상세 정보 가져오기
	 */
    @GetMapping("/postDetail/{postId}")
    public ResponseEntity<PostDTO> getPostDetail(
    		@PathVariable("postId") Integer postId) {
        PostDTO postDetail = groupBoardService.getPostDetail(postId);
        
        // PostDTO에 이미지를 표시할 경로를 추가
        postDetail.setImageName("/kkirikkiri/upload/" + postDetail.getImageName());
        
        return ResponseEntity.ok(postDetail);
    }
    
    /**
     * 그룹 설정 불러오기 (헤더 이미지 포함)
     */
    @GetMapping("/settings")
    public String getGroupSettings(
    		@RequestParam("groupId") Integer groupId,
    		Model model) {
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        // 날짜 포맷 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = group.getEventDate().format(formatter);

        // 그룹 헤더 이미지 설정 (기본값이 없으면 default 이미지로)
        String groupProfileImage = (group.getProfileImage() != null) ? group.getProfileImage() : "noImage_icon.png";
        
        // 해당 그룹의 해시태그 리스트 조회
        List<GroupHashtagEntity> hashtags = groupHashtagRepository.findByGroup(group);

        // 모델에 그룹, 이벤트 날짜, 헤더 이미지 및 해시태그 추가
        model.addAttribute("group", group);
        model.addAttribute("eventDateFormatted", formattedDate);
        model.addAttribute("groupProfileImage", groupProfileImage);
        model.addAttribute("hashtags", hashtags);

        return "socialgroup/settings";  // settings.html로 이동
    }

    
    // 해시태그 리스트 조회
    @GetMapping("/socialgroup/{groupId}/hashtags")
    public String getGroupHashtags(@PathVariable Integer groupId, Model model) {
        // 그룹 조회
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다: " + groupId));

        // 해당 그룹의 해시태그 리스트 조회
        List<GroupHashtagEntity> hashtags = groupHashtagRepository.findByGroup(group);

        // 모델에 그룹과 해시태그 리스트를 추가
        model.addAttribute("group", group);
        model.addAttribute("hashtags", hashtags);

        // 뷰로 데이터 전달 (groupDetails.html로 이동)
        return "groupDetails";  // 해당 페이지로 전달
    }
    
    
    /**
     * 설정 탭
     * 설정 업데이트
     */
    @PostMapping("/update")
    public String updateGroup(
            @RequestParam("groupId") Integer groupId,
            @RequestParam("groupName") String groupName,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("eventDate") String eventDate,
            @RequestParam("interest") String interest,
            @RequestParam("joinMethod") String joinMethod,
            @RequestParam("memberLimit") Integer memberLimit,
            @RequestParam("hashtags") String hashtags
            ) {

        // 그룹 정보 업데이트 처리 로직
        groupBoardService.updateGroup(groupId, groupName, description, location, eventDate, interest, joinMethod, memberLimit, hashtags);
        
        // 해당 그룹의 설정 페이지로 리다이렉트
        return "redirect:/socialgroup/settings?groupId=" + groupId;
    
    }

}
