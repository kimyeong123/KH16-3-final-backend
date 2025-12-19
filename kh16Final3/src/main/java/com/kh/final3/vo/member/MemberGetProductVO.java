package com.kh.final3.vo.member;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberGetProductVO {

    private int productNo;        // product_no
    private String name;          // name
    private int finalPrice;       // final_price
    private LocalDateTime endTime; // end_time
    private String status;        // status
}
