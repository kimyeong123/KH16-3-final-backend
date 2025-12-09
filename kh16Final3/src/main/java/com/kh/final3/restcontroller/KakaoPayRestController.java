package com.kh.final3.restcontroller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.service.KakaoPayService;
import com.kh.final3.service.PaymentService;
import com.kh.final3.vo.kakaopay.KakaoPayApproveRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayApproveResponseVO;
import com.kh.final3.vo.kakaopay.KakaoPayFlashVO;
import com.kh.final3.vo.kakaopay.KakaoPayReadyRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayReadyResponseVO;

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
            @RequestBody KakaoPayReadyRequestVO requestVO,
            @RequestHeader("Frontend-Url") String frontendUrl
    ) {
        if (requestVO.getPartnerOrderId() == null || requestVO.getPartnerOrderId().isEmpty()) {
            requestVO.setPartnerOrderId(UUID.randomUUID().toString());
        }

        KakaoPayReadyResponseVO responseVO = kakaoPayService.ready(requestVO);

        KakaoPayFlashVO flashVO = KakaoPayFlashVO.builder()
                .partnerOrderId(requestVO.getPartnerOrderId())
                .partnerUserId(requestVO.getPartnerUserId())
                .tid(responseVO.getTid())
                .returnUrl(frontendUrl)
                .build();

        flashMap.put(requestVO.getPartnerOrderId(), flashVO);

        return responseVO;
    }

    @GetMapping("/buy/success/{partnerOrderId}")
    public void success(
            @PathVariable String partnerOrderId,
            @RequestParam("pg_token") String pgToken,
            HttpServletResponse response
    ) throws IOException {
        KakaoPayFlashVO flashVO = flashMap.remove(partnerOrderId);

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
        response.sendRedirect(flashVO.getReturnUrl() + "/cancel");
    }

    @GetMapping("/buy/fail/{partnerOrderId}")
    public void fail(
            @PathVariable String partnerOrderId,
            HttpServletResponse response
    ) throws IOException {
        KakaoPayFlashVO flashVO = flashMap.remove(partnerOrderId);
        response.sendRedirect(flashVO.getReturnUrl() + "/fail");
    }
}
