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
public class PaymentDto {

    private Long paymentNo;

    private Long memberNo;

    private String type;

    private String pg;

    private String pgTid;

    private Long amount;

    private Long point;

    private String status;

    private LocalDateTime reqTime;

    private LocalDateTime resTime;
}
