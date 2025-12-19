package com.kh.final3.helper;

import org.springframework.stereotype.Component;

import com.kh.final3.domain.enums.EscrowStatus;
import com.kh.final3.domain.enums.OrderStatus;
import com.kh.final3.domain.enums.PointHistoryReason;
import com.kh.final3.domain.records.SettlementResult;
import com.kh.final3.dto.BidDto;
import com.kh.final3.dto.EscrowLedgerDto;
import com.kh.final3.dto.OrdersDto;
import com.kh.final3.dto.PointHistoryDto;

@Component
public class AuctionHelper {

	public BidDto createBidDto(long bidNo, long memberNo, long productNo, long amount) {
		return BidDto.builder()
				.bidNo(bidNo)
				.productNo(productNo)
				.bidderNo(memberNo)
				.amount(amount)
				.build();
	}
	
	public EscrowLedgerDto createEscrowDtoByBid(BidDto bidDto, long escrowLedgerNo, EscrowStatus status) {
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
	
	public PointHistoryDto createPointHistoryDtoByBid(BidDto bidDto, long pointHistoryNo, PointHistoryReason reason) {
		return	PointHistoryDto.builder()
				.pointHistoryNo(pointHistoryNo)
				.memberNo(bidDto.getBidderNo())
				.type(reason.getType())
				.amount(bidDto.getAmount())
				.reason(reason.getReason())
				.productNo(bidDto.getProductNo())
				.build();
	}
	
    public OrdersDto createOrderDtoByBid(BidDto bidDto, long orderNo, long sellerNo) {
        return OrdersDto.builder()
        		.orderNo(orderNo)
        		.productNo(bidDto.getProductNo())
        		.buyerNo(bidDto.getBidderNo())
        		.sellerNo(sellerNo)
        		.finalPrice(bidDto.getAmount())
        		.status(OrderStatus.CREATED.getStatus())
        		.build();
    }
	
    public PointHistoryDto createPointHistoryDtoByOrder(
    		OrdersDto orderDto, long pointHistoryNo, 
    		SettlementResult settlementResult ,PointHistoryReason reason) {
    	
		return	PointHistoryDto.builder()
				.pointHistoryNo(pointHistoryNo)
				.memberNo(orderDto.getSellerNo())
				.type(reason.getType())
				.amount(settlementResult.amount())
				.feeAmount(settlementResult.feeAmount())
				.reason(reason.getReason())
				.productNo(orderDto.getProductNo())
				.build();
	}
    
}
