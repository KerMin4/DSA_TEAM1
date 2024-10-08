package com.dsa.team1.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.entity.enums.JoinMethod;
import com.dsa.team1.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	private final UserRepository ur;
	private final BCryptPasswordEncoder passwordEncoder;
	@Override
	public void join(String userid, String password, String phone, String email, String location, String name, String username/*, List<String> interests */) {
		UserEntity userEntity = UserEntity.builder()
								.userId(userid)
								.password(passwordEncoder.encode(password))
								.phoneNumber(phone)
								.email(email)
								.preferredLocation(location)
								.userName(username)
								.name(name)
								.build();
		ur.save(userEntity);
	}
}
