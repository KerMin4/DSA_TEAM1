package com.dsa.team1.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dsa.team1.dto.PlaceDTO;
import com.dsa.team1.repository.BookmarkRepository;
import com.dsa.team1.security.AuthenticatedUser;
import com.dsa.team1.service.PlaceService;
import com.dsa.team1.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("place")
public class PlaceController {
	
    private final PlaceService placeService;
    
    private final UserService userService;
    private final BookmarkRepository bookmarkRepository;
	
    /**
     * Listing Places
     * @param activity
     * @param query
     * @param user
     * @param model
     * @return placeMain.html
     */
    @GetMapping("placeMain")
    public String placeMain(
    		@RequestParam(value = "query", required = false) String query,
    		@AuthenticationPrincipal AuthenticatedUser user,
    		Model model
    		) {
    	
    	// 플레이스 목록을 저장할 변수 선언
        List<PlaceDTO> places;
        
        // If there is query, look for filtered results, if not look for all places
        if (query != null && !query.trim().isEmpty()) {
            log.info("검색어: {}", query);
            places = placeService.searchPlaces(query, null, null); // 검색된 그룹 목록
            log.info("검색된 플레이스 수: {}", places.size());
        } else {
            places = placeService.getAllPlaces(); // 전체 그룹 목록
            log.info("전체 플레이스 수: {}", places.size());
        }
        
        // User information
        if (user != null) {
            model.addAttribute("name", user.getName());
        } else {
            model.addAttribute("name", null);
        }
        
        // Bookmark status per-user
        Map<Integer, Boolean> bookmarkedMap = new HashMap<>();
        if (user != null) {
        	  // not necessary
//            UserDTO currentUser = userService.findUser(user.getId());
            for (PlaceDTO place : places) {
				Boolean isBookmarked = bookmarkRepository.existsByUser_UserIdAndPlace_PlaceId(user.getId(), place.getPlaceId()); // 북마크 여부 확인
                // 만약 북마크 상태가 없는 경우 기본값을 false로 설정
                bookmarkedMap.put(place.getPlaceId(), isBookmarked != null ? isBookmarked : false); // null 체크 후 값 설정
            }
        }
        
        // Calculate currentMembers
        Map<Integer, Integer> currentMembersMap = placeService.getCurrentMembers(places);
        
        // profile image
        List<String> profileImages = places.stream()
        		.map(PlaceDTO::getProfileImage)
        		.map(image -> (image != null) ? image : "noImage_icon.png")
        		.collect(Collectors.toList());
        
        // Add place list, memberCount, bookmark status, profile image to model
        model.addAttribute("places", places);
        model.addAttribute("currentMembersMap", currentMembersMap);
        model.addAttribute("bookmarkedMap", bookmarkedMap);
        model.addAttribute("profileImages", profileImages);
        
        
        return "place/placeMain";
    }
    
    
    /**
     * Filter places
     * @param query
     * @param category
     * @param location
     * @param model
     * @return placeMain.html
     */
    @GetMapping("filter")
    public String filterPlaces(
    		@RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "location", required = false) String location,
            Model model) {
    	
    	List<PlaceDTO> placeDTOs = placeService.filter(query, category, location);
    	Map<Integer, Integer> currentMembersMap = placeService.getCurrentMembers(placeDTOs);
    	
    	model.addAttribute("places", placeDTOs);
    	model.addAttribute("currentMembers", currentMembersMap);
    	
    	return "place/placeMain";
    }
    
    /**
     * Toggle bookmark. Used in placeMain.js
     * @param placeId
     * @param user
     * @return
     */
    @PostMapping("bookmark/toggle")
    public ResponseEntity<String> toggleBookmark(
    		@RequestParam("placeId") Integer placeId,
    		@AuthenticationPrincipal AuthenticatedUser user) {
    	
    	log.info("Received placeId: {}", placeId);
    	
    	if (user == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }
    	
    	// Check user and place exist
    	// throws IllegalArgumentException
    	try {
			placeService.checkUserAndPlaceExistence(user.getId(), placeId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        placeService.toggleBookmark(user.getId(), placeId);
    	
        PlaceDTO place = placeService.findPlace(placeId);
    	return ResponseEntity.ok(place.getBookmarkCount().toString());  // 변경된 북마크 수를 반환
    }
    
//    /**
//     * Join in place
//     * @return joinPlace.html
//     */
//    @GetMapping("joinPlace")
//    public ResponseEntity<Map<String, String>> join(
//    		@RequestParam("placeId") Integer placeId,
//            @AuthenticationPrincipal AuthenticatedUser user) {
//    	
//    	return placeService.isMember(placeId, user);
//    }
    
    /**
     * Join Place
     * @param placeId
     * @return joinPlace.html
     */
    @GetMapping("joinPlace")
    public String joinPlace(
    		@RequestParam("placeId") Integer placeId,
    		Model model) {
    	
    	if (placeId == null) {
    	    log.error("Place ID is null");
    	    return "error/400"; // 잘못된 요청에 대한 처리
    	}
    	
    	log.info("[PlaceController-joinPlace] placeId: {}", placeId);
    	PlaceDTO place = placeService.findPlace(placeId);
    	log.info("[PlaceController-joinPlace] placeDTO: {}", place);
    	
    	model.addAttribute("place", place);
    	
    	return "place/joinPlace";
    }
    
//    @ResponseBody
//    @GetMapping("joinPlace/{placeId}")
//    public ResponseEntity<PlaceDTO> getPlaceById(@PathVariable("placeId") Integer placeId) {
//    	
//    	log.info("Fetching place with ID: {}", placeId);
//        
//        if (placeId == null || placeId <= 0) {
//            log.error("Invalid placeId: {}", placeId);
//            return ResponseEntity.badRequest().build();
//        }
//    	
//    	PlaceDTO place = placeService.findPlace(placeId);
//    	log.info("[PlaceController] place: {}", place);
//    	if (place != null) {
//    		return ResponseEntity.ok(place);
//    	} else {
//    		return ResponseEntity.notFound().build();
//    	}
//    }
    
    @GetMapping("placeDetail")
    public String placeDetail(
    		@RequestParam("placeId") Integer placeId, 
    		Model model) {
    	
    	if (placeId == null) {
    	    log.error("Place ID is null");
    	    return "error/400"; // 잘못된 요청에 대한 처리
    	}
    	
    	log.info("[PlaceController - placeDetail] placeId: {}", placeId);
    	
    	PlaceDTO place = placeService.findPlace(placeId);
    	log.info("Place eventDate: {}", place.getEventDate());

    	model.addAttribute("place", place);
    	
    	return "place/placeDetail";
    }
    
//    @PostMapping("reserve")
//    public String reservation(
//    		@RequestParam("placeId") Integer placeId,
//    		@AuthenticationPrincipal AuthenticatedUser user,
//    		Model model) {
//    	
//    	Boolean possibleToReserve = placeService.reservePlace(placeId, user.getId());
//    	if (possibleToReserve) {
//    		return "redirect:/place/placeDetail?placeId=" + placeId + "&message=예약이 완료되었습니다.";
//    	} else {
//    		return "redirect:/place/placeDetail?placeId=" + placeId + "&message=정보를 찾을 수 없습니다.";
//    	}
//    }
    
    @PostMapping("payment")
    public String payment(
            @RequestParam("placeId") Integer placeId,
            @AuthenticationPrincipal AuthenticatedUser user,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // 결제 처리 결과 메시지 가져오기
        String message = placeService.paymentPlace(placeId, user.getId());

        model.addAttribute("message", message);
//        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/place/placeDetail?placeId=" + placeId;
    }
    
    
//    @GetMapping("/placeMain")
//    public String getPlaceMain(@RequestParam(defaultValue = "최신순") String activity,
//                                @RequestParam(defaultValue = "최신순") String sort,
//                                Model model) {
//        List<PlaceDTO> posts = placeService.getAllPosts(sort);
//        model.addAttribute("posts", posts);
//        return "placeMain";
//    }
	
}
