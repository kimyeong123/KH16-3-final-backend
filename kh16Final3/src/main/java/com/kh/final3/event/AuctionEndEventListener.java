package com.kh.final3.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.kh.final3.dao.ProductDao;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionEndEventListener {

    private final MessageService messageService;
    private final ProductDao productDao;

    @EventListener
    public void handleAuctionEnded(AuctionEndedEvent event) {
        try {
            ProductDto product = productDao.selectOne(event.getProductNo());
            if (product != null && event.getBuyerNo() > 0) {
                messageService.sendNotification(
                    event.getBuyerNo(), 
                    "[" + product.getName() + "] 낙찰 알림", 
                    "/product/mylist",
                    event.getProductNo()
                );
            }
        } catch (Exception e) {
            // 로그를 없앴더라도 에러만큼은 콘솔에 찍히게 해야 합니다.
            e.printStackTrace(); 
        }
    }
}