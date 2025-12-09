package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;

import com.kh.final3.configuration.KakaoPayProperties;
import com.kh.final3.vo.kakaopay.KakaoPayReadyRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayReadyResponseVO;

public class KakaoPayService {
@Qualifier("kakaopayWebClient")
@Autowired
private WebClient webClient;
@Autowired
private KakaoPayProperties kakaoPayProperties;

//public KakaoPayReadyResponseVO ready(KakaoPayReadyRequestVO requestVO) {
//	
//	
	
	
	
	
	
}

