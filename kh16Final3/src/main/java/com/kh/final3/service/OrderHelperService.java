package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.OrdersDao;
import com.kh.final3.domain.enums.OrderStatus;
import com.kh.final3.dto.OrdersDto;

@Service
public class OrderHelperService {
	
	@Autowired
	private OrdersDao ordersDao; 
	
	@Transactional
    public void createOrder(OrdersDto ordersDto) {
        long orderNo = ordersDao.sequence();
        ordersDto.setOrderNo(orderNo);
        ordersDto.setStatus(OrderStatus.CREATED.getStatus());
        ordersDao.insert(ordersDto);
    }
	
	
}
