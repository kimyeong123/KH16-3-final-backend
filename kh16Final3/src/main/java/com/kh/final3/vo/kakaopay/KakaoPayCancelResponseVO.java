package com.kh.final3.vo.kakaopay;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class KakaoPayCancelResponseVO {
	private String aid;
	private String tid;
	private String cid;
	private String status;
	private String partnerOrderId;
	private String partnerUserId;
	private String paymentMethodType;
	private KakaoPayAmountVO amount;
	private KakaoPayAmountVO approvedCancelAmount;
	private KakaoPayAmountVO canceledAmount;
	private KakaoPayAmountVO cancelAvailableAmount;
	private String itemName;
	private String itemCode;
	private Integer quantity;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;//결제 요청 시각
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime approvedAt;//결제 승인 시각
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime canceledAt;//결제 취소 시각
	private String payload;//결제 추가 정보
}
