package com.dsa.team1.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.repository.BookmarkRepository;
import com.dsa.team1.repository.GroupHashtagRepository;
import com.dsa.team1.repository.SocialGroupRepository;
import com.dsa.team1.repository.UserGroupRepository;
import com.dsa.team1.repository.UserRepository;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.SocialGroupService;
import com.dsa.team1.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("socialgroup")
public class SocialGroupController {
    
    private final SocialGroupService socialGroupService;
    private final SocialGroupRepository socialGroupRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupHashtagRepository groupHashtagRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final NotificationService notificationService;
    
    @Value("${socialgroup.pageSize}")
    int pageSize;

    @Value("${socialgroup.linkSize}")
    int linkSize;

    @Value("${socialgroup.uploadPath}")
    String uploadPath;

    /**
     * 그룹 생성폼 진입 
     */
    @GetMapping("create")
    public String create() {
        return "socialgroup/createForm";
    }

    /**
     * 그룹 생성 
     */
    @PostMapping("create")
    public String create(
            @RequestParam("interest") List<Interest> interestList,		 // List<Interest>로 받음
            @RequestParam("groupName") String groupName,                 // 그룹 이름
            @RequestParam("eventDate") String eventDate,                 // 이벤트 날짜 (yyyy-MM-dd)
            @RequestParam("description") String description,             // 그룹 설명
            @RequestParam("location") String location,                   // 위치
            @RequestParam("joinMethod") String joinMethod,		 		 // 가입 권한
            @RequestParam("memberLimit") Integer memberLimit,            // 인원 제한
            @RequestParam("hashtags") String hashtags,                   // 해시태그
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage, 	 // 프로필 이미지
            @AuthenticationPrincipal AuthenticatedUser user,             // 인증된 사용자
            RedirectAttributes redirectAttributes) {
    	
    	// AuthenticatedUser 사용자 정보 체크
    	if (user == null) {
    	    redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 그룹을 생성할 수 있습니다.");
    	    return "redirect:/member/loginForm";
    	}
    	
    	// Interest Enum을 String으로 변환
        List<String> interestAsString = interestList.stream()
                .map(Interest::name)  // Enum의 이름(String)으로 변환
                .collect(Collectors.toList());
    	
        // 이벤트 날짜 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime eventDateTime = LocalDateTime.parse(eventDate, formatter);

    	// 가입 방법 Enum 변환
    	GroupJoinMethod groupJoinMethod = GroupJoinMethod.valueOf(joinMethod.toUpperCase());
        
        // 해시태그 데이터 처리
        List<String> hashtagList = Arrays.stream(hashtags.split(","))
						        	     .map(String::trim)
						        	     .collect(Collectors.toList());

        // 그룹 생성
        try {
            socialGroupService.create(interestAsString, groupName, eventDateTime, description, location, joinMethod, memberLimit, hashtagList, profileImage, user);
        } catch (IOException e) {
        	e.printStackTrace();
        }

        return "redirect:/socialgroup/socialing";
    }
    
    /**
     * 그룹 목록 조회 
     */
    @GetMapping("/socialing")
    public String list(
    		Model model,
    		@RequestParam(value = "query", required = false) String query,
    		@RequestParam(value = "sort", required = false) String sort,
    		@AuthenticationPrincipal AuthenticatedUser user) {
    	
    	// 그룹 목록을 저장할 변수 선언
        List<SocialGroupEntity> groups;
    	
    	// 검색어가 있을 경우 필터링된 결과 조회, 없을 경우 전체 그룹 조회
        if (query != null && !query.trim().isEmpty()) {
            groups = socialGroupService.searchGroups(query, null, null, sort); // 검색된 그룹 목록
        } else {
            groups = socialGroupRepository.findAll(); // 전체 그룹 목록 조회
        }
        
        // 정렬 옵션에 따라 그룹 목록을 정렬
        if ("mostViewed".equals(sort)) {
            groups.sort((g1, g2) -> g2.getViewCount().compareTo(g1.getViewCount()));
        } else if ("mostBookmarked".equals(sort)) {
            groups.sort((g1, g2) -> g2.getBookmarkCount().compareTo(g1.getBookmarkCount()));
        } else if ("upcomingEvents".equals(sort)) {
            groups.sort((g1, g2) -> {
                if (g1.getEventDate() == null) return 1;
                if (g2.getEventDate() == null) return -1;
                return g1.getEventDate().compareTo(g2.getEventDate());
            });
        }
        
        // 사용자 정보 추가
        if (user != null) {
            model.addAttribute("name", user.getName());
        } else {
            model.addAttribute("name", null);
        }
        
        // 사용자별 북마크 상태 추가
        Map<Integer, Boolean> bookmarkedMap = new HashMap<>();
        if (user != null) {
            UserEntity currentUser = userRepository.findById(user.getId()).get(); // 사용자 엔티티 조회
            for (SocialGroupEntity group : groups) {
            	Boolean isBookmarked = bookmarkRepository.existsByUserAndGroup(currentUser, group); // 북마크 여부 확인
                // 만약 북마크 상태가 없는 경우 기본값을 false로 설정
                bookmarkedMap.put(group.getGroupId(), isBookmarked != null ? isBookmarked : false); // null 체크 후 값 설정
            }
        }
        
        // 모든 해시태그를 가져와서 중복 제거 후 전달
        Set<String> allHashtags = new HashSet<>(groupHashtagRepository.findAllHashtags());	// Set으로 중복 해시태그 제거
        model.addAttribute("allHashtags", allHashtags);
        
        // 그룹 목록 및 멤버 수 계산
        Map<Integer, Integer> memberCountMap = new HashMap<>();
        for (SocialGroupEntity group : groups) {
            int memberCount = getMemberCountByGroup(group);
            memberCountMap.put(group.getGroupId(), memberCount);
        }

        // 모델에 그룹 목록, 멤버 수, 북마크 상태 추가
        model.addAttribute("groups", groups);
        model.addAttribute("memberCountMap", memberCountMap);
        model.addAttribute("bookmarkedMap", bookmarkedMap);
        
        // 프로필 이미지 추가
        List<String> profileImages = groups.stream()
                .map(SocialGroupEntity::getProfileImage)
                .map(image -> (image != null) ? image : "noImage_icon.png")
                .collect(Collectors.toList());
        model.addAttribute("profileImages", profileImages);

        // 전체 그룹을 socialing.html 페이지로 반환
        return "socialgroup/socialing";
    }
    
    /**
     * 그룹에 속한 멤버 수를 계산하는 메서드 추가
     */
    public int getMemberCountByGroup(SocialGroupEntity group) {
        // 그룹의 현재 활성화된 멤버 수 계산 (방장을 제외한 멤버 수)
        int memberCount = userGroupRepository.countActiveMembersByGroupId(group.getGroupId());
        
        // 방장은 무조건 포함하므로 1명을 더합니다.
        memberCount += 1;

        // 멤버 수가 그룹의 제한인원(memberLimit)을 넘지 않도록 합니다.
        if (memberCount > group.getMemberLimit()) {
            memberCount = group.getMemberLimit();
        }

        return memberCount;
    }

    /**
     * 검색, 카테고리, 지역, 해시태그 필터링
     */    
    @GetMapping("/filter")
    public String filterGroups(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "sort", required = false) String sort,
            Model model) {

        List<SocialGroupEntity> groups = new ArrayList<>();

        // 검색어가 있는 경우 처리
        if (query != null && !query.trim().isEmpty()) {
            // 그룹 이름 또는 설명에 검색어가 포함된 그룹을 찾음
            List<SocialGroupEntity> nameOrDescriptionGroups = socialGroupRepository
                    .findByGroupNameContainingOrDescriptionContaining(query, query);

            // 해시태그에 검색어가 포함된 그룹을 찾음
            List<SocialGroupEntity> hashtagGroups = groupHashtagRepository
                    .findByNameContaining(query)
                    .stream()
                    .map(GroupHashtagEntity::getGroup)
                    .collect(Collectors.toList());

            // 검색된 그룹 리스트를 합침
            groups = nameOrDescriptionGroups;
            groups.addAll(hashtagGroups);
            groups = groups.stream().distinct().collect(Collectors.toList()); // 중복 제거
        } else {
            // 검색어가 없으면 전체 그룹 조회
            groups = socialGroupRepository.findAll();
        }

        // 카테고리 및 지역 필터링 적용
        Interest interestCategory = null;
        if (category != null && !category.isEmpty()) {
            try {
                interestCategory = Interest.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.error("유효하지 않은 카테고리 값: {}", category);
            }
        }

        if (interestCategory != null || (location != null && !location.isEmpty())) {
            final Interest finalInterestCategory = interestCategory;

            // 지역 필터링
            if (location != null && !location.isEmpty()) {
                List<String> locationList = Arrays.asList(location.split(",")); // 콤마로 구분된 지역 목록

                groups = groups.stream()
                    .filter(group -> {
                        // 그룹의 위치를 비문자 문자로 분할
                        List<String> groupLocations = Arrays.asList(group.getLocation().split("[^\\p{L}]+"));

                        // 그룹의 위치 단어 리스트와 선택된 지역명 리스트에 공통된 단어가 있는지 확인
                        for (String loc : locationList) {
                            if (groupLocations.contains(loc)) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            }

            // 카테고리 필터링
            groups = groups.stream()
                    .filter(group -> (finalInterestCategory == null || group.getInterest() == finalInterestCategory))
                    .collect(Collectors.toList());
        }

        // 빈 location 값 처리
        location = (location != null && !location.trim().isEmpty()) ? location : null;

        // 필터링된 그룹 목록 가져오기
        groups = socialGroupRepository.filterGroups(query, null, location, interestCategory, sort);

        // 각 그룹의 인원 수 계산하여 모델에 추가
        Map<Integer, Integer> memberCountMap = new HashMap<>();
        for (SocialGroupEntity group : groups) {
            int memberCount = socialGroupService.getMemberCountByGroup(group);
            memberCountMap.put(group.getGroupId(), memberCount);
        }

        // 모델에 필요한 데이터 추가
        model.addAttribute("groups", groups);
        model.addAttribute("memberCountMap", memberCountMap);
        model.addAttribute("allHashtags", groupHashtagRepository.findAllHashtags());

        return "socialgroup/socialing";
    }
    
    /**
     * 북마크 토글
     */
    @PostMapping("/bookmark/toggle")
    public ResponseEntity<String> toggleBookmark(
    		@RequestParam("groupId") Integer groupId,
    		@AuthenticationPrincipal AuthenticatedUser user) {
    	
    	log.info("Received groupId: {}", groupId);
    	
        if (user == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        UserEntity userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        socialGroupService.toggleBookmark(userEntity, group);

        return ResponseEntity.ok(group.getBookmarkCount().toString());  // 변경된 북마크 수를 반환
    }
    
    /**
     * 그룹 가입 권유 페이지로 이동하는 메서드
     * 그룹 멤버가 아닌 사용자가 그룹에 가입할 때, 권유 페이지로 리다이렉트됨.
     */
    @GetMapping("/joinGroupInvitation")
    public String joinGroupInvitation(
        @RequestParam("groupId") Integer groupId,
        Model model) {
        
        // 그룹 정보 로드
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 그룹 ID입니다."));
        
        model.addAttribute("group", group);
        return "socialgroup/joinGroupInvitation";  // 템플릿 반환
    }

    /**
     * 가입 권유 페이지 로직
     * 바로 가입(AUTO) / 승인 후 가입(APPROVAL)
     */
    @PostMapping("/joinGroup")
    public ResponseEntity<Map<String, String>> joinGroup(
            @RequestParam("groupId") Integer groupId,
            @AuthenticationPrincipal AuthenticatedUser user,
            RedirectAttributes redirectAttributes) {
    	
    	Map<String, String> response = new HashMap<>();

        SocialGroupEntity group = socialGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 그룹 ID입니다."));
        
        // 이미 그룹에 가입된 멤버인지 확인
        boolean isMember = socialGroupService.isUserMemberOfGroup(String.valueOf(user.getId()), groupId);
        if (isMember) {
            response.put("errorMessage", "이미 그룹의 멤버입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        UserEntity groupLeader = group.getGroupLeader();
        String message = user.getUsername() + "님이 '"+ group.getGroupName() +"' 그룹에 가입하였습니다.";
        
        // 바로 가입(AUTO)에 따른 로직 처리
        if (group.getGroupJoinMethod() == GroupJoinMethod.AUTO) {
            socialGroupService.addMemberToGroup(user.getId(), groupId);
            notificationService.sendNotification(groupLeader, message);	// 알림 추가
            response.put("successMessage", "그룹에 성공적으로 가입되었습니다.");
            return ResponseEntity.ok(response);
        // 승인 후 가입(APPROVAL)에 따른 로직 처리
        } else if (group.getGroupJoinMethod() == GroupJoinMethod.APPROVAL) {
        	// 그룹 리더에게 가입 요청 알림
            socialGroupService.requestApprovalToJoinGroup(user.getId(), groupId);
            response.put("infoMessage", "그룹 리더의 승인이 필요합니다.");
            return ResponseEntity.ok(response);
        }
        response.put("errorMessage", "가입할 수 없습니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        
    }
    
    /**
     * 그룹리더의 가입 승인/거절 처리 
     */
    @PostMapping("/approveJoinRequest")
    public ResponseEntity<String> approveJoinRequest(
            @RequestParam("userId") Integer userId,
            @RequestParam("groupId") Integer groupId,
            @RequestParam("action") String action, // 승인 or 거절
            @AuthenticationPrincipal AuthenticatedUser leader) {

        // 그룹 리더 확인 및 승인/거절 처리
        SocialGroupEntity group = socialGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
        
        if (!group.getGroupLeader().getUserId().equals(leader.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("그룹 리더만 승인/거절할 수 있습니다.");
        }

        if (action.equals("approve")) {
            socialGroupService.addMemberToGroup(userId, groupId);
            return ResponseEntity.ok("가입 요청이 승인되었습니다.");
        } else if (action.equals("reject")) {
            socialGroupService.rejectJoinRequest(userId, groupId);
            return ResponseEntity.ok("가입 요청이 거절되었습니다.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
    }
    
}