package com.kh.final3.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PointChargeHistoryVO {
  private Long pointHistoryNo;
  private String type;
  private Long amount;
  private String reason;
  private Timestamp createdTime;
}