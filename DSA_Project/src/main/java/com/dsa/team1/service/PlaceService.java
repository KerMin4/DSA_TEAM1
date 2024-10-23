package com.dsa.team1.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import com.dsa.team1.dto.PlaceDTO;
import com.dsa.team1.entity.PlaceEntity;
import com.dsa.team1.entity.enums.PlaceCategory;
import com.dsa.team1.security.AuthenticatedUser;

public interface PlaceService {
	
	public List<PlaceDTO> getAllPlaces();
	
	public Integer getMemberCountByPlace(PlaceDTO place);

    public ResponseEntity<Map<String, String>> isMember(Integer placeId, AuthenticatedUser user);

	public void checkUserAndPlaceExistence(String userId, Integer placeId) throws Exception;

	public void toggleBookmark(String userId, Integer placeId);

	public PlaceDTO findPlace(Integer placeId);

	public List<PlaceDTO> filter(String query, String category, String location);
	
	public Map<Integer, Integer> getCurrentMembers(List<PlaceDTO> placeDTOs);

	List<PlaceDTO> searchPlaces(String query, String activity, String location);

}
