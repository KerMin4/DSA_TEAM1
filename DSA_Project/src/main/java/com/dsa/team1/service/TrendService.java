package com.dsa.team1.service;

import java.util.List;

import com.dsa.team1.dto.PlaceDTO;
import com.dsa.team1.dto.SocialGroupDTO;
import com.dsa.team1.dto.UserDTO;

public interface TrendService {

	public UserDTO getUserInfo(String userId);

    public List<SocialGroupDTO> getGroupsByUserDemographics(String userId);
    
    List<SocialGroupDTO> getGroupsByUserInterests(String userId);

    public List<SocialGroupDTO> getGroupsByUserLocation(String userId);

    public List<PlaceDTO> getPlacesByUserLocation(String userId);

    public List<PlaceDTO> getUpcomingPlaces();


}
