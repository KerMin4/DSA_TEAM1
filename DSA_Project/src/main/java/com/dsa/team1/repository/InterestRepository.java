
package com.dsa.team1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.dsa.team1.entity.InterestEntity;

public interface InterestRepository extends JpaRepository<InterestEntity, Long> {


}