package com.kh.final3.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kh.final3.service.SettlementScheduleService;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Slf4j
@Component
public class SettlementScheduler {
	
	@Autowired
	private SettlementScheduleService settlementScheduleService;
	
//	@Scheduled(cron = "0 */10 * * * *")
	@Scheduled(cron = "0 * * * * *")
	@SchedulerLock(
	    name = "settlementScheduler",
	    lockAtLeastFor = "PT2S",
	    lockAtMostFor  = "PT5M"
	)
	public void runSettlement() {
		log.info("[SCHEDULER] settlement check");
	    settlementScheduleService.processSettlementTargets();
	}
}
