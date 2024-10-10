package com.dsa.team1.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dsa.team1.dto.SocialGroupDTO;
import com.dsa.team1.entity.GroupHashtagEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.enums.GroupJoinMethod;
import com.dsa.team1.repository.GroupHashtagRepository;
import com.dsa.team1.repository.SocialGroupRepository;
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

    	// SocialGroupEntity 생성
        SocialGroupEntity socialGroupEntity = SocialGroupEntity.builder()
                 .groupName(groupName)           
                 .description(description)       
                 .profileImage(profileImagePath)                     
                 .location(location)             
                 .memberLimit(memberLimit)       
                 .eventDate(eventDateTime)
                 .groupLeader(userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("리더 사용자 ID를 찾을 수 없습니다.")))
                 .createdAt(LocalDateTime.now())  
                 .groupJoinMethod(groupJoinMethodEnum)	// Enum 타입으로 변경된 값을 사용
                 .build();
         
        // 그룹 저장
        socialGroupRepository.save(socialGroupEntity);

        // 해시태그 저장
        saveHashtags(hashtagList, socialGroupEntity);
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

    @Override
    public List<SocialGroupDTO> getAllGroups() {
        List<SocialGroupEntity> entities = socialGroupRepository.findAll();
        return entities.stream()
                .map(entity -> new SocialGroupDTO(entity.getGroupId(), entity.getGroupName(),
                                                   entity.getDescription(), entity.getProfileImage(),
                                                   entity.getLocation(), entity.getGroupLeader().getUserId(),
                                                   entity.getGroupJoinMethod(), entity.getMemberLimit(),
                                                   entity.getViewCount(), entity.getBookmarkCount(),
                                                   entity.getEventDate(), entity.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
