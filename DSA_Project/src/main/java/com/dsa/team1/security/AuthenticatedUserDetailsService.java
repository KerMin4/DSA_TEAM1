package com.dsa.team1.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticatedUserDetailsService implements UserDetailsService
{
	
	 private final UserRepository userRepository; 
	
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