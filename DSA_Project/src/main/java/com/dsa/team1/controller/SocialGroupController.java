package com.dsa.team1.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dsa.team1.dto.SocialGroupDTO;
import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.repository.GroupHashtagRepository;
import com.dsa.team1.repository.SocialGroupRepository;
import com.dsa.team1.repository.UserGroupRepository;
import com.dsa.team1.repository.UserRepository;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.SocialGroupService;
import com.dsa.team1.service.UserService;

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
            @RequestParam("interest") List<String> interest,			 // 관심사
            @RequestParam("groupName") String groupName,                 // 그룹 이름
            @RequestParam("eventDate") String eventDate,                 // 이벤트 날짜 (yyyy-MM-dd)
            @RequestParam("description") String description,             // 그룹 설명
            @RequestParam("location") String location,                   // 위치
            @RequestParam("joinMethod") String joinMethod,		 		 // 가입 권한
            @RequestParam("memberLimit") Integer memberLimit,            // 인원 제한
            @RequestParam("hashtags") String hashtags,                   // 해시태그
            @RequestParam("profileImage") MultipartFile profileImage, 	 // 프로필 이미지
            @AuthenticationPrincipal AuthenticatedUser user,             // 인증된 사용자
            RedirectAttributes redirectAttributes) {
    	
    	// AuthenticatedUser 사용자 정보 체크
    	if (user == null) {
    	    redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 그룹을 생성할 수 있습니다.");
    	    return "redirect:/member/loginForm";
    	}
    	
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	LocalDateTime eventDateTime = LocalDate.parse(eventDate, formatter).atStartOfDay();
        
    	// 대문자로 변환 후 Enum으로 변환
    	GroupJoinMethod groupJoinMethod = GroupJoinMethod.valueOf(joinMethod.toUpperCase());
    	
        // 해시태그 데이터 처리
        List<String> hashtagList = Arrays.stream(hashtags.split(","))
						        	     .map(String::trim)
						        	     .collect(Collectors.toList());

        // 그룹 생성
        try {
            socialGroupService.create(interest, groupName, eventDate, description, location, joinMethod, memberLimit, hashtagList, profileImage, user);
        } catch (IOException e) {
        	e.printStackTrace();
        }

        return "redirect:/socialgroup/socialing";
    }
    
    /**
     * 그룹 목록 조회 및 검색
     * @param query 검색어 (입력된 경우에만 처리, 없으면 전체 그룹을 조회)
     * @param model 모델 객체를 사용해 검색 결과를 뷰로 전달
     * @param user 현재 로그인한 사용자 정보 (로그인 상태에 따라 사용자 이름 표시)
     * @return socialing 페이지를 반환하여 그룹 목록을 보여줌
     */
    @GetMapping("/socialing")
    public String list(
    		Model model,
            @RequestParam(value = "query", required = false) String query,
            @AuthenticationPrincipal AuthenticatedUser user) {
    	
        log.info("Authenticated User: {}", user);

        List<SocialGroupEntity> groups;

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

            groups = nameOrDescriptionGroups;
            groups.addAll(hashtagGroups);
            groups = groups.stream().distinct().collect(Collectors.toList());
        } else {
            // 전체 그룹 조회
            groups = socialGroupRepository.findAll();
        }

        // 사용자 정보 추가
        if (user != null) {
            model.addAttribute("name", user.getName()); // 사용자 이름을 모델에 추가
        } else {
            model.addAttribute("name", null);
        }
        
        // 모든 해시태그를 가져와서 모델에 추가
        List<String> allHashtags = groupHashtagRepository.findAllHashtags();
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

        // 검색어도 뷰에 전달
        model.addAttribute("query", query); 

        return "socialgroup/socialing";
    }
    
    // 그룹에 속한 멤버 수를 계산하는 함수
    public int getMemberCountByGroup(SocialGroupEntity group) {
        // 그룹의 현재 활성화된 멤버 수 계산 (방장을 제외한 멤버 수)
        int memberCount = userGroupRepository.countActiveMembersByGroupId(group.getGroupId());
        
        // 방장은 무조건 포함하므로 1명을 더한다
        memberCount += 1;

        // 멤버 수가 그룹의 제한인원(memberLimit)을 넘지 않게 한다
        if (memberCount > group.getMemberLimit()) {
            memberCount = group.getMemberLimit();
        }

        return memberCount;
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
