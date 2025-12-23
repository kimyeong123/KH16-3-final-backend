package com.kh.final3.event;

import com.kh.final3.dto.BidDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 이 BidDto는‘입찰이 발생했다’는 맥락에서 전달되는 데이터(표현 클래스 느낌?)
public class BidPlacedEvent {
    private final BidDto bidDto;
}