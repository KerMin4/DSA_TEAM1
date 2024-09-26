package com.dsa.team1.entity;

import java.sql.Timestamp;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Place")
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    private String title;
    private String description;
    private String location;

    @Column(name = "event_date")
    private Timestamp eventDate;

    @Column(name = "required_members")
    private Integer requiredMembers;

    @Column(name = "current_members")
    private Integer currentMembers = 0;

    private Double price;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private UserEntity vendor;

    @OneToMany(mappedBy = "place")
    private Set<UserPlaceEntity> userPlaces;

    // Getters and Setters
}

