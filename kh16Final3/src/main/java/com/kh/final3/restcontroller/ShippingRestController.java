package com.kh.final3.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.service.ShippingService;
import com.kh.final3.vo.OrderShippingRequestVO;
import com.kh.final3.vo.OrderTrackingUpdateVO;
import com.kh.final3.vo.TokenVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/orders/{orderNo}/shipping")
public class ShippingRestController {

	@Autowired
	private ShippingService shippingService;
	
	 // 구매자: 배송지 입력
	@PutMapping("/address")
	public void registerShippingAddress(
			@PathVariable long orderNo,
		    @RequestBody OrderShippingRequestVO shippingRequestVO,
		    @RequestAttribute TokenVO tokenVO
		    ) {
		long memberNo = tokenVO.getMemberNo();
		shippingRequestVO.setOrderNo(orderNo);
		shippingService.registerDeliveryAddress(shippingRequestVO, memberNo);
	}
	
	 // 판매자: 송장 입력 (배송 시작)
    @PutMapping("/tracking")
    public void shipProduct(
            @PathVariable long orderNo,
            @RequestBody OrderTrackingUpdateVO trackingUpdateVO,
            @RequestAttribute TokenVO tokenVO
    		) {
    	long memberNo = tokenVO.getMemberNo();
    	trackingUpdateVO.setOrderNo(orderNo);
        shippingService.shipProduct(trackingUpdateVO, memberNo);
    }
	
    // 관리자: 배송 완료 처리
    @PutMapping("/delivered")
    public void markDelivered(
            @PathVariable long orderNo,
            @RequestAttribute TokenVO tokenVO
    		) {
    	long memberNo = tokenVO.getMemberNo();
        shippingService.markOrderDelivered(orderNo, memberNo);
    }
    
}
