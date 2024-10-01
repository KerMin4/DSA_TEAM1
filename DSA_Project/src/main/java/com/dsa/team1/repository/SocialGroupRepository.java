package com.dsa.team1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.SocialGroupEntity;

@Repository
public interface SocialGroupRepository extends JpaRepository<SocialGroupEntity, Integer> {

}
