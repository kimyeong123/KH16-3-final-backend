package com.kh.final3.dto;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdersDto {
    private Long orderNo;       
    private Long productNo;  
    private Long buyerNo;        // 구매자
    private Long sellerNo;       // 판매자
    private Long finalPrice;     // 최종가
    // 배송지 정보
    private String receiverName;
    private String receiverPhone;
    private String post;
    private String address1;
    private String address2;
    // 송장 정보
    private String courier;
    private String trackingNumber;
    // 주문 상태: CREATED, SHIPPING, DELIVERED, COMPLETED, CANCELLED
    private String status;
    // 시간 정보
    private Timestamp createdTime;
    private Timestamp updatedTime;
    private Timestamp completedTime;
}
