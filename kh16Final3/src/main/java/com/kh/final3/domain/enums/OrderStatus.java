package com.kh.final3.domain.enums;

public enum OrderStatus {

    CREATED("CREATED"),          // 주문 생성됨
    
    SHIPPING("SHIPPING"),        // 판매자가 송장 입력함
    
    DELIVERED("DELIVERED"),      // 구매자가 수령확인함
    
    COMPLETED("COMPLETED"),      // 정산 완료됨
    
    CANCELLED("CANCELLED");      // 취소됨 (선택적)

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    
}
