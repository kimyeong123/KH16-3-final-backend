package com.kh.final3.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDto {
    private Long productNo;
    private Long sellerNo;
    private String name;
    private Long categoryCode;
    private String description;
    private Long startPrice; 
    private Long finalPrice;
    private Long instantPrice; 
    private Timestamp startTime; 
    private Timestamp endTime; 
    private String status;
    private Long buyerNo;        
    private Timestamp registrationTime;
    private Timestamp endedTime;
}