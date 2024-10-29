package com.dsa.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsa.team1.entity.PlaceEntity;
import com.dsa.team1.entity.enums.Interest;
import com.dsa.team1.entity.enums.PlaceCategory;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Integer> {
	
	List<PlaceEntity> findByCategory(PlaceCategory category);

	List<PlaceEntity> findByCategoryAndTitleContaining(PlaceCategory activity, String title);

	List<PlaceEntity> findByTitleContainingOrDescriptionContaining(String query, String query2);

	List<PlaceEntity> findByTitleContaining(String query);
	
	int countCurrentMembersByPlaceId(Integer placeId);
	
	@Query("SELECT p FROM PlaceEntity p " +
		       "WHERE (:query IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
		       "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " + // 검색어 필터링
		       "AND (:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) " + // 지역 필터링
		       "AND (:category IS NULL OR p.category = :category)") // 카테고리 필터링
	List<PlaceEntity> filterPlaces(@Param("query") String query,
									@Param("location") String location,
                                    @Param("category") PlaceCategory category);

	@Query("SELECT p FROM PlaceEntity p " +
           "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
	List<PlaceEntity> searchByGroupNameOrDescription(@Param("query") String query);

//	List<PlaceEntity> findByUser_UserId(String userId);

}
