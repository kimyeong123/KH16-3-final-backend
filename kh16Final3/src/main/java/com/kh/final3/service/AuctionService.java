package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.BidDao;
import com.kh.final3.dao.ProductDao;
import com.kh.final3.domain.enums.EscrowStatus;
import com.kh.final3.domain.enums.ProductStatus;
import com.kh.final3.dto.BidDto;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.event.AuctionEndedEvent;
import com.kh.final3.vo.AuctionEndRequestVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuctionService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private BidDao bidDao;
    
    @Autowired
    private EscrowLedgerService escrowLedgerService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    /*
    	상태 전이의 결정권을 가진 Aggregate Root에 row lock을 걸면,
    	해당 락 범위 내에서 수행되는 하위 엔티티들의 수정은
    	추가적인 개별 락 없이도 일관성이 보장된다.
    */
    // Bid / Escrow는 단독 상태 전이 유스케이스가 없음. (별도의 단독 수정 public 메소드)
    // Product(Aggregate Root)에서 row lock을 선점하므로 별도 락 불필요. (동시성 보장이 충분함)
    @Transactional
    public void handleSingleAuctionEnd(Long productNo) {
    	log.debug("[AUCTION-END] Try end auction. productNo={}", productNo);
    	ProductDto product = productDao.selectOneForUpdate(productNo);

    	if (!product.getStatus().equals(ProductStatus.BIDDING.getStatus())) {
    		log.debug("[AUCTION-END] Skip. Already ended. productNo={}, status={}",
    	              productNo, product.getStatus());
    	    return; // 이미 종료된 경우에 대한 검사(스케줄러 중복 실행시에 필수 상품의 경매진행상태 재확인)
    	}
    	
        BidDto winningBid = bidDao.findHighestBid(productNo);

        if (winningBid == null) {
        	log.info("[AUCTION-END] No bid. productNo={}", productNo);
            noBidAuction(productNo);
        } else {
        	log.info("[AUCTION-END] productNo={} | BIDDING → ENDED | bidNo={} | buyerNo={} | price={}",
        	         productNo, winningBid.getBidNo(), winningBid.getBidderNo(), winningBid.getAmount());
        	closeAuction(winningBid);
        }
    }
    
    @Transactional
    public void handleSingleAuctionStart(Long productNo) {
    	log.info("[AUCTION-START] Try start auction. productNo={}", productNo);
        ProductDto product = productDao.selectOneForUpdate(productNo);

        // 스케줄러 중복 실행 / 재시도 대비
        if (!product.getStatus().equals(ProductStatus.REGISTRATION.getStatus())) {
        	log.debug("[AUCTION-START] Skip. Not registration state. productNo={}, status={}",
                    productNo, product.getStatus());
        	return;
        }
        
        productDao.updateStatus(productNo, ProductStatus.BIDDING);
        log.info("[AUCTION-START] Auction started. productNo={}", productNo);
    }
    
	public void closeAuction(BidDto winningBid) {
		AuctionEndRequestVO endRequestVO = 
					AuctionEndRequestVO
					.builder()
					.productNo(winningBid.getProductNo())
					.buyerNo(winningBid.getBidderNo())
					.finalPrice(winningBid.getAmount())
					.build();
					
		productDao.updateProductOnAuctionEnd(endRequestVO);
		escrowLedgerService.updateEscrowForBid(winningBid.getBidNo(), EscrowStatus.PENDING_SETTLEMENT);
		orderService.createOrderForWinningBid(winningBid);
		
		// 이벤트 발행
		eventPublisher.publishEvent(
		        new AuctionEndedEvent(
		            endRequestVO.getProductNo(),
		            endRequestVO.getFinalPrice(),
		            endRequestVO.getBuyerNo()
		        )
		    );
	}
    
	public void noBidAuction(long productNo) {
    	productDao.updateStatus(productNo, ProductStatus.ENDED);
    	
    	eventPublisher.publishEvent(
    	        new AuctionEndedEvent(productNo, 0L, 0L)
    	    );
    }
    
}
