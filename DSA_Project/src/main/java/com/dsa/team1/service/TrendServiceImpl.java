package com.dsa.team1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.dsa.team1.dto.PlaceDTO;
import com.dsa.team1.dto.SocialGroupDTO;
import com.dsa.team1.dto.UserDTO;
import com.dsa.team1.entity.InterestEntity;
import com.dsa.team1.entity.PlaceEntity;
import com.dsa.team1.entity.SocialGroupEntity;
import com.dsa.team1.entity.UserEntity;
import com.dsa.team1.repository.InterestRepository;
import com.dsa.team1.repository.PlaceRepository;
import com.dsa.team1.repository.SocialGroupRepository;
import com.dsa.team1.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor	
public class TrendServiceImpl implements TrendService {

	private final UserRepository userRepository;
	private final SocialGroupRepository groupRepository;
	private final PlaceRepository placeRepository;
	private final InterestRepository interestRepository;
	
	private UserDTO userConvertEntityToDto(UserEntity userEntity) {
		UserDTO userDTO = UserDTO.builder()
				.userId(userEntity.getUserId())
				.userName(userEntity.getUserName())
				.name(userEntity.getName())
				.birth(userEntity.getBirth())
				.gender(userEntity.getGender())
				.phoneNumber(userEntity.getPhoneNumber())
				.email(userEntity.getEmail())
				.preferredLocation(userEntity.getPreferredLocation())
				.joinMethod(userEntity.getJoinMethod())
				.profileImage(userEntity.getProfileImage())
				.userType(userEntity.getUserType())
				.createdAt(userEntity.getCreatedAt())
				.updatedAt(userEntity.getUpdatedAt())
				.build();
		return userDTO;
	}
	
	private SocialGroupDTO groupConvertEntityToDto(SocialGroupEntity groupEntity) {
		SocialGroupDTO groupDTO = SocialGroupDTO.builder()
				.groupId(groupEntity.getGroupId())
				.groupName(groupEntity.getGroupName())
				.description(groupEntity.getDescription())
				.profileImage(groupEntity.getProfileImage())
				.location(groupEntity.getLocation())
				.interest(groupEntity.getInterest())
				.groupLeaderId(groupEntity.getGroupLeader().getUserId())
				.groupJoinMethod(groupEntity.getGroupJoinMethod())
				.memberLimit(groupEntity.getMemberLimit())
				.viewCount(groupEntity.getViewCount())
				.bookmarkCount(groupEntity.getBookmarkCount())
				.eventDate(groupEntity.getEventDate())
				.createdAt(groupEntity.getCreatedAt())
				.build();
		return groupDTO;
	}
	
	private PlaceDTO placeConvertEntityToDto(PlaceEntity placeEntity) {
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
	
	@Override
	public UserDTO getUserInfo(String userId) {
		UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
		UserDTO userDTO = userConvertEntityToDto(userEntity);
		
		return userDTO;
	}
	
	@Override
	public List<SocialGroupDTO> getGroupsByUserDemographics(String userId) {
		// TODO Auto-generated method stub
		// 유저의 나이와 성별에 맞는 그룹 목록을 가져오는 로직
		return null;
	}
	
	@Override
	public List<SocialGroupDTO> getGroupsByUserInterests(String userId) {
		UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
		List<InterestEntity> interestEntityList = interestRepository.findByUser(userEntity);
		log.info("[TrendService-getGroupsByUserInterests] Interest Entity List: {}", interestEntityList);
		
		List<SocialGroupEntity> groupEntityList = groupRepository.findAll();
		List<SocialGroupDTO> groupDtoList = new ArrayList<>();
		for (SocialGroupEntity groupEntity : groupEntityList) {
			for (InterestEntity interestEntity : interestEntityList) {
				if (interestEntity.toString().equals(groupEntity.getInterest().toString())) {
					SocialGroupDTO groupDTO = groupConvertEntityToDto(groupEntity);
					groupDtoList.add(groupDTO);
				}
			}
		}
		return groupDtoList;
	}
	
	@Override
	public List<SocialGroupDTO> getGroupsByUserLocation(String userId) {
		
		UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
		List<SocialGroupEntity> groupEntityList = groupRepository.findAll();
		List<SocialGroupDTO> groupDtoList = new ArrayList<>();
		for (SocialGroupEntity groupEntity : groupEntityList) {
			if (userEntity.getPreferredLocation() == groupEntity.getLocation()) {
				SocialGroupDTO groupDTO = groupConvertEntityToDto(groupEntity);
				groupDtoList.add(groupDTO);
			}
		}
		return groupDtoList;
	}
	
	@Override
	public List<PlaceDTO> getPlacesByUserLocation(String userId) {
		
		UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
		List<PlaceEntity> placeEntityList = placeRepository.findAll();
		List<PlaceDTO> placeDtoList = new ArrayList<>();
		for (PlaceEntity placeEntity : placeEntityList) {
			if (userEntity.getPreferredLocation() == placeEntity.getLocation()) {
				PlaceDTO placeDTO = placeConvertEntityToDto(placeEntity);
				placeDtoList.add(placeDTO);
			}
		}
		return placeDtoList;
	}
	
	@Override
	public List<PlaceDTO> getUpcomingPlaces() {
		List<PlaceEntity> placeEntityList = placeRepository.findAll();
		return null;
	}

	
	
	
}
