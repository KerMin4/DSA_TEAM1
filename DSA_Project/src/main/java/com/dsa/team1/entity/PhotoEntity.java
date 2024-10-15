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
@Table(name = "Photo")
public class PhotoEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "photo_id")
	private Integer photoId;
	
	@ManyToOne
	@JoinColumn(name = "post_id")
	private PostEntity post;
	
	@Column(name = "image_name")
	private String imageName;
	
	@CreatedDate
	@Column(name = "created_at", columnDefinition = "timestamp default current_timestamp")
	private LocalDateTime createdAt;

	@ManyToOne
    @JoinColumn(name = "group_id")  // group_id로 SocialGroupEntity와 연결
    private SocialGroupEntity group;
}
