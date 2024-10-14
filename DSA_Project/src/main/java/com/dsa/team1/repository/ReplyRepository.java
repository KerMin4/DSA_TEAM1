package com.dsa.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.ReplyEntity;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {
	
	@Query("SELECT r FROM ReplyEntity r WHERE r.post.postId = :postId")
	List<ReplyEntity> findByPost_PostId(@Param("postId") Integer postId);


}
