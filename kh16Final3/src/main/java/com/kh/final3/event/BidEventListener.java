package com.kh.final3.event;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.kh.final3.dto.BidDto;
import com.kh.final3.service.MessageService; // MessageService 추가
import com.kh.final3.vo.BidUpdateMessageVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class BidEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService; // Service로 변경

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBidPlaced(BidPlacedEvent event) {

        BidDto bidDto = event.getBidDto();

        // 1. 실시간 가격 갱신 (STOMP 전송)
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
        
        // 2. 상위 입찰 발생 시 이전 입찰자에게 알림 저장
        if (event.getPreviousBidderNo() != null) {
            String content = "회원님이 입찰하신 상품에 상위 입찰이 발생했습니다.";
            // 알림 클릭 시 이동할 상세 페이지 경로
            String url = "/product/auction/detail/" + bidDto.getProductNo(); 
            
            // 서비스의 sendNotification 호출 (내부에서 senderNo=0, sequence 처리 수행)
            messageService.sendNotification(
                event.getPreviousBidderNo(), 
                content, 
                url,
                bidDto.getProductNo()
            );
        }

        // 3. 로그 출력
        log.warn("STOMP SEND productNo={}, price={}, targetUser={}",
                bidDto.getProductNo(),
                bidDto.getAmount(),
                event.getPreviousBidderNo());
    }
}