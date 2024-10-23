package com.dsa.team1.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dsa.team1.dto.PlaceDTO;
import com.dsa.team1.entity.BookmarkEntity;
import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.PlaceEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.UserPlaceEntity;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.entity.enums.PlaceCategory;
import com.dsa.team1.entity.enums.UserPlaceStatus;
import com.dsa.team1.repository.BookmarkRepository;
import com.dsa.team1.repository.PlaceRepository;
import com.dsa.team1.repository.UserPlaceRepository;
import com.dsa.team1.repository.UserRepository;
import com.dsa.team1.security.AuthenticatedUser;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor	
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository; // 이미 작성된 Repository
    private final UserPlaceRepository userPlaceRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    
    public PlaceDTO findPlace(Integer placeId) {
    	PlaceEntity placeEntity = placeRepository.findById(placeId).orElseThrow(() -> new NoSuchElementException("Place not found with id" + placeId));
    	PlaceDTO placeDTO = PlaceDTO.builder()
    			.placeId(placeEntity.getPlaceId())
    			.title(placeEntity.getTitle())
    			.description(placeEntity.getDescription())
    			.profileImage(placeEntity.getProfileImage())
    			.category(placeEntity.getCategory())
    			.location(placeEntity.getLocation())
    			.eventDate(placeEntity.getEventDate())
    			.requiredMembers(placeEntity.getRequiredMembers())
    			.currentMembers(placeEntity.getCurrentMembers())
    			.memberLimit(placeEntity.getMemberLimit())
    			.viewCount(placeEntity.getViewCount())
    			.bookmarkCount(placeEntity.getBookmarkCount())
    			.price(placeEntity.getPrice())
    			.vendorId(placeEntity.getVendor().getUserId())
    			.createdAt(placeEntity.getCreatedAt())
    			.build();
    	return placeDTO;
    }
    
    /**
     * List<PlaceEntity> to List<PlaceDTO>
     * @param placeEntityList
     * @return placeList(List<placeDTO>)
     */
    private List<PlaceDTO> convertEntityToDto(List<PlaceEntity> placeEntityList) {
    	List<PlaceDTO> placeList = new ArrayList<>();
    	for (PlaceEntity placeEntity : placeEntityList) {
    		PlaceDTO placeDTO = PlaceDTO.builder()
    				.placeId(placeEntity.getPlaceId())
    		        .title(placeEntity.getTitle())
    		        .description(placeEntity.getDescription())
    		        .profileImage(placeEntity.getProfileImage())
    		        .category(placeEntity.getCategory())
    		        .location(placeEntity.getLocation())
    		        .eventDate(placeEntity.getEventDate())
    		        .requiredMembers(placeEntity.getRequiredMembers())
    		        .currentMembers(placeEntity.getCurrentMembers())
    		        .memberLimit(placeEntity.getMemberLimit())
    		        .viewCount(placeEntity.getViewCount())
    		        .bookmarkCount(placeEntity.getBookmarkCount())
    		        .price(placeEntity.getPrice())
    		        .vendorId(placeEntity.getVendor().getUserId())
    		        .createdAt(placeEntity.getCreatedAt())
    		        .build();
    		
    		placeList.add(placeDTO);
    	}
    	
    	return placeList;
    }
    
    private List<PlaceEntity> convertDtoToEntity(List<PlaceDTO> placeDTOs) {
    	List<PlaceEntity> placeList = new ArrayList<>();
    	for (PlaceDTO placeDTO : placeDTOs) {
    		PlaceEntity placeEntity = PlaceEntity.builder()
    				.placeId(placeDTO.getPlaceId())
    		        .title(placeDTO.getTitle())
    		        .description(placeDTO.getDescription())
    		        .profileImage(placeDTO.getProfileImage())
    		        .category(placeDTO.getCategory())
    		        .location(placeDTO.getLocation())
    		        .eventDate(placeDTO.getEventDate())
    		        .requiredMembers(placeDTO.getRequiredMembers())
    		        .currentMembers(placeDTO.getCurrentMembers())
    		        .memberLimit(placeDTO.getMemberLimit())
    		        .viewCount(placeDTO.getViewCount())
    		        .bookmarkCount(placeDTO.getBookmarkCount())
    		        .price(placeDTO.getPrice())
    		        .vendor(userRepository.findById(placeDTO.getVendorId()).orElseThrow(() -> new NullPointerException("Vendor not found")))
    		        .createdAt(placeDTO.getCreatedAt())
    		        .build();
    		
    		placeList.add(placeEntity);
    	}
    	return placeList;
    }
    
    public List<PlaceDTO> getAllPlaces() {
    	List<PlaceEntity> placeEntityList = placeRepository.findAll();
    	List<PlaceDTO> placeList = convertEntityToDto(placeEntityList);
    	return placeList;
    }
    
    @Override
    public List<PlaceDTO> searchPlaces(String query, String activity, String location) {
    	
    	List<PlaceEntity> places = new ArrayList<>();
    	
    	if (query != null && !query.trim().isEmpty()) {
	        // 플레이스 이름과 설명에서 검색
	        places = placeRepository.searchByGroupNameOrDescription(query);
	    }
    	
    	// 2. 카테고리와 위치 필터링
	    PlaceCategory placeCategory = null;
	    if (activity != null && !activity.isEmpty()) {
	        try {
	            placeCategory = PlaceCategory.valueOf(activity.toUpperCase());
	        } catch (IllegalArgumentException e) {
	            log.error("유효하지 않은 카테고리 값: {}", activity);
	        }
	    }

	    // 필터링된 결과 반환 (query가 없을 경우, 필터링만 실행)
	    if (location != null || placeCategory != null) {
	        places = placeRepository.filterPlaces(query, location, placeCategory);
	    }
	    
	    // 중복 제거
	    places = places.stream().distinct().collect(Collectors.toList());
	    List<PlaceDTO> placeDtoList = convertEntityToDto(places);
	    
	    return placeDtoList;
    	
//    	// 검색어가 없을 때
//    	if (query != null && activity != null) {
//    		places = placeRepository.findByCategoryAndTitleContaining(activity, query);
//    	} else if (query == null && activity != null) {
//	        places = placeRepository.findByCategory(activity);
//	    } else if (query != null && activity == null) {
//	    	places = placeRepository.findByTitleCntaining(query);
//	    } else if (query == null && activity == null){
//	    	places = placeRepository.findAll();
//	    }
    	
    }
    
    @Override
    public Integer getMemberCountByPlace(PlaceDTO place) {
    	int memberCount = placeRepository.countCurrentMembersByPlaceId(place.getPlaceId());
    	
    	// 멤버 수가 그룹의 제한인원(memberLimit)을 넘지 않도록 합니다.
        if (memberCount > place.getMemberLimit()) {
            memberCount = place.getMemberLimit();
        }
        
        return memberCount;
    }
    
    @Override
    public ResponseEntity<Map<String, String>> isMember(Integer placeId, AuthenticatedUser user) {
    	
    	Map<String, String> response = new HashMap<>();
    	
    	PlaceEntity place = placeRepository.findById(placeId).orElseThrow(() -> new IllegalArgumentException("잘못된 플레이스 ID입니다."));
    	
    	// UserPlaceEntity를 통해 현재 사용자와 플레이스에 대한 정보 조회
        Optional<UserPlaceEntity> userPlaceOpt = userPlaceRepository.findByUser_UserIdAndPlace_PlaceId(user.getId(), placeId);
        
        if (userPlaceOpt.isPresent()) {
            UserPlaceEntity userPlace = userPlaceOpt.get();
            
            // 상태가 CONFIRMED인지 확인
            if (userPlace.getStatus() == UserPlaceStatus.CONFIRMED) {
                response.put("Error Message", "이미 플레이스에 참가했습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // CONFIRMED 상태가 아니라면 에러 메시지 반환
            response.put("OK Message","해당 플레이스에 참가되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    	
    	boolean isMember = userPlaceRepository.existsByUser_UserIdAndPlace_PlaceId(user.getId(), placeId);
    	if (isMember) {
    		response.put("Error Message", "이미 플레이스에 참가했습니다.");
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    	}
    	
		return ResponseEntity.ok(response);
    }
    
    @Override
    public void checkUserAndPlaceExistence(String userId, Integer placeId) throws Exception {
    	UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PlaceEntity place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

    }
    
    @Override
    public void toggleBookmark(String userId, Integer placeId) {
    	
//    	Boolean existingBookmark = bookmarkRepository.existsByUserAndPlace(userId, placeId);
    	
    	UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
    	PlaceEntity placeEntity = placeRepository.findById(placeId).orElseThrow(() -> new NoSuchElementException("Place not found"));
	    
    	Optional<BookmarkEntity> bookmarkOpt = bookmarkRepository.findByUserAndPlace(userEntity, placeEntity);
    			
	    if (bookmarkOpt.isPresent()) {
	        // 북마크가 존재하면 제거
	        bookmarkRepository.delete(bookmarkOpt.get());
	        // 북마크 수가 0 이상이면 감소, 그렇지 않으면 0으로 유지
	        if (placeEntity.getBookmarkCount() > 0) {
	            placeEntity.setBookmarkCount(placeEntity.getBookmarkCount() - 1); 
	        }
	    } else {
	        // 북마크가 없으면 추가
	        BookmarkEntity newBookmark = BookmarkEntity.builder()
	            .user(userEntity)
	            .group(null)			// 필요 없으므로 null
	            .place(placeEntity)  
	            .createdAt(LocalDateTime.now())
	            .build();
	        bookmarkRepository.save(newBookmark);
	        placeEntity.setBookmarkCount(placeEntity.getBookmarkCount() + 1); // 북마크 수 증가
	    }
	    
	    // DB에 그룹의 북마크 수 업데이트 반영
	    placeRepository.save(placeEntity);
    }
    
    @Override
    public List<PlaceDTO> filter(String query, String category, String location) {
    	List<PlaceEntity> places = new ArrayList<>();
    	
    	// if query is not null
    	if (query != null && !query.trim().isEmpty()) {
    	
    		// Finding Place including query in title or description
    		places = placeRepository.findByTitleContainingOrDescriptionContaining(query, query);
    	} else {
    		// if query is null, find all places
    		places = placeRepository.findAll();
    	}
    	
    	//Filtering category and location
    	PlaceCategory placeCategory = null;
    	
    	if (category != null && !category.isEmpty()) {
    		try {
    			placeCategory = PlaceCategory.valueOf(category.toUpperCase());
    		} catch (IllegalArgumentException e) {
    			log.error("유효하지 않은 카테고리 값: {}", category);
    		}
    	}
    	
    	if (placeCategory != null || (location != null && !location.isEmpty())) {
    		final PlaceCategory finalPlaceCategory = placeCategory;
    		
    		// filtering location
    		if (location != null && !location.isEmpty()) {
    			List<String> locationList = Arrays.asList(location.split(","));
    			
    			places = places.stream()
    					.filter(place -> {
    						// split placeLocation by non-characteristric
    						List<String> placeLocations = Arrays.asList(place.getLocation().split("[^\\p{L}]+"));
    						
    						// Check words common to the places' location list and the list of selected region names
    						for (String loc : locationList) {
    							if (placeLocations.contains(loc)) {
    								return true;
    							}
    						}
    						return false;
    					})
    					.collect(Collectors.toList());
    		}
    		
    		//Filter category
    		places = places.stream()
    				.filter(place -> (finalPlaceCategory == null || place.getCategory() == finalPlaceCategory))
    				.collect(Collectors.toList());
    		
    	}
    	
    	// Processing empty location value
		location = (location != null && !location.trim().isEmpty()) ? location : null;
		
		// Get filtered place list
		places = placeRepository.filterPlaces(query, location, placeCategory);
		
		// Convert entity to DTO
		List<PlaceDTO> placeDTOs = convertEntityToDto(places);
		
		return placeDTOs;
    	
    }
    
    @Override
    public Map<Integer, Integer> getCurrentMembers(List<PlaceDTO> placeDTOs) {
    	if (placeDTOs.isEmpty()) {
    	    return new HashMap<>();
    	}
    	// Calculate currentMembers of each places
		Map<Integer, Integer> currentMembersMap = new HashMap<>();
		List<PlaceEntity> placeEntityList = convertDtoToEntity(placeDTOs);
		for (PlaceEntity placeEntity : placeEntityList) {
			int currentMembers = getCurrentMembersByPlace(placeEntity);
			currentMembersMap.put(placeEntity.getPlaceId(), currentMembers);
			log.info("Place ID: {}, Current Members: {}", placeEntity.getPlaceId(), currentMembers);

		}
    	return currentMembersMap;
    }
    
    public int getCurrentMembersByPlace(PlaceEntity place) {
    	// Get Active members num in place (except vendor)
    	int currentMembers = userPlaceRepository.countActiveMembersByPlace_PlaceId(place.getPlaceId());
    	
    	// Vendor is not counting
//    	currentMembers += 1;
    	
    	return currentMembers;
    }
    
    
    
    
//
//    public List<PlaceDTO> getAllPosts(String sort) {
//        List<PlaceEntity> places = placeRepository.findAll();
//
//        // 정렬 로직 추가
//        switch (sort) {
//            case "조회수":
//                places.sort(Comparator.comparingInt(PlaceDTO::getViewCount).reversed());
//                break;
//            case "많이 담은 순":
//                places.sort(Comparator.comparingInt(Place::getSavedCount).reversed());
//                break;
//            default: // "최신순"
//                places.sort(Comparator.comparing(Place::getDate).reversed());
//                break;
//        }
//
//        return places.stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    private PlaceDTO convertToDTO(PlaceDTO place) {
//        PlaceDTO dto = new PlaceDTO();
//        dto.setPlaceId(place.getPlaceId());
//        dto.setTitle(place.getTitle());
//        dto.setDescription(place.getDescription());
//        dto.setEventDate(place.getEventDate());
//        return dto;
//    }
}
