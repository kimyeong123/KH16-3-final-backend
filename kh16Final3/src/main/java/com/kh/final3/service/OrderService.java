package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.OrdersDao;
import com.kh.final3.dao.ProductDao;
import com.kh.final3.domain.enums.OrderStatus;
import com.kh.final3.dto.BidDto;
import com.kh.final3.dto.OrdersDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.helper.AuctionHelper;
import com.kh.final3.vo.OrderShippingRequestVO;

@Service
public class OrderService {

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private AuctionHelper auctionHelperService;

     // 경매 종료 시 주문 생성 (배송지 없음, CREATED 상태)
    @Transactional
    public OrdersDto createOrderForWinningBid(BidDto winningBid) {
        long orderNo = ordersDao.sequence();
        Long sellerNo = productDao.findSellerNoByProductNo(winningBid.getProductNo());
        if(sellerNo == null)
        	throw new TargetNotfoundException();
        
        OrdersDto ordersDto = 
        		auctionHelperService.createOrderDtoByBid(winningBid, orderNo, sellerNo);
        ordersDao.insert(ordersDto);
        return ordersDto;
    }

     // 구매자가 배송지 입력
    @Transactional
    public void registerShippingInfo(OrderShippingRequestVO shippingRequestVO) {
        ordersDao.updateShippingInfo(shippingRequestVO);
    }

     // 판매자가 송장 입력 → 배송 시작
//    @Transactional
//    public void registerTrackingInfo(long orderNo, String courier, String trackingNumber) {
//        ordersDao.updateTrackingInfo(orderNo, courier, trackingNumber);
//        ordersDao.updateStatus(orderNo, OrderStatus.SHIPPING.getStatus());
//    }

     // 구매자 수령 완료
    @Transactional
    public void completeOrder(long orderNo) {
        ordersDao.completeOrder(orderNo);
    }
}
