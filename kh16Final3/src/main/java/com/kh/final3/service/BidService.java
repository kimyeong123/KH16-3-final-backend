package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.BidDao;
import com.kh.final3.dao.MemberDao;
import com.kh.final3.dao.ProductDao;
import com.kh.final3.domain.enums.EscrowStatus;
import com.kh.final3.domain.enums.ProductStatus;
import com.kh.final3.dto.BidDto;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.error.BidLowerThanHighestException;
import com.kh.final3.error.InvalidOrderStateException;
import com.kh.final3.error.PointNotSufficientException;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.vo.AuctionEndRequestVO;

@Service
public class BidService {
	
	@Autowired
	private BidDao bidDao;
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private ProductDao productDao; 
	
	@Autowired
	private EscrowLedgerService escrowLedgerService;
	
	@Autowired
	private PointHistoryService pointHistoryService;
	
	@Autowired
	private AuctionService auctionService;
	
	@Transactional
	public void placeBid(BidDto incomingBid) {
		// 1. 동시성 처리. 상품 로우를 잠가서 경매 상태와 최고가 업데이트를 보호. 해당 로우에 대한 변경(Update, Delete)을 방지
	    ProductDto currentProduct = productDao.selectOneForUpdate(incomingBid.getProductNo());
		if(currentProduct == null)
			throw new TargetNotfoundException();
	    
		// 2. 입찰 상품이 현재 경매 진행중인지 확인
		String currentProductStatus = currentProduct.getStatus();
		if(!currentProductStatus.equals(ProductStatus.BIDDING.getStatus()))
			throw new InvalidOrderStateException();
		
		// 3. 입찰 금액이 현재 포인트보다 큰지 확인
		if(incomingBid.getAmount() > memberDao.findMemberPoint(incomingBid.getBidderNo()))
			throw new PointNotSufficientException();
		
		// 4. 최고 입찰 가져오기
		BidDto previousHighestBid = bidDao.findHighestBid(currentProduct.getProductNo());
		
		// 5. 즉시 구매가 가져오기
		Long instantPrice = currentProduct.getInstantPrice();
		
		// 6. 입찰 금액이 즉시 구매가 이상일 경우 로직 처리 후 경매 종료
		if(instantPrice != null && incomingBid.getAmount() >= instantPrice) {
			processInstantBuy(incomingBid, previousHighestBid);
	        return;
	    }
		
		if(previousHighestBid != null) {
			// 7. 입찰 금액이 현재 가장 높은 입찰금보다 높은지 확인
			if(previousHighestBid.getAmount() >= incomingBid.getAmount()) 
				throw new BidLowerThanHighestException();
			
			// 8. 최고 입찰 내역의 해당 에스크로 상태 수정 및 포인트 반환/포인트 내역 기록 
			refundPreviousBid(previousHighestBid);
		}
		
		// 9. 입찰 기록
		insertBid(incomingBid);
	}

	// private 메소드에서는 Transactional이 작동하지않음
	// 단독 호출 되는 경우가 현재까지는 없음
	private void insertBid(BidDto incomingBid) {
		// 입찰 등록
		incomingBid.setBidNo(bidDao.sequence()); // 시퀀스 설정
		bidDao.insert(incomingBid);
		// 에스크로 정보 등록
		escrowLedgerService.registerEscrowForBid(incomingBid);
		// 회원 포인트 차감 및 로그 기록
		pointHistoryService.lockPointsForBid(incomingBid);
	}
	
	private void refundPreviousBid(BidDto previousHighestBid) {
		// 가장 높은 입찰 내역의 에스크로 상태 변경
		escrowLedgerService.updateEscrowForBid(previousHighestBid.getBidNo(), EscrowStatus.RELEASED);
		// 해당 내역의 회원에게 포인트 반환 및 로그 기록
		pointHistoryService.refundPointsForBid(previousHighestBid);
	}
	
	private void processInstantBuy(BidDto incomingBid, BidDto previousHighestBid) {
		 // 기존 최고가가 있다면 환불
		if (previousHighestBid != null)
	        refundPreviousBid(previousHighestBid);
		
		// 새 입찰 등록 (즉시구매 입찰)
	   insertBid(incomingBid);
	    
	    // 경매종료 변경
	    AuctionEndRequestVO auctionEndRequestVO = 
	    		AuctionEndRequestVO.builder()
	    			.productNo(incomingBid.getProductNo())
	    			.buyerNo(incomingBid.getBidderNo())
	    			.finalPrice(incomingBid.getAmount())
	    			.build();
	    
	    // 경매 종료 처리 및 에스크로 상태 정산대기로 변경 
	    auctionService.closeAuction(auctionEndRequestVO, incomingBid.getBidNo());
	}
	
}
