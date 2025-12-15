package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.PaymentDao;
import com.kh.final3.dao.PointHistoryDao;
import com.kh.final3.dto.PaymentDto;
import com.kh.final3.dto.PointHistoryDto;
import com.kh.final3.vo.kakaopay.KakaoPayApproveResponseVO;
import com.kh.final3.vo.kakaopay.KakaoPayFlashVO;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private PointHistoryDao pointHistoryDao;

    // ✅ 카카오페이 충전 성공 시 저장 (ADD)
    @Transactional
    public void insert(KakaoPayApproveResponseVO responseVO, KakaoPayFlashVO flashVO) {

        // partnerUserId 에 memberNo(숫자 문자열)가 들어있다고 가정 (A안)
        Long memberNo = Long.valueOf(flashVO.getPartnerUserId());

        long paymentNo = paymentDao.sequence();
        long amount    = responseVO.getAmount().getTotal();

        // payment 저장
        PaymentDto paymentDto = PaymentDto.builder()
                .paymentNo(paymentNo)
                .memberNo(memberNo)
                .type("CHARGE")
                .pg("KAKAOPAY")
                .pgTid(responseVO.getTid())
                .amount(amount)
                .point(amount)
                .status("PAID")
                .reqTime(responseVO.getCreatedAt())
                .resTime(responseVO.getApprovedAt())
                .build();
        paymentDao.insert(paymentDto);

        // point_history 저장 (충전 = ADD)
        long pointHistoryNo = pointHistoryDao.sequence();

        PointHistoryDto pointHistoryDto = PointHistoryDto.builder()
                .pointHistoryNo(pointHistoryNo)
                .memberNo(memberNo)
                .type("ADD")          // ✅ 잔액 계산에 반영
                .amount(amount)
                .reason("CHARGE")
                .productNo(null)
                .build();

        pointHistoryDao.insert(pointHistoryDto);
    }

    // ✅ “가짜 환전”: 입력 금액만큼 포인트 차감 (DEDUCT)
    @Transactional
    public void exchange(Long memberNo, long amount) {
        if (memberNo == null) {
            throw new IllegalArgumentException("INVALID_MEMBER");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("INVALID_AMOUNT");
        }

        long balance = pointHistoryDao.calculateMemberBalance(memberNo.intValue());
        if (balance < amount) {
            throw new IllegalStateException("INSUFFICIENT_POINT");
        }

        long pointHistoryNo = pointHistoryDao.sequence();

        PointHistoryDto dto = PointHistoryDto.builder()
                .pointHistoryNo(pointHistoryNo)
                .memberNo(memberNo)
                .type("DEDUCT")       // ✅ 차감
                .amount(amount)
                .reason("EXCHANGE")   // 환전(가짜 출금)
                .productNo(null)
                .build();

        pointHistoryDao.insert(dto);

        // (원하면) payment 테이블에도 환전 기록 남길 수 있는데, 지금 요구는 “차감만”이라 생략
    }
}
