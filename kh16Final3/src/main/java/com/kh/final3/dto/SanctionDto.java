package com.kh.final3.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SanctionDto {
    
    private Long sanctionNo;
    private Long memberNo;
    private String type;
    private Integer durationDay;
    private Timestamp startTime;
    private Timestamp endTime;
    private String reason;
    private String status;
}