package com.dsa.team1.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import com.dsa.team1.dto.ReplyDTO;
import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.PhotoEntity;
import com.dsa.team1.entity.PostEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.PostType;
import com.dsa.team1.repository.GroupHashtagRepository;
import com.dsa.team1.repository.PhotoRepository;
import com.dsa.team1.repository.PostRepository;
import com.dsa.team1.repository.ReplyRepository;
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
@RequestMapping("groupboard")
public class GroupBoardController {
	
	private final SocialGroupService socialGroupService;
	private final GroupBoardService groupBoardService;
    private final SocialGroupRepository socialGroupRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PostRepository postRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupHashtagRepository groupHashtagRepository;
    private final ReplyRepository replyRepository;
    
    /**
     * 그룹 보드 게시판 페이지로 이동
     * 조회수 카운트
     */
    @GetMapping("/main")
    public String main(
    		@RequestParam("groupId") Integer groupId,
    		@AuthenticationPrincipal AuthenticatedUser user,
    		Model model) {
    	
        // 해당 그룹을 조회
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 그룹 ID입니다."));
        
        // 기본 이미지 설정
        String headerImage = (group.getProfileImage() != null && !group.getProfileImage().isEmpty()) 
                ? group.getProfileImage() 
                : "default.png";
        
        // 멤버 수 가져오기
        int memberCount = groupBoardService.getMemberCount(groupId);
        model.addAttribute("memberCount", memberCount);
        
        // group 객체를 모델에 추가
        model.addAttribute("group", group);
        model.addAttribute("groupName", group.getGroupName());
        model.addAttribute("headerImage", headerImage);
        
        // 해시태그 가져오기
        List<GroupHashtagEntity> hashtags = groupHashtagRepository.findByGroup(group);
        List<String> hashtagNames = hashtags.stream()
                .map(GroupHashtagEntity::getName)
                .collect(Collectors.toList());
        model.addAttribute("hashtags", hashtagNames);
        
        // 조회수 증가
        if (group.getViewCount() == null) {
            group.setViewCount(0);
        }
        group.setViewCount(group.getViewCount() + 1);
        socialGroupRepository.save(group);
        
        // 멤버 목록 가져오기 (리더 제외)
        List<Map<String, String>> memberProfiles = groupBoardService.getMemberProfiles(groupId).stream()
            .filter(member -> !member.get("userId").equals(group.getGroupLeader().getUserId()))  // 리더 제외
            .collect(Collectors.toList());
        model.addAttribute("members", memberProfiles);
        
        // 리더 정보 가져오기
        UserEntity leader = groupBoardService.getGroupLeader(groupId);
        model.addAttribute("leader", leader);

        // 리더의 나이를 계산하여 모델에 추가
        int leaderAge = groupBoardService.calculateUserAge(leader);
        model.addAttribute("leaderAge", leaderAge > 0 ? leaderAge + "세" : "N/A");
        
        // 리더 위치 정보 추가 (위치가 없다면 "N/A")
        String leaderLocation = leader.getPreferredLocation() != null ? leader.getPreferredLocation() : "N/A";
        model.addAttribute("leaderLocation", leaderLocation);

        // 현재 로그인한 유저가 그룹의 멤버인지 확인
        boolean isMember = groupBoardService.isUserMemberOfGroup(user.getId(), groupId);
        model.addAttribute("isMember", isMember);
        
        return "groupboard/main";
//        // 그룹 멤버일 때와 비회원일 때 페이지 반환
//        if (isMember) {
//            model.addAttribute("group", group);
//            model.addAttribute("groupLeaderId", group.getGroupLeader().getUserId());
//            return "groupboard/main";
//        } else {
//            model.addAttribute("group", group);
//            return "socialgroup/joinGroupInvitation";
//        }
        
    }
    
    /**
     * 공지사항 탭으로 이동
     * 공지사항 목록 조회
     */
    @GetMapping("/announcement")
    public String announcementPage(
        @RequestParam("groupId") Integer groupId,
        @AuthenticationPrincipal AuthenticatedUser user,
        Model model) {
    	
    	// 그룹 객체 조회
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));

        model.addAttribute("group", group);
	    model.addAttribute("groupId", groupId);
	    
	    // 멤버 목록 가져오기 (리더 제외)
        List<Map<String, String>> memberProfiles = groupBoardService.getMemberProfiles(groupId).stream()
            .filter(member -> !member.get("userId").equals(group.getGroupLeader().getUserId()))  // 리더 제외
            .collect(Collectors.toList());
        model.addAttribute("members", memberProfiles);
        
        // 리더 정보 가져오기
        UserEntity leader = groupBoardService.getGroupLeader(groupId);
        model.addAttribute("leader", leader);
	    
	    return "groupboard/announcement";
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
            .postType(PostType.NOTIFICATION)
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
     * 공지사항 리스트 반환
     */
    @GetMapping("/announcement/list")
    public ResponseEntity<List<PostDTO>> getAnnouncements(
    		@RequestParam("groupId") Integer groupId) {
    	
    	// **PostType이 NOTIFICATION인 항목만 조회**
        List<PostEntity> announcements = postRepository.findByGroup_GroupIdAndPostType(groupId, PostType.NOTIFICATION);

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

        // 반드시 JSON 배열 형태로 반환
        return ResponseEntity.ok(announcementDTOs);
    }

    /**
     * 일정탭으로 이동
     */
    @GetMapping("/schedule")
    public String scheduleTab(
    		@RequestParam("groupId") Integer groupId,
    		@AuthenticationPrincipal AuthenticatedUser user,
    		Model model) {
    	
    	// 그룹 객체 조회
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));
        model.addAttribute("group", group);
	    model.addAttribute("groupId", groupId);
	    
	    // 멤버 목록 가져오기 (리더 제외)
        List<Map<String, String>> memberProfiles = groupBoardService.getMemberProfiles(groupId).stream()
            .filter(member -> !member.get("userId").equals(group.getGroupLeader().getUserId()))  // 리더 제외
            .collect(Collectors.toList());
        model.addAttribute("members", memberProfiles);
        
        // 리더 정보 가져오기
        UserEntity leader = groupBoardService.getGroupLeader(groupId);
        model.addAttribute("leader", leader);

	    // 날짜 포맷 설정
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm");
	    String formattedDateTime = group.getEventDate().format(formatter);
	    model.addAttribute("eventDateFormatted", formattedDateTime);
	    
        return "groupboard/schedule";
    }
    
    /**
     * 일정 업데이트 
     */
    @PostMapping("/schedule/update")
    public ResponseEntity<?> updateSchedule(
            @RequestParam("groupId") Integer groupId,
            @RequestParam("eventDate") String eventDate,
            @RequestParam("location") String location) {

        SocialGroupEntity group = socialGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 그룹 ID입니다."));

        // 날짜 포맷 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        group.setEventDate(LocalDateTime.parse(eventDate, formatter));
        group.setLocation(location);
        socialGroupRepository.save(group);

        return ResponseEntity.ok("일정이 성공적으로 업데이트되었습니다.");
    }

    /**
     * 앨범탭으로 이동
     */
    @GetMapping("/album")
    public String albumTab(
    		@RequestParam("groupId") Integer groupId,
    		@AuthenticationPrincipal AuthenticatedUser user,
    		Model model
    		) {
    	
    	// 그룹 객체 조회
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));
    	
    	// groupId를 모델에 추가하여 Thymeleaf 템플릿에서 사용 가능하도록 설정
        model.addAttribute("groupId", groupId);
        
        // 필요한 경우, 기본 사진 데이터를 미리 조회하여 모델에 추가할 수 있습니다.
        List<PhotoDTO> photos = photoRepository.findByGroup_GroupId(groupId)
            .stream()
            .map(photo -> PhotoDTO.builder()
                .photoId(photo.getPhotoId())
                .imageName("/kkirikkiri/upload/" + photo.getImageName())
                .postId(photo.getPost() != null ? photo.getPost().getPostId() : null)
                .build())
            .collect(Collectors.toList());

        model.addAttribute("photos", photos);
        model.addAttribute("group", group);
        
        // 멤버 목록 가져오기 (리더 제외)
        List<Map<String, String>> memberProfiles = groupBoardService.getMemberProfiles(groupId).stream()
            .filter(member -> !member.get("userId").equals(group.getGroupLeader().getUserId()))  // 리더 제외
            .collect(Collectors.toList());
        model.addAttribute("members", memberProfiles);
        
        // 리더 정보 가져오기
        UserEntity leader = groupBoardService.getGroupLeader(groupId);
        model.addAttribute("leader", leader);
    	
        return "groupboard/album";
    }
    
    /**
     * 앨범 탭 포스트 업로드
     */ 
    @PostMapping("/album/uploadPost")
    public ResponseEntity<?> uploadPost(
        @RequestParam("photo") MultipartFile photo,
        @RequestParam("content") String content,
        @RequestParam("groupId") Integer groupId,
        @AuthenticationPrincipal AuthenticatedUser user) {

    	try {
            // 포스트 업로드 서비스 호출
            Integer postId = groupBoardService.uploadPost(photo, content, groupId, user);

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
     *  그룹 ID로 포스트 리스트 가져오기
     */
    @GetMapping("/album/photos")
    public ResponseEntity<List<PhotoDTO>> getPhotosByGroupId(
        @RequestParam("groupId") Integer groupId,
        @RequestParam("type") String type) {
        
    	// 그룹 ID로 사진을 조회하는 로직을 수행
        List<PhotoDTO> photos = photoRepository.findByGroup_GroupId(groupId)
            .stream()
            .map(photo -> PhotoDTO.builder()
                .photoId(photo.getPhotoId())
                .imageName("/kkirikkiri/upload/" + photo.getImageName())
                .postId(photo.getPost() != null ? photo.getPost().getPostId() : null)
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok(photos);
    	
    }
    
    /**
     * postId로 게시글 상세보기 
     */
    @GetMapping("/album/postDetail/{postId}")
    public ResponseEntity<PostDTO> getPostDetail(
    		@PathVariable("postId") Integer postId) {
        
        try {
        	// postId로 게시글 조회
            PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
            
            // 게시글에 연결된 사진 조회
            List<PhotoDTO> photoDTOs = photoRepository.findByPost_PostId(postId)
                .stream()
                .map(photo -> PhotoDTO.builder()
                    .photoId(photo.getPhotoId())
                    .imageName("/kkirikkiri/upload/" + photo.getImageName())
                    .build())
                .collect(Collectors.toList());

            // user를 명시적으로 초기화
            String userId = post.getUser().getUserId();
            String content = post.getContent();
            LocalDateTime createdAt = post.getCreatedAt();

            PostDTO postDTO = PostDTO.builder()
                    .postId(post.getPostId())
                    .userId(userId)
                    .content(content)
                    .createdAt(post.getCreatedAt())
                    .photos(photoDTOs)
                    .build();

            return ResponseEntity.ok(postDTO);
        } catch (Exception e) {
            log.error("postDetail 조회 중 오류 발생: postId = " + postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * photoId로 게시글 상세보기
     */
    @GetMapping("/album/photoDetail/{photoId}")
    public ResponseEntity<PostDTO> getPostByPhotoId(@PathVariable("photoId") Integer photoId) {
        try {
            // photoId로 PhotoEntity를 조회
            PhotoEntity photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사진을 찾을 수 없습니다. photoId: " + photoId));

            // PhotoEntity에 연결된 PostEntity 가져오기
            PostEntity post = photo.getPost();
            if (post == null) {
                throw new IllegalArgumentException("해당 사진에 연결된 게시글이 없습니다.");
            }

            // PostDTO로 변환
            PostDTO postDTO = PostDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .photos(List.of(PhotoDTO.builder()
                    .photoId(photo.getPhotoId())
                    .imageName("/kkirikkiri/upload/" + photo.getImageName())
                    .build()))
                .build();

            return ResponseEntity.ok(postDTO);
        } catch (Exception e) {
            log.error("photoDetail 조회 중 오류 발생. photoId: " + photoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * 댓글 작성 
     */
    @PostMapping("/post/{postId}/reply")
    public ResponseEntity<?> addReply(
        @PathVariable("postId") Integer postId,
        @RequestParam("content") String content,
        @AuthenticationPrincipal AuthenticatedUser user) {
        
        ReplyDTO replyDTO = ReplyDTO.builder()
                .postId(postId)
                .content(content)
                .build();
        
        groupBoardService.addReply(replyDTO, user);
        
        return ResponseEntity.ok("댓글 작성 성공");
    }
    
    /**
     * 댓글 수정 
     */
    @PostMapping("/reply/{replyId}/edit")
    public ResponseEntity<?> editReply(
        @PathVariable("replyId") Integer replyId,
        @RequestParam("content") String content) {
        
        try {
            groupBoardService.editReply(ReplyDTO.builder().replyId(replyId).content(content).build());
            return ResponseEntity.ok("댓글 수정 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 중 오류 발생");
        }
    }
    
    /**
     * 댓글 삭제 
     */
    @PostMapping("/reply/{replyId}/delete")
    public ResponseEntity<?> deleteReply(@PathVariable("replyId") Integer replyId) {
        try {
            groupBoardService.deleteReply(replyId);
            return ResponseEntity.ok("댓글 삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류 발생");
        }
    }
    
    /**
     * Post에 대한 댓글을 따로 가져오는 메서드
     */
    @GetMapping("/post/{postId}/replies")
    public ResponseEntity<List<ReplyDTO>> getRepliesByPostId(
        @PathVariable("postId") Integer postId) {
    	
    	List<ReplyDTO> replies = replyRepository.findByPost_PostId(postId).stream()
    	        .map(reply -> ReplyDTO.builder()
    	            .replyId(reply.getReplyId())
    	            .postId(reply.getPost().getPostId())
    	            .userId(reply.getUser().getUserId())
    	            .content(reply.getContent())
    	            .createdAt(reply.getCreatedAt()) // LocalDateTime 그대로 사용
    	            .build())
    	        .collect(Collectors.toList());

    	    return ResponseEntity.ok(replies);
    }
    
    /**
     * 그룹 설정 불러오기
     */
    @GetMapping("/settings")
    public String getGroupSettings(
    		@RequestParam("groupId") Integer groupId,
    		@AuthenticationPrincipal AuthenticatedUser user,
    		Model model) {
    	
    	// 그룹 객체 조회
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        // 그룹 헤더 이미지 설정 (기본값이 없으면 default 이미지로)
        String groupProfileImage = (group.getProfileImage() != null) ? group.getProfileImage() : "noImage_icon.png";
        
        // 해당 그룹의 해시태그 리스트 조회
        List<GroupHashtagEntity> hashtags = groupHashtagRepository.findByGroup(group);

        // 모델에 그룹, 이벤트 날짜, 헤더 이미지 및 해시태그 추가
        model.addAttribute("group", group);
	    model.addAttribute("groupId", groupId);
        model.addAttribute("groupProfileImage", groupProfileImage);
        model.addAttribute("hashtags", hashtags);
        
        // 멤버 목록 가져오기 (리더 제외)
        List<Map<String, String>> memberProfiles = groupBoardService.getMemberProfiles(groupId).stream()
            .filter(member -> !member.get("userId").equals(group.getGroupLeader().getUserId()))  // 리더 제외
            .collect(Collectors.toList());
        model.addAttribute("members", memberProfiles);
        
        // 리더 정보 가져오기
        UserEntity leader = groupBoardService.getGroupLeader(groupId);
        model.addAttribute("leader", leader);

        return "groupboard/settings";
    }
    
    /**
     * 해시태그 리스트 조회
     */
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
        return "groupDetails";

    }
    
    /**
     * 설정 탭
     * 설정 업데이트
     */
    @PostMapping("/settings/update")
    public ResponseEntity<?> updateGroup(
            @RequestParam("groupId") Integer groupId,
            @RequestParam("groupName") String groupName,
            @RequestParam("description") String description,
            @RequestParam("interest") String interest,
            @RequestParam("joinMethod") String joinMethod,
            @RequestParam("memberLimit") Integer memberLimit,
            @RequestParam("removedMembers") String removedMembers, // 추가된 삭제된 멤버 리스트
            @RequestParam("hashtags") String hashtags,  // 추가된 해시태그
            @RequestParam(value = "removedHashtags", required = false) String removedHashtags,  // 삭제된 해시태그
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

    	try {
            // 그룹 정보 업데이트
            groupBoardService.updateGroup(groupId, groupName, description, interest, joinMethod, memberLimit, removedMembers, hashtags, removedHashtags, profileImage);

            SocialGroupEntity group = socialGroupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));

            // 삭제된 해시태그 처리 (removedHashtags가 있으면)
            if (removedHashtags != null && !removedHashtags.trim().isEmpty()) {
                String[] removedHashtagArray = removedHashtags.split(",");
                for (String removedHashtag : removedHashtagArray) {
                    if (!removedHashtag.trim().isEmpty()) {
                        // 그룹에 해당하는 해시태그만 삭제
                        groupHashtagRepository.deleteByGroupAndName(group, removedHashtag.trim());
                    }
                }
            }

            // 새로운 해시태그 추가
            if (hashtags != null && !hashtags.trim().isEmpty()) {
                String[] hashtagArray = hashtags.split(",");
                for (String hashtag : hashtagArray) {
                	// DB에 저장할 때는 # 기호를 제거한 상태로 저장
                	String cleanHashtag = hashtag.trim().replaceAll("^#+", "");
                	if (!cleanHashtag.isEmpty() && !groupHashtagRepository.existsByGroupAndName(group, cleanHashtag)) {
                	    GroupHashtagEntity hashtagEntity = GroupHashtagEntity.builder()
                	        .group(group)
                	        .name(cleanHashtag)
                	        .build();
                	    groupHashtagRepository.save(hashtagEntity);  // 해시태그 저장
                	}
                }
            }


            return ResponseEntity.ok("그룹 설정이 성공적으로 업데이트되었습니다.");
        } 
    	catch (IOException e) {
            log.error("그룹 설정 업데이트 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("그룹 설정 업데이트 중 오류가 발생했습니다.");
        }
    }

    /**
     * 그룹헤더이미지 관리 
     */
    @PostMapping("/settings")
    public ResponseEntity<?> uploadImage(@RequestParam("groupId") Integer groupId,
                                          @RequestParam("profileImage") MultipartFile profileImage) {
    	if (profileImage.isEmpty()) {
            return ResponseEntity.badRequest().body("업로드할 이미지가 없습니다.");
        }

        try {
            // 파일 이름 생성 (UUID 사용 예시)
            String originalFileName = profileImage.getOriginalFilename();
            String savedFileName = UUID.randomUUID() + "_" + originalFileName; // 고유한 파일 이름 생성

            // 파일 저장 경로 설정
            Path path = Paths.get("C:/upload/" + savedFileName);
            Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING); // 파일 저장

            // 저장된 파일의 URL을 생성
            String imageUrl = "/kkirikkiri/upload/" + savedFileName;

            // 응답으로 이미지 URL 반환
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 중 오류가 발생했습니다.");
        }
    }




}
