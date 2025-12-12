package com.kh.final3.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderShippingRequestVO {
	private Long orderNo;
    private String receiverName;
    private String receiverPhone;
    private String post;
    private String address1;
    private String address2;
}
