package com.kh.final3.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.final3.configuration.SettlementProperties;
import com.kh.final3.dao.OrdersDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SettlementScheduleService {

	@Autowired
    private OrdersDao ordersDao;
	
	@Autowired
    private SettlementService settlementService;

	@Autowired
	private SettlementProperties settlementProperties;
	
	public void processSettlementTargets() {
		log.debug("[SETTLEMENT-SCHEDULE] start");
        List<Long> orderNos =
            ordersDao.findSettlementTargets(settlementProperties.getDelayDays());

        if (orderNos.isEmpty()) {
            log.debug("[SETTLEMENT-SCHEDULE] No settlement targets");
            return;
        }

        log.info("[SETTLEMENT-SCHEDULE] Settlement targets found. orderNos={}",
                orderNos);

        List<Long> failedNos = new ArrayList<>();

        // 1차 처리
        for (Long orderNo : orderNos) {
            try {
                settlementService.settleDeliveredOrder(orderNo);
            } catch (Exception e) {
                failedNos.add(orderNo);
                log.warn("[SETTLEMENT-SCHEDULE] Settlement failed (1st). orderNo={}",
                         orderNo, e);
            }
        }

        if (!failedNos.isEmpty()) {
            log.info("[SETTLEMENT-SCHEDULE] Retry settlement. retryCount={}",
                     failedNos.size());
        }

        // 실패 건 재시도 1회
        for (Long failedNo : failedNos) {
            try {
                settlementService.settleDeliveredOrder(failedNo);
            } catch (Exception e) {
                log.error("[SETTLEMENT-SCHEDULE] Settlement failed twice. orderNo={}",
                          failedNo, e);
            }
        }
    }
}
