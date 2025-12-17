package com.kh.final3.domain.enums;

public enum PointHistoryReason {
    
    // 포인트 충전 (결제를 통한 포인트 구매)
	CHARGED("CHARGED", "ADD"),
    
    // 포인트 출금/환급 (포인트를 현금으로 인출)
	WITHDRAWN("WITHDRAWN", "DEDUCT"),

    // 입찰 보증금 잠금 (입찰 시점에 포인트가 묶이는 행위)
	BID_LOCKED("BID_LOCKED", "DEDUCT"),
    
    // 입찰 보증금 환불 (유찰, 취소 등으로 묶였던 포인트 반환) 
	BID_REFUNDED("BID_REFUNDED", "ADD"),

	SETTLEMENT("SETTLEMENT", "ADD"); // 판매자 정산
    
    private final String reason;
    private final String type;

    PointHistoryReason(String reason, String type) {
        this.reason = reason;
        this.type = type;
    }

    public String getReason() {
        return reason;
    }
    
    public String getType() {
        return type;
    }
    
}