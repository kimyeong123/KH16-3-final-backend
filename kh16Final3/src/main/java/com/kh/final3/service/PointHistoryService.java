package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
      //	충전 서비스
	@Transactional
	public void chargePoint(long memberNo, int amount) {
	    if (amount < 1) {
	        throw new IllegalArgumentException("amount must be >= 1");
	    }

	    // 1️⃣ member 테이블 포인트 증가
	    memberDao.increasePoint(memberNo, (long) amount);

	    // 2️⃣ point_history 기록
	    PointHistoryDto dto = new PointHistoryDto();
	    dto.setMemberNo(memberNo);
	    dto.setAmount((long) amount);
	    dto.setType("ADD");
	    dto.setReason("CHARGED"); 
	    pointHistoryDao.insertCharge(dto);
	}

	
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
