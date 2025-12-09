package com.kh.final3.vo.kakaopay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoPayAmountVO {
		
		private Integer total;
		//이건 혹시나모르니까 놔둠. 경매사이트는 필요없을거같긴한데
		private Integer taxFree;
		private Integer vat;
		private Integer point;
		private Integer discount;
		private Integer greenDeposit;
}
