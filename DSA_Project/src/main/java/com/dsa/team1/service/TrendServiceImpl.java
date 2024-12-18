package com.dsa.team1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
		log.info("[TrendService-getGroupsByUserInterests] Social Group List: {}", groupEntityList);
		if (!(groupEntityList != null && interestEntityList != null)) {
			return null;
		}
		List<SocialGroupDTO> groupDtoList = new ArrayList<>();
		for (SocialGroupEntity groupEntity : groupEntityList) {
			for (InterestEntity interestEntity : interestEntityList) {
				
				if (groupEntity.getInterest() == null) {
		            System.out.println("Group ID: " + groupEntity.getGroupId() + " has null interest.");
		        } else if (interestEntity.toString().equals(groupEntity.getInterest().toString())) {
					SocialGroupDTO groupDTO = groupConvertEntityToDto(groupEntity);
					log.info("[TrendService-getGroupsByUserInterests] Social DTO: {}", groupDTO);
					groupDtoList.add(groupDTO);
					log.info("[TrendService-getGroupsByUserInterests] Social DTO List: {}", groupDtoList);
				}
			}
		}
		return groupDtoList;
	}
	
	@Override
	public List<SocialGroupDTO> getGroupsByUserLocation(String userId) {
		
		UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
		List<SocialGroupEntity> groupEntityList = groupRepository.findAll();
		log.info("[TrendService - getGroupsByUserLocation] User: {}, Group Entity List: {}", userEntity, groupEntityList);
		
		List<SocialGroupDTO> groupDtoList = new ArrayList<>();
		for (SocialGroupEntity groupEntity : groupEntityList) {
			log.info("TrendService - getGroupsByUserLocation] GroupEntity: {}, location: {}, preferredLocation: {}", groupEntity, groupEntity.getLocation(), userEntity.getPreferredLocation());
			if (userEntity.getPreferredLocation().contains(groupEntity.getLocation()) || groupEntity.getLocation().contains(userEntity.getPreferredLocation())) {
				log.info("TrendService - getGroupsByUserLocation] 선호 장소 일치");
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
			if (userEntity.getPreferredLocation().contains(placeEntity.getLocation()) || placeEntity.getLocation().contains(userEntity.getPreferredLocation())) {
				PlaceDTO placeDTO = placeConvertEntityToDto(placeEntity);
				placeDtoList.add(placeDTO);
			}
		}
		return placeDtoList;
	}
	
	@Override
	public List<PlaceDTO> getPlacesByEventDate() {
		List<PlaceEntity> placeEntityList = placeRepository.findAll();
		
		List<PlaceDTO> upcomingPlaces = placeEntityList.stream()
		        .filter(place -> place.getEventDate() != null) // eventDate가 null이 아닌 경우만 필터링
		        .sorted((p1, p2) -> p1.getEventDate().compareTo(p2.getEventDate())) // eventDate 기준으로 정렬
		        .map(this::placeConvertEntityToDto) // PlaceEntity를 PlaceDTO로 변환
		        .collect(Collectors.toList()); // 리스트로 수집

		    return upcomingPlaces; // 정렬된 리스트 반환
	}

	@Override
	public List<SocialGroupDTO> getGroupsByCreatedAt() {
		List<SocialGroupEntity> groupEntityList = groupRepository.findAll();
		
		List<SocialGroupDTO> recentGroupL = groupEntityList.stream()
		        .filter(place -> place.getCreatedAt() != null) 						// createdAt이 null이 아닌 경우만 필터링
		        .sorted((p1, p2) -> p1.getEventDate().compareTo(p2.getEventDate())) // createdAt 기준으로 정렬
		        .map(this::groupConvertEntityToDto) 								// SocialGroupEntity를 PlaceDTO로 변환
		        .collect(Collectors.toList()); 										// 리스트로 수집
		
		return recentGroupL;
	}
	
	
	
}
