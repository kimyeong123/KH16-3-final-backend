package com.kh.final3.dto;

import java.time.LocalDateTime; // 변경
import com.fasterxml.jackson.annotation.JsonFormat; // 추가
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDto {
    private Long productNo;
    private Long sellerNo;
    private String name;
    private Long categoryCode;
    private String description;
    private Long startPrice; 
    private Long currentPrice;
    private Long finalPrice;
    private Long instantPrice; 

    // [핵심 수정] pattern에 'T'가 포함되어야 프론트엔드의 datetime-local 값을 인식합니다.
    // timezone을 Asia/Seoul로 고정하여 9시간 오차를 원천 봉쇄합니다.
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startTime; 

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endTime;

    private String status;
    private Long buyerNo;        

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime registrationTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endedTime;
}