package com.kh.final3.vo.kakaopay;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoPayApproveResponseVO {
	private String aid;
	private String tid;
	private String cid;
	private String sid;
	private String partnerOrderId;
	private String partnerUserId;
	private String paymentMthodType;
	private KakaoPayAmountVO amount;

	private String itemName;//상품이름
	private String itemCode;//상품코드
	private Integer quantity;//상품 수량
	private LocalDateTime createdAt;//결제시작시각
	private LocalDateTime approvedAt;//결제승인시각
	private String payload;//결제에 대한 메모
}
