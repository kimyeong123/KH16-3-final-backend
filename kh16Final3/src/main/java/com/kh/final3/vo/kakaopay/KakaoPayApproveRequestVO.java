package com.kh.final3.vo.kakaopay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
	
//카카오페이 결제승인 데이터
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KakaoPayApproveRequestVO {
private String partnerOrderId;
private String partnerUserId;
private String tid;
private String pgToken;
}
