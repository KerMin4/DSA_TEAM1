package com.dsa.team1.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.SocialGroupDTO;
import com.dsa.team1.entity.BookmarkEntity;
import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.UserGroupEntity;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.entity.enums.UserGroupStatus;
import com.dsa.team1.repository.BookmarkRepository;
import com.dsa.team1.repository.GroupHashtagRepository;
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
public class SocialGroupServiceImpl implements SocialGroupService {
    
    private final SocialGroupRepository socialGroupRepository;   
    private final UserRepository userRepository;                 
    private final GroupHashtagRepository groupHashtagRepository;
    private final UserGroupRepository userGroupRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FileManager fileManager;                       
    
    @Override
    public void create(List<String> interest, String groupName, String eventDate, String description,
			String location, String joinMethod, Integer memberLimit, List<String> hashtagList,
			MultipartFile profileImage, AuthenticatedUser user) throws IOException {

    	String profileImagePath = null;

    	// 프로필 이미지가 있는 경우 파일을 저장하고, 경로를 변수에 저장
    	if (profileImage != null && !profileImage.isEmpty()) {
    		profileImagePath = fileManager.saveFile("C:/upload", profileImage);
		}
    	
    	// eventDate가 String으로 전달되었으므로 LocalDateTime으로 변환
        LocalDateTime eventDateTime = LocalDateTime.parse(eventDate + "T00:00:00");  // 시간 정보 없이 기본값을 사용
        
        // String joinMethod를 GroupJoinMethod로 변환
        GroupJoinMethod groupJoinMethodEnum = GroupJoinMethod.valueOf(joinMethod.toUpperCase()); // 대문자로 변환 후 Enum 변환
        
        // Interest Enum으로 변환 - 리스트로 변환
        List<Interest> interestEnumList = interest.stream()
            .map(Interest::valueOf)   // 각 문자열을 Enum으로 변환
            .collect(Collectors.toList());

    	// SocialGroupEntity 생성
        SocialGroupEntity socialGroupEntity = SocialGroupEntity.builder()
                 .groupName(groupName)           
                 .description(description)       
                 .profileImage(profileImagePath)                     
                 .location(location)             
                 .memberLimit(memberLimit)       
                 .eventDate(eventDateTime)
                 .groupLeader(userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("리더 사용자 ID를 찾을 수 없습니다.")))
                 .viewCount(0)  // 기본 조회수 설정
                 .bookmarkCount(0)  // 기본 북마크 설정
                 .createdAt(LocalDateTime.now())  
                 .groupJoinMethod(groupJoinMethodEnum)	// Enum 타입으로 변경된 값을 사용
                 .interest(interestEnumList.get(0))		// 변환된 interestEnum 사용		// 첫 번째 관심사만 설정
                 .build();
         
        // 그룹 저장
        socialGroupRepository.save(socialGroupEntity);

        // 해시태그 저장
        saveHashtags(hashtagList, socialGroupEntity);
        
        // 나머지 관심사 로깅 (현재는 저장하지 않음)
    	for (int i = 1; i < interestEnumList.size(); i++) {
    		System.out.println("Additional interest: " + interestEnumList.get(i));
    	}
    }

    private void saveHashtags(List<String> hashtags, SocialGroupEntity socialGroupEntity) {
        if (hashtags != null && !hashtags.isEmpty()) {
            for (String hashtag : hashtags) {
                GroupHashtagEntity groupHashtag = GroupHashtagEntity.builder()
                        .group(socialGroupEntity)
                        .name(hashtag.trim())
                        .build();
                groupHashtagRepository.save(groupHashtag);
            }
        }
    }
    
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

	@Override
	public List<SocialGroupEntity> findAllGroups() {
	    List<SocialGroupEntity> groups = socialGroupRepository.findAll();
	    return (groups != null) ? groups : new ArrayList<>();
	}

	@Override
	public List<SocialGroupEntity> searchGroups(String query, String category, String location) {

	    // 1. 해시태그나 그룹 이름/설명으로 검색
	    List<SocialGroupEntity> groups = new ArrayList<>();
	    
	    if (query != null && !query.trim().isEmpty()) {
	        // 그룹 이름과 설명에서 검색
	        groups = socialGroupRepository.searchByGroupNameOrDescription(query);
	        
	        // 해시태그에서 검색한 결과를 추가
	        List<SocialGroupEntity> hashtagGroups = groupHashtagRepository
	                .findByNameContaining(query)
	                .stream()
	                .map(GroupHashtagEntity::getGroup)
	                .collect(Collectors.toList());
	        
	        groups.addAll(hashtagGroups);
	    }

	    // 2. 카테고리와 위치 필터링
	    Interest interestCategory = null;
	    if (category != null && !category.isEmpty()) {
	        try {
	            interestCategory = Interest.valueOf(category.toUpperCase());
	        } catch (IllegalArgumentException e) {
	            log.error("유효하지 않은 카테고리 값: {}", category);
	        }
	    }

	    // 필터링된 결과 반환 (query가 없을 경우, 필터링만 실행)
	    if (location != null || interestCategory != null) {
	        groups = socialGroupRepository.filterGroups(query, null, location, interestCategory);
	    }

	    // 중복 제거
	    return groups.stream().distinct().collect(Collectors.toList());
	}
	
	@Override
	public void toggleBookmark(UserEntity user, SocialGroupEntity group) {
	    Optional<BookmarkEntity> existingBookmark = bookmarkRepository.findByUserAndGroup(user, group);
	    
	    if (existingBookmark.isPresent()) {
	        // 북마크가 존재하면 제거
	        bookmarkRepository.delete(existingBookmark.get());
	        // 북마크 수가 0 이상이면 감소, 그렇지 않으면 0으로 유지
	        if (group.getBookmarkCount() > 0) {
	            group.setBookmarkCount(group.getBookmarkCount() - 1); 
	        }
	    } else {
	        // 북마크가 없으면 추가
	        BookmarkEntity newBookmark = BookmarkEntity.builder()
	            .user(user)
	            .group(group)
	            .place(null)  // 필요 없으므로 null
	            .createdAt(LocalDateTime.now())
	            .build();
	        bookmarkRepository.save(newBookmark);
	        group.setBookmarkCount(group.getBookmarkCount() + 1); // 북마크 수 증가
	    }
	    
	    // DB에 그룹의 북마크 수 업데이트 반영
	    socialGroupRepository.save(group);
	}

}
