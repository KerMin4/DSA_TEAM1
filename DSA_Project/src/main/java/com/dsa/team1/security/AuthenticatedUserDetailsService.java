package com.dsa.team1.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticatedUserDetailsService //implements UserDetailsService
{
	
	// private final MemberRepository memberRepository; 레포지토리 만들면 그 때 다시 수정
/*	
	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		log.info("로그인 시도 : {}",id);
		
		MemberEntity memberEntity = memberRepository.findById(id)
				.orElseThrow(() -> {
					return new UsernameNotFoundException(id + " : 없는 ID입니다.");
				});
		log.debug("조회정보 : {}", memberEntity); 
		엔티티 생성시 수정
			
		AuthenticatedUser user = AuthenticatedUser.builder()
				.id(memberEntity.getMemberId())
				.password(memberEntity.getMemberPassword())
				.name(memberEntity.getMemberName())
				.enabled(memberEntity.getMemberEnabled())
				.roleName(memberEntity.getMemberRolename())
				.build();
		
		return user;
	
	*/
	//1
}