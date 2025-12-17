package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.MemberDao;
import com.kh.final3.dao.OrdersDao;
import com.kh.final3.dao.ProductDao;
import com.kh.final3.domain.enums.OrderStatus;
import com.kh.final3.domain.enums.ProductStatus;
import com.kh.final3.dto.MemberDto;
import com.kh.final3.dto.OrdersDto;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.error.InvalidOrderStateException;
import com.kh.final3.error.InvalidProductStateException;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.vo.OrderShippingRequestVO;
import com.kh.final3.vo.OrderTrackingUpdateVO;

@Service
public class ShippingService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private OrdersDao ordersDao;
    
    @Autowired
    private MemberDao memberDao;

    // 구매자가 배송지 입력시 로직
    @Transactional
    public void registerDeliveryAddress(OrderShippingRequestVO shippingRequestVO, long buyerNo) {
    	// 1. 주문, 상품 정보 조회 및 주문 lock
    	// 수령지 입력 더블 클릭 / 재전송 / 중복 요청 방지
    	OrdersDto currentOrder = ordersDao.selectOneForUpdate(shippingRequestVO.getOrderNo()); 
    	if(currentOrder == null) 
    		throw new TargetNotfoundException();
    	
        ProductDto orderProduct = productDao.selectOne(currentOrder.getProductNo());
        if(orderProduct == null) 
    		throw new TargetNotfoundException();
        
        // 2. 상태 검증
        if (!OrderStatus.CREATED.getStatus().equals(currentOrder.getStatus())) {
            throw new InvalidOrderStateException();
        }
        if (!ProductStatus.ENDED.getStatus().equals(orderProduct.getStatus())) {
            throw new InvalidProductStateException();
        }

        // 3. 권한 검증 (구매자)
        if (!currentOrder.getBuyerNo().equals(buyerNo)) {
            throw new UnauthorizationException();
        }

        // 4. 수령지 정보 등록
        ordersDao.updateShippingInfo(shippingRequestVO);
    }
    
    // 판매자가 배송정보 입력시 로직
    @Transactional
    public void shipProduct(OrderTrackingUpdateVO trackingUpdateVO, long sellerNo) {
    	// 1. 주문, 상품 정보 조회 및 모두 lock
    	// 배송 정보 등록 더블 클릭 / 재전송 / 중복 요청 방지
    	OrdersDto currentOrder = ordersDao.selectOneForUpdate(trackingUpdateVO.getOrderNo()); 
    	if(currentOrder == null) 
    		throw new TargetNotfoundException();
    	
        ProductDto orderProduct = productDao.selectOneForUpdate(currentOrder.getProductNo());
        if(orderProduct == null) 
    		throw new TargetNotfoundException();
        
        // 2. 상태 검증
        if (!currentOrder.getStatus().equals(OrderStatus.CREATED.getStatus())) {
            throw new InvalidOrderStateException();
        }
        if (!orderProduct.getStatus().equals(ProductStatus.ENDED.getStatus())) {
            throw new InvalidProductStateException();
        }
        
        // 3. 권한 검증 (판매자)
        if (!currentOrder.getSellerNo().equals(sellerNo)) {
            throw new UnauthorizationException();
        }
        
        // 4. 배송 정보 등록(상태 shippingd으로 변경)
        ordersDao.updateTrackingInfo(trackingUpdateVO);
        
        // 5. 상품 상태 변경
        productDao.updateStatus(currentOrder.getProductNo(), ProductStatus.SHIPPED);
    }
    
    // 배송 완료 처리 (현재는 관리자 페이지 버튼 트리거)
    @Transactional
    public void	markOrderDelivered(long orderNo, long memberNo) {
    	// 1. 관리자 확인(현재 시스템 구조상 관리자가 버튼 눌러서 배송완료 처리)
    	MemberDto memberDto = memberDao.selectOne(memberNo);
    	
    	if(memberDto == null || memberDto.getRole().equals("ADMIN"))
    		throw new UnauthorizationException();
    	
    	// 2. 주문 정보 조회 및 lock
        OrdersDto currentOrder = ordersDao.selectOneForUpdate(orderNo);
        
        // 3. 상태 검증
        if (currentOrder == null) 
        	throw new TargetNotfoundException();
        if (!currentOrder.getStatus().equals(OrderStatus.SHIPPING.getStatus()))
            throw new InvalidOrderStateException();

        // 4. 주문 정보 업데이트
        ordersDao.updateStatus(orderNo, OrderStatus.DELIVERED);
    }

    
}
