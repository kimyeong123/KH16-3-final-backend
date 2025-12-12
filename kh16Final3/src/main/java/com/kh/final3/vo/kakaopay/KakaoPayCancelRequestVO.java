package com.kh.final3.vo.kakaopay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayCancelRequestVO {

    private String tid;          // 카카오페이 결제 고유번호
    private Integer cancelAmount; // 취소 금액
}
