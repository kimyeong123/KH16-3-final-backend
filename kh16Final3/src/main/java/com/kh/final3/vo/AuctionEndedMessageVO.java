package com.kh.final3.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionEndedMessageVO {
    private long productNo;
    private long finalPrice;
    private long buyerNo;
}