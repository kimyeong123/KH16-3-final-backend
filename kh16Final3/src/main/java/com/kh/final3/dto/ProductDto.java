package com.kh.final3.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductDto {
private int productNo;
private int productSeller;
private String productName;
private String productDescription; //상세설명
private int productStartPrice;//시작가
private int productCurrentPrice;//현재가(초기값=시작가)
private Integer productInstantPrice;   //즉시 구매가(Null허용함)
private Integer productFinalPrice;//Null허용해야함
private LocalDateTime productStartDate; //경매시작시간
private LocalDateTime productEndDate; //경매 마감시간
private String productStatus; //BIDDING(진행중), ENDED(낙찰), DELIVERING(배송중), COMPLETED(완료)
private Integer productBuyerNo; //낙찰자 Fk (낙찰전엔 Null)
private LocalDateTime productRegistrationAt;//상품등록한시간?



}
