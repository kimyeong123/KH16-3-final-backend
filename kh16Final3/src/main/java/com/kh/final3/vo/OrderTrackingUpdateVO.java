package com.kh.final3.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderTrackingUpdateVO {
    private Long orderNo;
    private String trackingNumber;
}