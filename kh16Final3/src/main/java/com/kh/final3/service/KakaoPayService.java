package com.kh.final3.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.final3.configuration.KakaoPayProperties;
import com.kh.final3.vo.kakaopay.KakaoPayApproveRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayApproveResponseVO;
import com.kh.final3.vo.kakaopay.KakaoPayCancelRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayCancelResponseVO;
import com.kh.final3.vo.kakaopay.KakaoPayOrderRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayOrderResponseVO;
import com.kh.final3.vo.kakaopay.KakaoPayReadyRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayReadyResponseVO;


@Service
public class KakaoPayService {
@Qualifier("kakaopayWebClient")
@Autowired
private WebClient webClient;
@Autowired
private KakaoPayProperties kakaoPayProperties;

public KakaoPayReadyResponseVO ready(KakaoPayReadyRequestVO requestVO) {

	Map<String, String> body =new HashMap<>();
	body.put("cid", kakaoPayProperties.getCid());
	body.put("partner_order_id", requestVO.getPartnerOrderId()); 
	body.put("partner_order_id", requestVO.getPartnerOrderId());
	body.put("partner_user_id", requestVO.getPartnerUserId());
	body.put("item_name", requestVO.getItemName());
	body.put("quantity", "1");
	body.put("total_amount", String.valueOf(requestVO.getTotalAmount()));
	body.put("tax_free_amount", "0");
	
	String currentPath = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
	body.put("approval_url", currentPath + "/success/" + requestVO.getPartnerOrderId());//성공 시 연결할 주소(카카오페이에 등록된 플랫폼 주소)
	body.put("cancel_url", currentPath + "/cancel/" + requestVO.getPartnerOrderId());//취소 시 연결할 주소(카카오페이에 등록된 플랫폼 주소)
	body.put("fail_url", currentPath + "/fail/" + requestVO.getPartnerOrderId());//실패 시 연결할 주소(카카오페이에 등록된 플랫폼 주소)
	
	KakaoPayReadyResponseVO response = webClient.post()
	.uri("/online/v1/payment/ready")
	.bodyValue(body)
.retrieve()//응답을 수신하겠다
	.bodyToMono(KakaoPayReadyResponseVO.class)
	.block();
	return response;
}

public KakaoPayApproveResponseVO approve(KakaoPayApproveRequestVO requestVO) {
	Map<String, String> body = new HashMap<>();
	//body에 필요한 정보들을 담음 (카카오페이 문서 확인)
	body.put("cid", kakaoPayProperties.getCid());
	body.put("partner_order_id", requestVO.getPartnerOrderId());
	body.put("partner_user_id", requestVO.getPartnerUserId());
	body.put("tid", requestVO.getTid());
	body.put("pg_token", requestVO.getPgToken());
	
	KakaoPayApproveResponseVO response = webClient.post()//POST 요청
			.uri("/online/v1/payment/approve")//webClient에 기본주소 설정이 있을 경우
			.bodyValue(body)//요청에 첨부할 데이터 설정
		.retrieve()//응답을 수신하겠다
			.bodyToMono(KakaoPayApproveResponseVO.class)//데이터는 한번에 오고(Mono) 형태는 KakaoPayApproveResponseVO이다 (↔ 연속적으로 오면 Flux)
			.block();//동기적으로 변환하여 응답이 올때까지 기다려라! (RestTemplate과 같아짐)
	
	return response;
}
//결제 조회
public KakaoPayOrderResponseVO order(KakaoPayOrderRequestVO requestVO) {
	Map<String, String> body = new HashMap<>();
	body.put("cid", kakaoPayProperties.getCid());
	body.put("tid", requestVO.getTid());
	
	KakaoPayOrderResponseVO responseVO = webClient.post()//POST 요청
			.uri("/online/v1/payment/order")//webClient에 기본주소 설정이 있을 경우
			.bodyValue(body)//요청에 첨부할 데이터 설정
		.retrieve()//응답을 수신하겠다
			.bodyToMono(KakaoPayOrderResponseVO.class)//데이터는 한번에 오고(Mono) 형태는 KakaoPayApproveResponseVO이다 (↔ 연속적으로 오면 Flux)
			.block();//동기적으로 변환하여 응답이 올때까지 기다려라! (RestTemplate과 같아짐)
	
	return responseVO;
}
//결제 취소
public KakaoPayCancelResponseVO cancel(KakaoPayCancelRequestVO requestVO) {
	Map<String, String> body = new HashMap<>();
	body.put("cid", kakaoPayProperties.getCid());
	body.put("tid", requestVO.getTid());
	body.put("cancel_amount", String.valueOf(requestVO.getCancelAmount()));
	body.put("cancel_tax_free_amount", "0");
	
	KakaoPayCancelResponseVO responseVO = webClient.post()//POST 요청
			.uri("/online/v1/payment/cancel")//webClient에 기본주소 설정이 있을 경우
			.bodyValue(body)//요청에 첨부할 데이터 설정
		.retrieve()//응답을 수신하겠다
			.bodyToMono(KakaoPayCancelResponseVO.class)//데이터는 한번에 오고(Mono) 형태는 KakaoPayApproveResponseVO이다 (↔ 연속적으로 오면 Flux)
			.block();//동기적으로 변환하여 응답이 올때까지 기다려라! (RestTemplate과 같아짐)
	
	return responseVO;
}
}




