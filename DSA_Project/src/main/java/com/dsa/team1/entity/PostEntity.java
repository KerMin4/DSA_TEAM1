package com.dsa.team1.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Page		Socialing
 * Function	Reply in group post, including photos
 * @version CreateTables_9
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Post")
public class PostEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Integer postId;
	
	@ManyToOne
	@JoinColumn(name = "group_id")
	private SocialGroupEntity group;
	
	@ManyToOne
	@JoinColumn(name = "place_id")
	private PlaceEntity place;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	
	@CreatedDate
	@Column(name = "create_at", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
}
