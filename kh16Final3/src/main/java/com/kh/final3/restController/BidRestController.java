package com.kh.final3.restController;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="경매 관리 컨트롤러")

@CrossOrigin //CORS는 “브라우저 → 웹서버“ 사이에서만 적용되는 보안 규칙이다. // DB는 예외
@RestController
@RequestMapping("/bid")
public class BidRestController {

	
}
