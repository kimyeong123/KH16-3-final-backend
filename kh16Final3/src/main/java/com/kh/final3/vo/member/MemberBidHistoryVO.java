package com.kh.final3.vo.member;

import java.sql.Timestamp;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberBidHistoryVO {

    private Long productNo;//상품번호
    private Long lastBidAmount;//마지막 입찰가
    private Timestamp lastBidTime;//마지막 입찰 시간
     private String productName;//상품이름
}
