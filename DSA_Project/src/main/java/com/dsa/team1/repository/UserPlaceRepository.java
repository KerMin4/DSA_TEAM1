package com.dsa.team1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.UserPlaceEntity;

@Repository
public interface UserPlaceRepository extends JpaRepository<UserPlaceEntity, Integer> {

}
