package com.kh.final3.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseListVO {
    private long productNo;
    private String productName;
    private String sellerNickname;
    private int attachmentNo;
    
    private long myBidPrice;
    private long finalPrice;
    
    private String status;

  
    private String endTime; 

    private String paymentStatus;
}