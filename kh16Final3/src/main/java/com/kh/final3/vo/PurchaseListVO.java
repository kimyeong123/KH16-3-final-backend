package com.kh.final3.vo;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseListVO {
    private long productNo;
    private String productName;
    private String sellerNickname; // 판매자 닉네임
    private int attachmentNo;      // 썸네일 이미지 번호
    
    private long myBidPrice;       // 내가 입찰한 금액 중 최고가
    private long finalPrice;       // 현재 상품의 현재가(최고가)
    
    private String status;         // BIDDING(진행중), ENDED(종료)
    private Date endTime;          // 마감 시간
    private String paymentStatus;  // 결제 상태 (null, PAID 등)
}