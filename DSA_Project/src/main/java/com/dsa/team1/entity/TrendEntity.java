package com.dsa.team1.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Trend")
public class TrendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trendId;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private SocialGroupEntity socialGroup;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "bookmark_count")
    private Integer bookmarkCount = 0;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    // Getters and Setters
}
