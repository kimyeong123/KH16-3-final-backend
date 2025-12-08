package com.kh.final3.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;

@Data @NoArgsConstructor @AllArgsConstructor @Builder 
public class BidDto {
    private Long bidNo;
    private Long productNo;
    private Long bidderNo; 
    private Long amount; 
    private Timestamp bidTime;
}