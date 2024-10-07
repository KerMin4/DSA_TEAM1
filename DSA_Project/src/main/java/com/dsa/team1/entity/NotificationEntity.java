package com.dsa.team1.entity;

import java.time.LocalDateTime;

import org.hibernate.usertype.UserType;
import org.springframework.data.annotation.CreatedDate;

import com.dsa.team1.entity.enums.JoinMethod;

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
 * Page		Top right of all logged-in pages
 * Function	Notify group membership, etc
 * @version	CreateTables_9
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Notification")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(columnDefinition = "text")
    private String message;
    
    @Column(name = "read_status", columnDefinition = "Boolean Default false")
    private Boolean readStatus = false;
    
    @CreatedDate
	@Column(name = "created_at", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;

}
