package com.kh.final3.event;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.kh.final3.vo.AuctionEndedMessageVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionEndEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionEnded(AuctionEndedEvent event) {

        messagingTemplate.convertAndSend(
            "/topic/products/" + event.getProductNo() + "/end",
            AuctionEndedMessageVO.builder()
                .productNo(event.getProductNo())
                .finalPrice(event.getFinalPrice())
                .buyerNo(event.getBuyerNo())
                .build()
        );

        log.warn("[AUCTION-END-STOMP] productNo={}", event.getProductNo());
    }
}

