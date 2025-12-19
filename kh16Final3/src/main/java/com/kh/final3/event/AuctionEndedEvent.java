package com.kh.final3.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuctionEndedEvent {
    private final long productNo;
    private final long finalPrice;
    private final long buyerNo; // 낙찰자 없으면 0
}