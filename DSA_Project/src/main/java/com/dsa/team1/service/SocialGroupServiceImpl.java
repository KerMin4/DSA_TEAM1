package com.dsa.team1.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    
    /**
     * 그룹을 생성하는 메서드
     * @param interest 그룹의 관심사 (리스트 형태)
     * @param groupName 그룹명
     * @param eventDate 이벤트 날짜 (String 형식으로 전달됨)
     * @param description 그룹 설명
     * @param location 그룹 위치
     * @param joinMethod 그룹 가입 방식 (AUTO 또는 APPROVAL)
     * @param memberLimit 그룹 멤버 수 제한
     * @param hashtagList 해시태그 리스트
     * @param profileImage 그룹 프로필 이미지 (MultipartFile)
     * @param user 현재 로그인한 유저 (AuthenticatedUser)
     * @throws IOException 파일 업로드 중 예외 발생 시
     */
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

    /**
     * 그룹에 연결된 해시태그를 저장하는 메서드
     * @param hashtags 저장할 해시태그 목록
     * @param socialGroupEntity 해시태그를 연결할 그룹 엔티티
     */
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
    
    /**
     * 그룹의 활성 멤버 수를 계산하는 메서드 (그룹 리더를 포함하여 계산)
     * @param group 조회할 그룹
     * @return 그룹의 활성 멤버 수
     */
    public int getMemberCountByGroup(SocialGroupEntity group) {
    	
        int memberCount = userGroupRepository.countActiveMembersByGroupId(group.getGroupId());
        
        // 그룹 리더는 무조건 포함하므로 1명을 더한다
        memberCount += 1;

        // 멤버 수가 그룹의 제한인원(memberLimit)을 넘지 않게 한다
        if (memberCount > group.getMemberLimit()) {
            memberCount = group.getMemberLimit();
        }

        return memberCount;
    }

    /**
     * 모든 그룹을 조회하는 메서드
     * @return 전체 그룹 리스트
     */
	@Override
	public List<SocialGroupEntity> findAllGroups() {
	    List<SocialGroupEntity> groups = socialGroupRepository.findAll();
	    return (groups != null) ? groups : new ArrayList<>();
	}

	/**
     * 검색 쿼리를 바탕으로 그룹을 검색하고 카테고리 및 위치를 필터링하는 메서드
     * @param query 그룹명, 설명, 해시태그로 검색할 쿼리
     * @param category 그룹 카테고리 (예: HOBBY, FOOD)
     * @param location 그룹 위치
     * @return 필터링된 그룹 리스트
     */
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
	
	/**
     * 그룹 북마크를 토글(추가/제거)하는 메서드
     * @param user 현재 로그인한 사용자
     * @param group 북마크할 그룹
     */
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
	
	/**
     * 그룹 ID로 특정 그룹을 조회하는 메서드
     * @param groupId 조회할 그룹의 ID
     * @return 조회된 그룹 엔티티
     */
	@Override
    public SocialGroupEntity findById(Integer groupId) {
        return socialGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));
    }
	
	/**
	 * 그룹에 멤버를 추가하는 공통 메서드
	 * @param user 추가할 사용자
	 * @param group 해당 그룹
	 * @param status 그룹 가입 상태 (APPROVED, PENDING 등)
	 */
	private void addUserToGroup(UserEntity user, SocialGroupEntity group, UserGroupStatus status) {
	    // 새로운 멤버를 그룹에 추가 (빌더 패턴 사용)
	    UserGroupEntity userGroupEntity = UserGroupEntity.builder()
	            .user(user)
	            .group(group)
	            .status(status)
	            .build();

	    userGroupRepository.save(userGroupEntity);
	}

	/**
     * 그룹에 새로운 멤버를 추가하는 메서드
     * @param userId 추가할 사용자의 ID
     * @param groupId 해당 그룹의 ID
     */
	@Override
	public void addMemberToGroup(String userId, Integer groupId) {
	    // 해당 그룹 조회
	    SocialGroupEntity group = socialGroupRepository.findById(groupId)
	            .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

	    // 사용자가 이미 그룹의 멤버인지 확인
	    boolean isMember = userGroupRepository.existsByUserAndGroupId(userId, groupId);
	    if (isMember) {
	        throw new IllegalArgumentException("이미 그룹의 멤버입니다.");
	    }

	    // 사용자 조회
	    UserEntity user = userRepository.findById(userId)
	            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

	    // 공통 메서드를 사용하여 멤버 추가
	    addUserToGroup(user, group, UserGroupStatus.APPROVED);

	    // 그룹 멤버 수 증가 처리 (이미 구현된 getMemberCountByGroup 사용 가능)
	    int currentMemberCount = getMemberCountByGroup(group);
	    group.setMemberLimit(currentMemberCount);

	    // 그룹 정보 업데이트
	    socialGroupRepository.save(group);
	}

	/**
     * 그룹에 가입 요청을 보내는 메서드
     * @param userId 가입 요청을 보낼 사용자의 ID
     * @param groupId 해당 그룹의 ID
     */
	@Override
	public void requestApprovalToJoinGroup(String userId, Integer groupId) {
	    // 해당 그룹 조회
	    SocialGroupEntity group = socialGroupRepository.findById(groupId)
	            .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

	    // 사용자가 이미 가입 요청을 보냈는지 확인
	    boolean isPending = userGroupRepository.existsByUserAndGroupIdAndStatus(userId, groupId, UserGroupStatus.PENDING);
	    if (isPending) {
	        throw new IllegalArgumentException("이미 가입 요청이 진행 중입니다.");
	    }

	    // 사용자 조회
	    UserEntity user = userRepository.findById(userId)
	            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

	    // 공통 메서드를 사용하여 가입 요청 저장 (상태: PENDING)
	    addUserToGroup(user, group, UserGroupStatus.PENDING);

	    // 그룹 리더에게 알림 기능을 구현 (예: 이메일, 알림 메시지 등)
	    log.info("그룹 리더 {}에게 가입 요청 알림을 보냅니다.", group.getGroupLeader().getUserId());
	}

	/**
	 * 가입 요청을 거절하는 메서드
	 * @param userId 거절할 사용자의 ID
	 * @param groupId 해당 그룹의 ID
	 */
	@Override
	public void rejectJoinRequest(Integer userId, Integer groupId) {
	    // 그룹 조회
	    SocialGroupEntity group = socialGroupRepository.findById(groupId)
	            .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
	    
	    // 가입 요청 조회 (상태: PENDING)
	    Optional<UserGroupEntity> joinRequest = userGroupRepository
	            .findByUserAndGroupIdAndStatus(userId, groupId, UserGroupStatus.PENDING);

	    // 요청이 없으면 예외 처리
	    if (joinRequest.isEmpty()) {
	        throw new IllegalArgumentException("가입 요청이 존재하지 않습니다.");
	    }

	    // 가입 요청을 거절 (DB에서 삭제 또는 상태를 REJECTED로 변경)
	    UserGroupEntity userGroupEntity = joinRequest.get();
	    userGroupEntity.setStatus(UserGroupStatus.REJECTED); // 상태를 거절로 변경
	    userGroupRepository.save(userGroupEntity);  // 변경 사항 저장
	}

	/**
	 * 새로운 멤버를 그룹에 추가하는 메서드 (Integer 버전)
	 * @param userId 추가할 사용자의 ID
	 * @param groupId 해당 그룹의 ID
	 */
	@Override
	public void addMemberToGroup(Integer userId, Integer groupId) {
	    // 그룹 조회
	    SocialGroupEntity group = socialGroupRepository.findById(groupId)
	            .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

	    // 사용자가 이미 그룹의 멤버인지 확인
	    boolean isMember = userGroupRepository.existsByUserAndGroupId(userId.toString(), groupId);
	    if (isMember) {
	        throw new IllegalArgumentException("이미 그룹의 멤버입니다.");
	    }

	    // 사용자 조회
	    UserEntity user = userRepository.findById(userId.toString())
	            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

	    // 공통 메서드를 사용하여 멤버 추가
	    addUserToGroup(user, group, UserGroupStatus.APPROVED);

	    // 그룹 멤버 수 증가 처리 (이미 구현된 getMemberCountByGroup 사용 가능)
	    int currentMemberCount = getMemberCountByGroup(group);
	    group.setMemberLimit(currentMemberCount);

	    // 그룹 정보 업데이트
	    socialGroupRepository.save(group);
	}


	

//	/**
//     * 그룹에 새로운 멤버를 추가하는 메서드
//     * @param userId 추가할 사용자의 ID
//     * @param groupId 해당 그룹의 ID
//     */
//	@Override
//	public void addMemberToGroup(
//			String userId,
//			Integer groupId) {
//		// 해당 그룹 조회
//	    SocialGroupEntity group = socialGroupRepository.findById(groupId)
//	            .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
//
//	    // 사용자가 이미 그룹의 멤버인지 확인
//	    boolean isMember = userGroupRepository.existsByUserAndGroupId(userId, groupId);
//	    if (isMember) {
//	        throw new IllegalArgumentException("이미 그룹의 멤버입니다.");
//	    }
//
//	    // 사용자 조회
//	    UserEntity user = userRepository.findById(userId)
//	            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//	    
//	    // 새로운 멤버를 그룹에 추가
//	    UserGroupEntity userGroupEntity = UserGroupEntity.builder()
//	            .user(user)
//	            .group(group)
//	            .status(UserGroupStatus.APPROVED)
//	            .build();
//
//	    userGroupRepository.save(userGroupEntity);
//
//	    // 새로운 멤버를 그룹에 추가
//	    userGroupRepository.save(new UserGroupEntity(user, group, UserGroupStatus.APPROVED));
//
//	    // 그룹 멤버 수 증가 처리 (이미 구현된 getMemberCountByGroup 사용 가능)
//	    int currentMemberCount = getMemberCountByGroup(group);
//	    group.setMemberLimit(currentMemberCount);
//	    
//	    // 그룹 정보 업데이트
//	    socialGroupRepository.save(group);
//	}
//
//	@Override
//	public void requestApprovalToJoinGroup(
//			String userId,
//			Integer groupId) {
//	    // 해당 그룹 조회
//	    SocialGroupEntity group = socialGroupRepository.findById(groupId)
//	            .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
//
//	    // 사용자가 이미 가입 요청을 보냈는지 확인
//	    boolean isPending = userGroupRepository.existsByUserAndGroupIdAndStatus(userId, groupId, UserGroupStatus.PENDING);
//	    if (isPending) {
//	        throw new IllegalArgumentException("이미 가입 요청이 진행 중입니다.");
//	    }
//
//	    // 사용자 조회
//	    UserEntity user = userRepository.findById(userId)
//	            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//	    
//	    // 그룹에 가입 요청을 저장 (상태: PENDING, 빌더 패턴 사용)
//	    UserGroupEntity userGroupEntity = UserGroupEntity.builder()
//	            .user(user)
//	            .group(group)
//	            .status(UserGroupStatus.PENDING)
//	            .build();
//	    
//	    userGroupRepository.save(userGroupEntity);
//
//	    // 그룹에 가입 요청을 저장 (상태: PENDING)
//	    userGroupRepository.save(new UserGroupEntity(user, group, UserGroupStatus.PENDING));
//
//	    // 그룹 리더에게 알림 기능을 구현 (예: 이메일, 알림 메시지 등)
//	    log.info("그룹 리더 {}에게 가입 요청 알림을 보냅니다.", group.getGroupLeader().getUserId());
//	}
	

}
