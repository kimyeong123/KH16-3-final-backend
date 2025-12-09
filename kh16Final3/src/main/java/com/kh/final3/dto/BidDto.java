package com.kh.final3.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidDto {

    private Integer bidNo;
    private Integer productNo;
    private Integer bidderNo;
    private Integer amount;
    private LocalDateTime bidTime;
}
