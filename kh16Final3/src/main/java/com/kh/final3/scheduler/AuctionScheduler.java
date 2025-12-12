package com.kh.final3.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kh.final3.service.AuctionService;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Slf4j
@Component
public class AuctionScheduler {

	@Autowired
	private AuctionService auctionService;
	
	@Scheduled(cron = "0 * * * * *")
	@SchedulerLock(name = "auctionScheduler",
									lockAtLeastFor = "PT10S", // 최소 10초는 기다려라
									lockAtMostFor = "PT2M" // 최대 2분까지 기다려라
									)
	// 스케줄러가 죽으면 그걸 정리하는 clean-up 코드가 필요하지 않을까?
	// 이미 트랜잭션 단위 설계로 충분히 안전
	public void checkAuctionTimeout() {
	    auctionService.processExpiredAuctions();
	    log.debug("[SCHEDULER] Auction check executed");
	}
	
}