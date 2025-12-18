package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.OrdersDao;
import com.kh.final3.dao.ProductDao;
import com.kh.final3.dto.BidDto;
import com.kh.final3.dto.OrdersDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.helper.AuctionHelper;

@Service
public class OrderService {

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private AuctionHelper auctionHelper;

     // 경매 종료 시 주문 생성 (배송지 없음, CREATED 상태)
    @Transactional
    public OrdersDto createOrderForWinningBid(BidDto winningBid) {
        long orderNo = ordersDao.sequence();
        Long sellerNo = productDao.findSellerByRegProductNo(winningBid.getProductNo());
        
        if(sellerNo == null)
        	throw new TargetNotfoundException();
        
        OrdersDto ordersDto = 
        		auctionHelper.createOrderDtoByBid(winningBid, orderNo, sellerNo);
        
        ordersDao.insert(ordersDto);
        
        return ordersDto;
    }


}
