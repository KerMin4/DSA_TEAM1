package com.dsa.team1.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.dsa.team1.entity.enums.UserPlaceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Page		Place
 * Function	Mapping User and Place
 * @version CreateTables_9
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "UserPlace")
public class UserPlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_place_id")
    private Integer userPlaceId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private PlaceEntity place;

    @CreatedDate
	@Column(name = "joined_at", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserPlaceStatus status;

}
