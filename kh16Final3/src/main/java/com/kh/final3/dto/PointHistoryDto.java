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
public class PointHistoryDto {

    private Integer pointHistoryNo;  // point_history_no
    private Integer memberNo;        // member_no
    private Integer amount;          // amount
    private String reason;           // reason
    private Integer relatedNo;       // related_no
    private LocalDateTime createdTime; // created_time
}
