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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.kh.final3.service.KakaoPayService;
import com.kh.final3.service.PaymentService;
import com.kh.final3.service.TokenService;
import com.kh.final3.vo.TokenVO;
import com.kh.final3.vo.PointExchangeRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayApproveRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayApproveResponseVO;
import com.kh.final3.vo.kakaopay.KakaoPayFlashVO;
import com.kh.final3.vo.kakaopay.KakaoPayReadyRequestVO;
import com.kh.final3.vo.kakaopay.KakaoPayReadyResponseVO;
import com.kh.final3.vo.kakaopay.KakaoPayChargeRequestVO;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/kakaopay")
public class KakaoPayRestController {

    @Autowired
    private KakaoPayService kakaoPayService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TokenService tokenService;

    private Map<String, KakaoPayFlashVO> flashMap =
            Collections.synchronizedMap(new HashMap<>());

    // ✅ 포인트 충전 (카카오페이 ready)
    @PostMapping("/buy")
    public KakaoPayReadyResponseVO buy(
            @RequestBody KakaoPayChargeRequestVO chargeRequestVO,
            @RequestHeader("Frontend-Url") String frontendUrl,
            @RequestHeader(name = "Authorization", required = false) String bearerToken
    ) {

        TokenVO tokenVO = parseTokenOrThrow(bearerToken);

        if (chargeRequestVO == null ||
            chargeRequestVO.getAmount() == null ||
            chargeRequestVO.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_AMOUNT");
        }

        int amount = chargeRequestVO.getAmount();
        String partnerOrderId = UUID.randomUUID().toString();

        // ✅ A안: partnerUserId에 memberNo(숫자 문자열) 저장
        String memberNoStr = String.valueOf(tokenVO.getMemberNo());

        KakaoPayReadyRequestVO requestVO = KakaoPayReadyRequestVO.builder()
                .partnerOrderId(partnerOrderId)
                .partnerUserId(memberNoStr)
                .itemName("포인트 충전")
                .totalAmount(amount)
                .build();

        KakaoPayReadyResponseVO responseVO = kakaoPayService.ready(requestVO);

        KakaoPayFlashVO flashVO = KakaoPayFlashVO.builder()
                .partnerOrderId(partnerOrderId)
                .partnerUserId(memberNoStr)
                .tid(responseVO.getTid())
                .returnUrl(frontendUrl)
                .build();

        flashMap.put(partnerOrderId, flashVO);
        return responseVO;
    }

    // ✅ 포인트 충전 성공 콜백 (approve + DB 저장)
    @GetMapping("/buy/success/{partnerOrderId}")
    public void success(
            @PathVariable String partnerOrderId,
            @RequestParam("pg_token") String pgToken,
            HttpServletResponse response
    ) throws IOException {

        KakaoPayFlashVO flashVO = flashMap.remove(partnerOrderId);
        if (flashVO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_ORDER_ID");
        }

        KakaoPayApproveRequestVO requestVO = KakaoPayApproveRequestVO.builder()
                .partnerOrderId(flashVO.getPartnerOrderId())
                .partnerUserId(flashVO.getPartnerUserId()) // memberNo 문자열
                .tid(flashVO.getTid())
                .pgToken(pgToken)
                .build();

        KakaoPayApproveResponseVO approveResponse = kakaoPayService.approve(requestVO);

        // ✅ 충전 내역 + 포인트 ADD 적재
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

    // ✅ “가짜 환전” 엔드포인트: 포인트 차감만 수행
    // 요청: { "amount": 5000 }
    @PostMapping("/exchange")
    public Map<String, Object> exchange(
            @RequestBody PointExchangeRequestVO exchangeRequestVO,
            @RequestHeader(name = "Authorization", required = false) String bearerToken
    ) {
        TokenVO tokenVO = parseTokenOrThrow(bearerToken);

        if (exchangeRequestVO == null ||
            exchangeRequestVO.getAmount() == null ||
            exchangeRequestVO.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_AMOUNT");
        }

        Long memberNo = tokenVO.getMemberNo();
        long amount = exchangeRequestVO.getAmount().longValue();

        try {
            paymentService.exchange(memberNo, amount);
        }
        catch (IllegalStateException e) {
            // 잔액 부족
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return Map.of("result", "OK");
    }

    // ✅ 토큰 파싱 공통 처리
    private TokenVO parseTokenOrThrow(String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "LOGIN_REQUIRED");
        }

        try {
            return tokenService.parse(bearerToken);
        }
        catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
        }
    }
}
