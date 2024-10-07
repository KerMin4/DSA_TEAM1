package com.dsa.team1.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dsa.team1.dto.SocialGroupDTO;
import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.repository.GroupHashtagRepository;
import com.dsa.team1.repository.SocialGroupRepository;
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

    /**
     * 그룹 생성
     * @param socialGroupDTO 생성한 그룹 정보 (ModelAttribute로 받음)
     * @param upload 그룹 프로필 이미지 (첨부파일로 받음)
     * @param hashtags 그룹에 포함할 해시태그 (콤마로 구분된 문자열)
     * @param joinMethod 가입 승인 방식 (enum 타입으로 처리)
     * @param user 로그인한 사용자 정보 (AuthenticatedUser로 로그인 상태에서 받음)
     * @param redirectAttributes 리다이렉트 시 에러 메시지를 전달하기 위한 객체
     * @return socialing 페이지로 리디렉트
     */
    @PostMapping("create")
    public String create(
            @ModelAttribute SocialGroupDTO socialGroupDTO,
            @RequestParam(name = "upload", required = false) MultipartFile upload,
            @RequestParam(name = "hashtags", required = false) String hashtags,
            @RequestParam(name = "joinMethod") String joinMethod,  // joinMethod 값 받음
            @AuthenticationPrincipal AuthenticatedUser user,
            RedirectAttributes redirectAttributes) {

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 그룹을 생성할 수 있습니다.");
            return "redirect:/member/loginForm";
        }

        // 사용자 및 그룹 정보 설정
        socialGroupDTO.setGroupLeaderId(Integer.parseInt(user.getId()));
        socialGroupDTO.setGroupJoinMethod(GroupJoinMethod.valueOf(joinMethod));  // Enum 값 설정
        
        // 파일 업로드 처리
        if (upload != null && !upload.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + upload.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, fileName);
                Files.copy(upload.getInputStream(), filePath);
                socialGroupDTO.setProfileImage("/images/" + fileName); // 저장된 경로를 DTO에 설정
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "파일 업로드 중 문제가 발생했습니다.");
                return "redirect:/socialgroup/create";
            }
        }

        try {
            //그룹 생성
            socialGroupService.create(socialGroupDTO, uploadPath, upload, hashtags, GroupJoinMethod.valueOf(joinMethod));
            return "redirect:/socialgroup/socialing";
        } catch (Exception e) {
            e.printStackTrace();
            return "socialgroup/createForm";
        }
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
            @RequestParam(value = "query", required = false) String query,
            Model model,
            @AuthenticationPrincipal AuthenticatedUser user) {

        List<SocialGroupEntity> groups;

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
            groups = socialGroupRepository.findAll();
        }

        // 사용자 이름 전달
        if (user != null) {
            model.addAttribute("name", user.getUsername());
        }

        // 모든 해시태그를 가져와서 모델에 추가
        List<String> allHashtags = groupHashtagRepository.findAllHashtags();
        model.addAttribute("allHashtags", allHashtags);
        model.addAttribute("groups", groups);
        model.addAttribute("query", query); // 검색어도 뷰에 전달

        return "socialgroup/socialing";
    }
    
//    /**
//     * 전체 그룹 목록 반환 
//     */
//    @GetMapping("/socialgroup/viewAll")
//    public ResponseEntity<?> viewAllGroups() {
//        List<SocialGroupDTO> groups = socialGroupService.getAllGroups();
//        return ResponseEntity.ok(Map.of("groups", groups));
//    }

    /**
     * Ajax로 검색 및 필터링 결과를 반환하는 메서드
     */
    @GetMapping("/api/filter")
    public ResponseEntity<Map<String, String>> filterGroups(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "region", required = false) String region) {

        // 그룹 필터링 로직
        List<SocialGroupEntity> groups = socialGroupRepository.filterGroups(query, category, region);

        String html = groups.isEmpty() ? "<p>검색 결과가 없습니다.</p>" : generateGroupHtml(groups);

        Map<String, String> response = new HashMap<>();
        response.put("html", html);

        return ResponseEntity.ok(response);
    }

    /**
     * 그룹 목록을 HTML로 변환하는 메서드
     */
    private String generateGroupHtml(List<SocialGroupEntity> groups) {
        StringBuilder htmlBuilder = new StringBuilder();

        for (SocialGroupEntity group : groups) {
            String formattedDate = group.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            htmlBuilder.append("<div class=\"group-card\">")
                .append("<div class=\"group-info\">")
                .append("<table><tr>")
                .append("<td><img src=\"").append(group.getProfileImage()).append("\" class=\"group-image\" style=\"width:100%;\"></td>")
                .append("<td><h3>").append(group.getGroupName()).append("</h3>")
                .append("<p>위치: ").append(group.getLocation()).append("</p>")
                .append("<p>날짜: ").append(formattedDate).append("</p>")
                .append("<p>인원: ").append("멤버 수를 여기에 추가").append("/").append(group.getMemberLimit()).append("명</p>")
                .append("</td><td>")
                .append("<a href=\"/socialgroup/board?groupId=").append(group.getGroupId()).append("\" class=\"join-btn\">그룹 참여</a>")
                .append("</td></tr></table>")
                .append("</div></div>");
        }

        return htmlBuilder.toString();
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
