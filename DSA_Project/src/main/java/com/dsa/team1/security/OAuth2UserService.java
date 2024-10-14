package com.dsa.team1.security;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
	private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptEncoder;
	
 public OAuth2UserService(BCryptPasswordEncoder bCryptEncoder) {
	 this.bCryptEncoder = bCryptEncoder;
	 log.debug("주입성공");
 }
    
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
		log.debug("일단 여기까진 진입 성공");
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.debug("카카오 사용자 정보: " ,oAuth2User.getAttributes());
		
		OAuth2UserInfo oAuth2UserInfo = null;
		String provider = userRequest.getClientRegistration().getRegistrationId();
		
		if(provider.equals("kakao")) {
			oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
		}
		
		String providerId = oAuth2UserInfo.getProviderId();
		String username = provider="_"+providerId;
		String uuid = UUID.randomUUID().toString().substring(0,6);
		log.debug("여기까진 올거야 아마?");
		String password = bCryptEncoder.encode("패스워드"+uuid);
		
		String email = oAuth2UserInfo.getEmail();
		
		UserEntity userEntity = userRepository.findById(username).orElse(null);
		
		if(userEntity == null) {
			userEntity = UserEntity.builder()
									.userId(providerId)
									.userName(username)
									.password(password)
									.email(email)
									.build();
			userRepository.save(userEntity);
		}
		
		log.debug("너 혹시 여기까진 오냐?");
		return new DefaultOAuth2User(
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
				oAuth2User.getAttributes(), "id"
				); 
	}
}
