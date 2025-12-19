package com.kh.final3.event;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import com.kh.final3.dto.BidDto;
import com.kh.final3.vo.BidUpdateMessageVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor // 필수 필드만 받은 생성자 생성
@Component
public class BidEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    // 매개변수 타입으로 이벤트 실행을 구분
    // 트랜잭션 커밋이 완전히 일어났을 경우에 대한 콜백 메소드 실행 어노테이션
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBidPlaced(BidPlacedEvent event) {

        BidDto bidDto = event.getBidDto();

        BidUpdateMessageVO message =
            BidUpdateMessageVO.builder()
                .productNo(bidDto.getProductNo())
                .currentPrice(bidDto.getAmount())
                .bidderNo(bidDto.getBidderNo())
                .build();

        messagingTemplate.convertAndSend(
            "/topic/products/" + bidDto.getProductNo() + "/bid",
            message
        );
        
        log.warn("STOMP SEND productNo={}, price={}",
                bidDto.getProductNo(),
                bidDto.getAmount());
    }
}
