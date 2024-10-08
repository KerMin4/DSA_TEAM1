package com.dsa.team1.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.dsa.team1.entity.enums.PlaceCategory;

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
 * Function	Feed in Place Page
 * @version CreateTables_9
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Place")
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Integer placeId;

    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "text")
    private String description;
    
    @Column(name = "profile_image")
    private String profileImage;
    
    @Column(name = "category", nullable = false)
    private PlaceCategory category;
    
    private String location;
    
    @Column(name="event_date", nullable = false)
    private LocalDateTime eventDate;
    
    // Minimum number of members
    @Column(name = "required_members", nullable = false)
    private Integer requiredMembers;
    
    @Column(name = "current_members", columnDefinition = "integer default 0")
    private Integer currentMembers = 0;
    
    @Column(name = "member_limit", nullable = false)
    private Integer memberLimit;
    
    @Column(name = "view_count", columnDefinition = "integer default 0")
    private Integer viewCount = 0;
    
    @Column(name = "bookmark_count", columnDefinition = "integer default 0")
    private Integer bookmarkCount = 0;
    
    private Double price;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private UserEntity vendor;

    @CreatedDate
	@Column(name = "create_at", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;

}
