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

    private Integer paymentNo;

    private Integer memberNo;

    private String type;

    private String pg;

    private String pgTid;

    private Integer amount;

    private Integer point;

    private String status;

    private LocalDateTime reqTime;

    private LocalDateTime resTime;
}
