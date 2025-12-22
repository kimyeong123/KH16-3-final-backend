package com.kh.final3.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrderListVO {

    /* ===== 식별 ===== */
    private Long orderNo;
    private Long productNo;

    /* ===== 상품 ===== */
    private String productName;

    /* ===== 구매자 ===== */
    private String buyerNickname;
    
    /* ===== 판매자 ===== */
    private String sellerNickname;

    /* ===== 금액 ===== */
    private Long finalPrice;

    /* ===== 상태 ===== */
    private String orderStatus;    // CREATED / SHIPPING_READY / SHIPPED / DELIVERED / COMPLETED
    private String productStatus;  // BIDDING / ENDED (product 테이블 조인)

    /* ===== 배송 ===== */
    private boolean invoiceRegistered; // courier + tracking_number 존재 여부

    /* ===== 시간 ===== */
    private LocalDateTime createdTime; // 주문 생성
    private LocalDateTime endTime;     // 경매 종료 시점 (product.end_time)
}