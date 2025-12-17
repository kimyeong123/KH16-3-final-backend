package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.OrdersDao;
import com.kh.final3.dao.ProductDao;
import com.kh.final3.domain.enums.OrderStatus;
import com.kh.final3.domain.enums.ProductStatus;
import com.kh.final3.dto.OrdersDto;
import com.kh.final3.dto.ProductDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SettlementService {
	
	@Autowired
	private OrdersDao ordersDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private EscrowLedgerService escrowLedgerService;
	
	@Autowired
	private PointHistoryService pointHistoryService;
	
	@Transactional
	public void settleDeliveredOrder(long orderNo) {
		
		log.info("[SETTLEMENT] Try settle delivered order. orderNo={}", orderNo);
		
		// 1. 주문 및 상품 조회 및 lock
	    OrdersDto order = ordersDao.selectOneForUpdate(orderNo);
	    ProductDto product = productDao.selectOneForUpdate(order.getProductNo());
	    
	    log.debug("[SETTLEMENT] Locked order & product. orderNo={}, productNo={}, orderStatus={}, productStatus={}",
	              orderNo, product.getProductNo(), order.getStatus(), product.getStatus());

	    
	    // 2. 중복 실행 방어
	    if (!order.getStatus().equals(OrderStatus.DELIVERED.getStatus())) {
	    	log.debug("[SETTLEMENT] Skip. Order not delivered. orderNo={}, status={}",
	    	          orderNo, order.getStatus());
	    	return; 
	    }

	    // 3. 에스크로 정산
	    log.info("[SETTLEMENT] Settle escrow. orderNo={}, productNo={}",
	             orderNo, product.getProductNo());
	    escrowLedgerService.markEscrowAsSettled(product.getProductNo());
	    
	    // 4. 포인트 정산 및 포인트 로그 기록
	    log.info("[SETTLEMENT] Settle points. orderNo={}, sellerNo={}",
	             orderNo, order.getSellerNo());
	    pointHistoryService.settlePointsForOrder(order);

	    // 5. 주문 상태 확정
	    ordersDao.updateStatus(orderNo, OrderStatus.COMPLETED);
	    log.info("[SETTLEMENT] Order status updated. orderNo={}, DELIVERED → COMPLETED",
	             orderNo);
	    
	    // 6. 상품 상태 확정
	    productDao.updateStatus(product.getProductNo(), ProductStatus.COMPLETED);
	    log.info("[SETTLEMENT] Settlement completed successfully. orderNo={}", orderNo);
	}

}
