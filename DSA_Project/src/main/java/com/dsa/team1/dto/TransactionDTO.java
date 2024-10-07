package com.dsa.team1.dto;

import java.time.LocalDateTime;

import com.dsa.team1.entity.enums.TransactionStatus;
import com.dsa.team1.entity.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
	
	private Integer transactionId;					// PK. Transaction ID
	private Integer groupId;						// FK. SocialGroup ID
	private String leaderId;						// FK. Group Leader ID(User ID) tp receive settlement
	private Integer placeId;						// FK. Place ID
	private String userId;							// FK. User ID
	private Double amount;							// Total price
	private TransactionType transactionType;		// Whether user will pay or balance accounts
	private TransactionStatus status;				// Status of Transaction progress
	private LocalDateTime createdAt;				// When the transaction was created
	private LocalDateTime completedAt;				// When the transaction was completed
}
