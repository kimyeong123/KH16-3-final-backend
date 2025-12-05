package com.kh.final3.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BidDto {

    private Integer bidNo;       // PK
    private Integer bidProduct;  
    private Integer bidMember;  
    private Integer bidAmount;  
    private LocalDateTime bidTime; 
}