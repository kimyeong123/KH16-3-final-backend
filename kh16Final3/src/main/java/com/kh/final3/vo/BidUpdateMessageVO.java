package com.kh.final3.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BidUpdateMessageVO {
    private long productNo;      // 어떤 상품의 가격이 변했는지 식별
    private long currentPrice;   // 에스크로에서 가져온 최신 최고가 (화면에 갱신될 숫자)
    private long bidderNo;       // 현재 1등인 사람의 번호 (내가 1등인지 확인할 때 사용)
}
