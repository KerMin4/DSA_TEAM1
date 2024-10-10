package com.dsa.team1.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.UserGroupEntity;
import com.dsa.team1.repository.SocialGroupRepository;
import com.dsa.team1.repository.UserGroupRepository;
import com.dsa.team1.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticatedUserDetailsService implements UserDetailsService
{
	
private final UserRepository userRepository;
private final SocialGroupRepository socialGroupRepository;
private final UserGroupRepository userGroupRepository;
	
    // 프로필 사진 불러오는 부분 추가함 -나연-
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        log.info("로그인 시도 : {}", id);
        
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException(id + " : 없는 ID입니다.");
                });
        log.debug("조회정보 : {}", userEntity); 
       
        AuthenticatedUser user = AuthenticatedUser.builder()
                .id(userEntity.getUserId())
                .password(userEntity.getPassword())
                .name(userEntity.getName())
                .profileImage(userEntity.getProfileImage()) 
                .build();
        
        return user;
    }
    
    // 그룹 헤더 이미지와 함께 사용자 정보를 로드하는 새로운 메소드
    public UserDetails loadUserDetailsWithGroupHeader(String id) throws UsernameNotFoundException {
        log.info("로그인 시도 (그룹 헤더 이미지 포함) : {}", id);

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(id + " : 없는 ID입니다."));
        log.debug("조회정보 : {}", userEntity);

        // 사용자와 그룹 간의 관계를 가져오기
        List<UserGroupEntity> userGroups = userGroupRepository.findByUser_UserId(userEntity.getUserId()); 

        String groupHeaderImage = null;
        if (!userGroups.isEmpty()) {
            // 첫 번째 그룹의 ID를 가져와서 헤더 이미지를 조회
            Integer groupId = userGroups.get(0).getGroup().getGroupId(); 
            groupHeaderImage = socialGroupRepository.findGroupHeaderImageByGroupId(groupId);
        }

        AuthenticatedUser user = AuthenticatedUser.builder()
                .id(userEntity.getUserId())
                .password(userEntity.getPassword())
                .name(userEntity.getName())
                .profileImage(userEntity.getProfileImage())
                .groupHeaderImage(groupHeaderImage) // 그룹 헤더 이미지 추가
                .build();

        return user;
    }
    
}




// 원래 코드
/*
 *  private final UserRepository userRepository; 
	
	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		log.info("로그인 시도 : {}",id);
		
		UserEntity userEntity = userRepository.findById(id)
				.orElseThrow(() -> {
					return new UsernameNotFoundException(id + " : 없는 ID입니다.");
				});
		log.debug("조회정보 : {}", userEntity); 
		
			
		AuthenticatedUser user = AuthenticatedUser.builder()
				.id(userEntity.getUserId())
				.password(userEntity.getPassword())
				.name(userEntity.getName())
				.build();
		
		return user;
}
	}
 */
