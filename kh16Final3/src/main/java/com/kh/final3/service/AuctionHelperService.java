package com.kh.final3.service;

import org.springframework.stereotype.Component;

import com.kh.final3.domain.enums.EscrowStatus;
import com.kh.final3.domain.enums.PointHistoryReason;
import com.kh.final3.dto.BidDto;
import com.kh.final3.dto.EscrowLedgerDto;
import com.kh.final3.dto.OrdersDto;
import com.kh.final3.dto.PointHistoryDto;

@Component
public class AuctionHelperService {

	public EscrowLedgerDto createEscrowDto(BidDto bidDto, long escrowLedgerNo , EscrowStatus status) {
		return EscrowLedgerDto.builder()
				.escrowLedgerNo(escrowLedgerNo)
				.bidNo(bidDto.getBidNo())
				.productNo(bidDto.getProductNo())
				.bidderNo(bidDto.getBidderNo())
				.amount(bidDto.getAmount())
				.status(status.getStatus())
				.description(status.getDescription())
				.build();
	}
	
	public PointHistoryDto createPointHistoryDto(BidDto bidDto, long pointHistoryNo,  PointHistoryReason reason) {
		return	PointHistoryDto.builder()
				.pointHistoryNo(pointHistoryNo)
				.memberNo(bidDto.getBidderNo())
				.type(reason.getType())
				.amount(bidDto.getAmount())
				.reason(reason.getReason())
				.productNo(bidDto.getProductNo())
				.build();
	}
	
    public OrdersDto createOrderDto(BidDto bidDto, long orderNo, long sellerNo) {
        return OrdersDto.builder()
        		.orderNo(orderNo)
        		.productNo(bidDto.getProductNo())
        		.buyerNo(bidDto.getBidderNo())
        		.sellerNo(sellerNo)
        		.finalPrice(bidDto.getAmount())
        		.build();
    }
	
}
