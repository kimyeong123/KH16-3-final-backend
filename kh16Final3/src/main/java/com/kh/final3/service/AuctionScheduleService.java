package com.kh.final3.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.final3.dao.ProductDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuctionScheduleService {

	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private AuctionService auctionService;
	
	 // 트랜젝션을 붙이지 않는 이유는 내부의 반복문 때문(내부사용 메소드에 transaction 걸어서 상품단위로 트랜젝션 일어나게 유도)
    public void processExpiredAuctions() {
        List<Long> expiredProductNos = productDao.findExpiredProductNos();

        if (expiredProductNos.isEmpty()) {
            log.debug("[AUCTION-SCHEDULE] No expired auctions");
            return;
        }

        log.info("[AUCTION-SCHEDULE] Expired auctions found. count={}",
                 expiredProductNos.size());
        
        List<Long> failedNos = new ArrayList<>();
        
        for (Long productNo : expiredProductNos) {
            try {
                auctionService.handleSingleAuctionEnd(productNo); // endAuction or noBidAuction
            } catch (Exception e) {
                failedNos.add(productNo); // 실패한 번호 기록
                log.warn("[AUCTION-SCHEDULE] End auction failed (1st). productNo={}",
                        productNo, e);
            }
        }

        if (!failedNos.isEmpty()) {
            log.info("[AUCTION-SCHEDULE] Retry expired auctions. retryCount={}",
                     failedNos.size());
        }
        
        // 실패한 상품 재시도 1회
        for (Long failedNo : failedNos) {
            try {
            	auctionService.handleSingleAuctionEnd(failedNo);
            } catch (Exception e) {
            	log.error("[AUCTION-SCHEDULE] End auction failed twice. productNo={}",
                        failedNo, e);
            }
        }
    }
    
    public void processStartableAuctions() {
        List<Long> startableProductNos = productDao.findStartableProductNos();

        if (startableProductNos.isEmpty()) {
            log.debug("[AUCTION-SCHEDULE] No startable auctions");
            return;
        }
        
        log.info("[AUCTION-SCHEDULE] Startable auctions found. count={}",
                startableProductNos.size());
        
        List<Long> failedNos = new ArrayList<>();

        for (Long productNo : startableProductNos) {
            try {
            	auctionService.handleSingleAuctionStart(productNo);
            } catch (Exception e) {
                failedNos.add(productNo);
                log.warn("[AUCTION-SCHEDULE] Start auction failed (1st). productNo={}",
                        productNo, e);
            }
        }
        
        if (!failedNos.isEmpty()) {
            log.info("[AUCTION-SCHEDULE] Retry start auctions. retryCount={}",
                     failedNos.size());
        }
        
        // 실패 재시도 1회
        for (Long failedNo : failedNos) {
            try {
            	auctionService.handleSingleAuctionStart(failedNo);
            } catch (Exception e) {
            	log.error("[AUCTION-SCHEDULE] Start auction failed twice. productNo={}",
                        failedNo, e);
            }
        }
    }
	
}
