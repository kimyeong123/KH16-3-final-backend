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
public class PointWithdrawDto {
    private long withdrawNo;
    private long memberNo;
    private long amount;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String status;          // REQUEST / DONE / REJECT
    private LocalDateTime createdTime;
    private LocalDateTime processedTime;
    private Long processedBy;
    private String rejectReason;
}
