package com.kh.final3.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EscrowLedgerDto {
    private Long escrowLedgerNo;
    private Long productNo;
    private Long bidderNo;    
    private Long amount; // 에스크로에 묶인 금액 (들어가는 화폐는 포인트)
    private String status;  
    private Timestamp createdTime;         
    private Timestamp updatedTime;                        
}