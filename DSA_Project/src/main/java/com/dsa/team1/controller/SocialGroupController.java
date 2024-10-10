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
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.repository.GroupHashtagRepository;
import com.dsa.team1.repository.SocialGroupRepository;
import com.dsa.team1.repository.UserGroupRepository;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.SocialGroupService;

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
    
    @Value("${socialgroup.pageSize}")
    int pageSize;

    @Value("${socialgroup.linkSize}")
    int linkSize;

    @Value("${socialgroup.uploadPath}")
    String uploadPath;

    @GetMapping("create")
    public String create() {
        return "socialgroup/createForm";
    }

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
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	LocalDateTime eventDateTime = LocalDate.parse(eventDate, formatter).atStartOfDay();
        
    	// 가입 방법 Enum 변환
    	GroupJoinMethod groupJoinMethod = GroupJoinMethod.valueOf(joinMethod.toUpperCase());
        
        // 해시태그 데이터 처리
        List<String> hashtagList = Arrays.stream(hashtags.split(","))
						        	     .map(String::trim)
						        	     .collect(Collectors.toList());

        // 그룹 생성
        try {
            socialGroupService.create(interestAsString, groupName, eventDate, description, location, joinMethod, memberLimit, hashtagList, profileImage, user);
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
    		@AuthenticationPrincipal AuthenticatedUser user) {
    	
//        // 전체 그룹 조회
//        List<SocialGroupEntity> groups = socialGroupRepository.findAll();
    	
    	// 그룹 목록을 저장할 변수 선언
        List<SocialGroupEntity> groups;
    	
    	// 검색어가 있을 경우 필터링된 결과 조회, 없을 경우 전체 그룹 조회
        if (query != null && !query.trim().isEmpty()) {
            log.info("검색어: {}", query);
            groups = socialGroupService.searchGroups(query, null, null); // 검색된 그룹 목록
            log.info("검색된 그룹 수: {}", groups.size());
        } else {
            groups = socialGroupRepository.findAll(); // 전체 그룹 목록
            log.info("전체 그룹 수: {}", groups.size());
        }
        
        // 사용자 정보 추가
        if (user != null) {
            model.addAttribute("name", user.getName());
        } else {
            model.addAttribute("name", null);
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

        // 모델에 그룹 목록 및 멤버 수 추가
        model.addAttribute("groups", groups);
        model.addAttribute("memberCountMap", memberCountMap);
        
        // 프로필 이미지 추가
        List<String> profileImages = groups.stream()
                .map(SocialGroupEntity::getProfileImage)
                .map(image -> (image != null) ? image : "noImage_icon.png")
                .collect(Collectors.toList());
        model.addAttribute("profileImages", profileImages);

        // 전체 그룹을 socialing.html 페이지로 반환
        return "socialgroup/socialing";
    }
    
    // 그룹에 속한 멤버 수를 계산하는 메서드 추가
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
     * 검색, 카테고리, 지역 필터링
     */
    @GetMapping("/filter")
    public String filterGroups(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "location", required = false) String location,
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
     * 그룹 게시판 페이지로 이동
     */
    @GetMapping("/groupBoard")
    public String groupBoard() {
        return "socialgroup/groupBoard";
    }

    /**
     * 공지사항 탭 클릭 시 공지사항 HTML 반환
     */
    @GetMapping("/announcement")
    public String announcementTab() {
        return "socialgroup/announcement";
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
     * 설정 탭 클릭 시 설정 HTML 반환
     */
    @GetMapping("/settings")
    public String settingsTab() {
        return "socialgroup/settings";
    }
    
    /**
     * 설정 탭에서 그룹 정보 수정 
     */
    @PostMapping("/settings")
    public String settingstab() {
    	return "socialgroup/settings";
    }
    
}
