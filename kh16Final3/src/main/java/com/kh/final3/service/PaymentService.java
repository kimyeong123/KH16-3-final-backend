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

    @Transactional
    public void insert(KakaoPayApproveResponseVO responseVO, KakaoPayFlashVO flashVO) {

        // partnerUserId 에 memberNo 를 문자열로 넣어두었다고 가정
        Integer memberNo = Integer.valueOf(flashVO.getPartnerUserId());

        int paymentNo = paymentDao.sequence();
        int amount    = responseVO.getAmount().getTotal();

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

        // point_history 저장
        int pointHistoryNo = pointHistoryDao.sequence();

        PointHistoryDto pointHistoryDto = PointHistoryDto.builder()
                .pointHistoryNo((long) pointHistoryNo)
                .memberNo(memberNo.longValue())
                .amount((long) amount)
                .reason("CHARGE")
                .relatedNo((long) paymentNo)
                .build();

        pointHistoryDao.insert(pointHistoryDto);
    }
}
