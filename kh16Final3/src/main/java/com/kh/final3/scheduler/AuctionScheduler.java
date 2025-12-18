/*
 * package com.kh.final3.scheduler;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.scheduling.annotation.Scheduled; import
 * org.springframework.stereotype.Component;
 * 
 * import com.kh.final3.service.AuctionScheduleService;
 * 
 * import lombok.extern.slf4j.Slf4j; import
 * net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
 * 
 * @Slf4j
 * 
 * @Component public class AuctionScheduler {
 * 
 * @Autowired private AuctionScheduleService auctionScheduleService;
 * 
 * // 종료 시간이 지난 경매 종료 처리
 * 
 * @Scheduled(cron = "0 * * * * *")
 * 
 * @SchedulerLock( name = "auctionEndScheduler", lockAtLeastFor = "PT10S",
 * lockAtMostFor = "PT2M" ) public void processAuctionEnds() {
 * auctionScheduleService.processExpiredAuctions();
 * log.debug("[SCHEDULER] Auction end check executed"); }
 * 
 * 
 * // 시작 시간이 지난 경매 시작 처리
 * 
 * @Scheduled(cron = "0 * * * * *")
 * 
 * @SchedulerLock( name = "auctionStartScheduler", lockAtLeastFor = "PT10S",
 * lockAtMostFor = "PT2M" ) public void processAuctionStarts() {
 * auctionScheduleService.processStartableAuctions();
 * log.debug("[SCHEDULER] Auction start check executed"); }
 * 
 * }
 */