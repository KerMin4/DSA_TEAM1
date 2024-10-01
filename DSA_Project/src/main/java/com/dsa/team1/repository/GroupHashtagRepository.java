package com.dsa.team1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.GroupHashtagEntity;

@Repository
public interface GroupHashtagRepository extends JpaRepository<GroupHashtagEntity, Integer> {

}
