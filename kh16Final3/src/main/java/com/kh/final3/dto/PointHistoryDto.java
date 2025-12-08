package com.kh.final3.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder 
public class PointHistoryDto {
    private Long pointHistoryNo;
    private Long memberNo; 
    private Long amount; 
    private String reason;
    private Long relatedNo;        
    private Timestamp createdTime;
}