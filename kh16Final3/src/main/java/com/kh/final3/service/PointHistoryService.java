package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.final3.configuration.SettlementProperties;
import com.kh.final3.dao.MemberDao;
import com.kh.final3.dao.OrdersDao;
import com.kh.final3.dao.PointHistoryDao;
import com.kh.final3.domain.enums.PointHistoryReason;
import com.kh.final3.domain.records.SettlementResult;
import com.kh.final3.dto.BidDto;
import com.kh.final3.dto.OrdersDto;
import com.kh.final3.dto.PointHistoryDto;
import com.kh.final3.helper.AuctionHelper;

@Service
public class PointHistoryService {
	
	@Autowired
	private PointHistoryDao pointHistoryDao;
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private AuctionHelper auctionHelperService;
	
	@Autowired
	private SettlementProperties settlementProperties;
	
	public void lockPointsForBid (BidDto bidDto) {
		memberDao.deductMemberPoint(bidDto.getBidderNo(), bidDto.getAmount());
		
		long pointHistoryNo = pointHistoryDao.sequence();
		PointHistoryDto pointHistoryDto = 
				auctionHelperService.createPointHistoryDtoByBid(bidDto, pointHistoryNo, PointHistoryReason.BID_LOCKED);
		
		pointHistoryDao.insert(pointHistoryDto);
	}
	
	public void refundPointsForBid (BidDto bidDto) {
		memberDao.addMemberPoint(bidDto.getBidderNo(), bidDto.getAmount());
		
		long pointHistoryNo = pointHistoryDao.sequence();
		PointHistoryDto pointHistoryDto = 
				auctionHelperService.createPointHistoryDtoByBid(bidDto, pointHistoryNo, PointHistoryReason.BID_REFUNDED);
		
		pointHistoryDao.insert(pointHistoryDto);
	}
	
	public void settlePointsForOrder (OrdersDto orderDto) {
		SettlementResult settlementResult = settlementProperties.calculateSettlement(orderDto.getFinalPrice());
		
		memberDao.addMemberPoint(orderDto.getSellerNo(), settlementResult.amount());
		
		long pointHistoryNo = pointHistoryDao.sequence();
		PointHistoryDto pointHistoryDto = 
				auctionHelperService.createPointHistoryDtoByOrder(
						orderDto, pointHistoryNo, 
						settlementResult, PointHistoryReason.SETTLEMENT);
		
		pointHistoryDao.insertSettlementHistory(pointHistoryDto);
	}
	
}
