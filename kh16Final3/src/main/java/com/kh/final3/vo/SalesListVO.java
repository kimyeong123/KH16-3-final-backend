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
public class SalesListVO {

    /* ===== 공통 식별 ===== */
    private long productNo;
    private Long orderNo;

    /* ===== 상품 정보 ===== */
    private String productName;
    private Long attachmentNo;

    /* ===== 구매자 정보 ===== */
    private String buyerNickname;   // 구매자 닉네임 (낙찰자)

    /* ===== 가격 ===== */
    private long finalPrice;         // 낙찰가 

    /* ===== 상태 ===== */
    private String productStatus;    // REGISTRATION / BIDDING / ENDED
    private String orderStatus;      // CREATED / SHIPPING_READY / SHIPPED / ...

    private LocalDateTime endTime;   // 경매 종료 시점

    /* ===== 배송 ===== */
    private boolean invoiceRegistered; // 송장 입력 여부 (courier + tracking)
}
