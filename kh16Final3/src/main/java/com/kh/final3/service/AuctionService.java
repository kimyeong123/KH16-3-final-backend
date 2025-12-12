package com.kh.final3.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.BidDao;
import com.kh.final3.dao.EscrowLedgerDao;
import com.kh.final3.dao.ProductDao;
import com.kh.final3.domain.enums.EscrowStatus;
import com.kh.final3.domain.enums.ProductStatus;
import com.kh.final3.dto.BidDto;
import com.kh.final3.vo.AuctionEndRequestVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuctionService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private EscrowLedgerDao escrowLedgerDao;
    
    @Autowired
    private BidDao bidDao;

    // 트랜젝션을 붙이지 않는 이유는 내부의 반복문 때문(내부사용 메소드에 transaction 걸어서 상품단위로 트랜젝션 일어나게 유도)
    public void processExpiredAuctions() {
        List<Long> expiredProductNos = productDao.findExpiredProductNos();

        List<Long> failedNos = new ArrayList<>();
        
        for (Long productNo : expiredProductNos) {
            try {
                handleSingleAuction(productNo); // endAuction or noBidAuction
            } catch (Exception e) {
                failedNos.add(productNo); // 실패한 번호 기록
            }
        }

        // 실패한 상품 재시도 1회
        for (Long failedNo : failedNos) {
            try {
                handleSingleAuction(failedNo);
            } catch (Exception e) {
            	log.debug("경매 종료 처리 실패 (두 번 실패): productNo={}", failedNo);
            }
        }
    }
    
    @Transactional
	public void closeAuction(AuctionEndRequestVO endRequestVO, long bidNo) {
		productDao.updateEndedAuction(endRequestVO);
		long escrowNo = escrowLedgerDao.findEscrowNoByBidNo(bidNo);
		escrowLedgerDao.updateStatusByEscrowNo(escrowNo, EscrowStatus.PENDING_SETTLEMENT);
	}
    
    @Transactional
    public void noBidAuction(long productNo) {
    	productDao.updateStatus(productNo, ProductStatus.ENDED);
    }
    
    @Transactional
    private void handleSingleAuction(Long productNo) {
        BidDto winningBid = bidDao.findHighestBid(productNo);

        if (winningBid == null) {
            noBidAuction(productNo);
        } else {
        	closeAuction(
                AuctionEndRequestVO.builder()
                    .productNo(productNo)
                    .buyerNo(winningBid.getBidderNo())
                    .finalPrice(winningBid.getAmount())
                    .build(), winningBid.getBidNo()
            );
        }
    }
    
}
