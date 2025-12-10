package com.kh.final3.restcontroller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.kh.final3.service.KakaoPayService;
import com.kh.final3.service.PaymentService;
import com.kh.final3.vo.TokenVO;
import com.kh.final3.vo.kakaopay.KakaoPayApproveRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayApproveResponseVO;
import com.kh.final3.vo.kakaopay.KakaoPayFlashVO;
import com.kh.final3.vo.kakaopay.KakaoPayReadyRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayReadyResponseVO;
import com.kh.final3.vo.kakaopay.KakaoPayChargeRequestVO;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/kakaopay")
public class KakaoPayRestController {

    @Autowired
    private KakaoPayService kakaoPayService;

    @Autowired
    private PaymentService paymentService;


    private Map<String, KakaoPayFlashVO> flashMap =
            Collections.synchronizedMap(new HashMap<>());


    @PostMapping("/buy")
    public KakaoPayReadyResponseVO buy(
            @RequestBody KakaoPayChargeRequestVO chargeRequestVO,
            @RequestHeader("Frontend-Url") String frontendUrl,
            @RequestAttribute(value = "tokenVO", required = false) TokenVO tokenVO
    ) {
       
        if (tokenVO == null) {
           
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "LOGIN_REQUIRED");
        }

      
        if (chargeRequestVO == null ||
            chargeRequestVO.getAmount() == null ||
            chargeRequestVO.getAmount() <= 0) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "INVALID_AMOUNT"
            );
        }

        int amount = chargeRequestVO.getAmount();

    
        String partnerOrderId = UUID.randomUUID().toString();

       
        KakaoPayReadyRequestVO requestVO = KakaoPayReadyRequestVO.builder()
                .partnerOrderId(partnerOrderId)
                .partnerUserId(tokenVO.getLoginId()) // 로그인 사용자 ID
                .itemName("포인트 충전")
                .totalAmount(amount)
                .build();


        KakaoPayReadyResponseVO responseVO = kakaoPayService.ready(requestVO);

       
        KakaoPayFlashVO flashVO = KakaoPayFlashVO.builder()
                .partnerOrderId(partnerOrderId)
                .partnerUserId(tokenVO.getLoginId())
                .tid(responseVO.getTid())
                .returnUrl(frontendUrl)
                .build();

        flashMap.put(partnerOrderId, flashVO);

        return responseVO;
    }


    @GetMapping("/buy/success/{partnerOrderId}")
    public void success(
            @PathVariable String partnerOrderId,
            @RequestParam("pg_token") String pgToken,
            HttpServletResponse response
    ) throws IOException {
        KakaoPayFlashVO flashVO = flashMap.remove(partnerOrderId);
        if (flashVO == null) {
            // 잘못된 접근 또는 이미 처리된 주문
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_ORDER_ID");
        }

        KakaoPayApproveRequestVO requestVO = KakaoPayApproveRequestVO.builder()
                .partnerOrderId(flashVO.getPartnerOrderId())
                .partnerUserId(flashVO.getPartnerUserId())
                .tid(flashVO.getTid())
                .pgToken(pgToken)
                .build();

        KakaoPayApproveResponseVO approveResponse = kakaoPayService.approve(requestVO);

        
        paymentService.insert(approveResponse, flashVO);

        response.sendRedirect(flashVO.getReturnUrl() + "/success");
    }

    @GetMapping("/buy/cancel/{partnerOrderId}")
    public void cancel(
            @PathVariable String partnerOrderId,
            HttpServletResponse response
    ) throws IOException {
        KakaoPayFlashVO flashVO = flashMap.remove(partnerOrderId);
        if (flashVO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_ORDER_ID");
        }
        response.sendRedirect(flashVO.getReturnUrl() + "/cancel");
    }

    @GetMapping("/buy/fail/{partnerOrderId}")
    public void fail(
            @PathVariable String partnerOrderId,
            HttpServletResponse response
    ) throws IOException {
        KakaoPayFlashVO flashVO = flashMap.remove(partnerOrderId);
        if (flashVO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_ORDER_ID");
        }
        response.sendRedirect(flashVO.getReturnUrl() + "/fail");
    }
}
