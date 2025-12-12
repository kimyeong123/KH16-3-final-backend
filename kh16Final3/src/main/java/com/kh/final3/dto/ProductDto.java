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
    private Timestamp endTime; // 경매 마감 예정 시각 (최초 설정 시각 또는 연장 시 갱신되는 시각)
    private String status;
    private Long buyerNo;        
    private Timestamp registrationTime;
    private Timestamp endedTime; // 경매 최종 종료 (status가 'ENDED'로 변경되는 시점 기록)
}
