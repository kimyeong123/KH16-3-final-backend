package com.kh.final3.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.service.BidService;
import com.kh.final3.vo.BidRequestVO;
import com.kh.final3.vo.TokenVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/products/{productNo}/bid")
public class BidRestController {
	
	@Autowired
	private BidService bidService;
	
	@PostMapping("/")
	public void bid(@PathVariable long productNo,
		    @RequestBody BidRequestVO bidRequestVO,
		    @RequestAttribute TokenVO tokenVO
		    ) {
		long memberNo = tokenVO.getMemberNo();
		log.info("상품번호: {}, 멤버번호: {}, 금액: {}", productNo, memberNo, bidRequestVO.getAmount());
		bidService.placeBid(memberNo, productNo, bidRequestVO.getAmount());
	}
	
	
}
