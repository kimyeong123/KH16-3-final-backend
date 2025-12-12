package com.kh.final3.domain.enums;

public enum EscrowStatus {
	
    HELD("HELD", "입찰금 잠금 (최고가)"),
    
    RELEASED("RELEASED", "입찰금 해제 및 환불 완료 (최고가 박탈)"),
    
    PENDING_SETTLEMENT("PENDING_SETTLEMENT", "정산 대기 (구매자 수령확인 전)"),
    
    SETTLED("SETTLED", "정산 완료 (판매자 지급)"),
    
    CANCELLED("CANCELLED", "거래 취소 및 환불 완료");

    private final String status;
    private final String description;

    EscrowStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

}