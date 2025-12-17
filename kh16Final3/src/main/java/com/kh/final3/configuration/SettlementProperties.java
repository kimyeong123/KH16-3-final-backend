package com.kh.final3.configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.kh.final3.domain.records.SettlementResult;

import lombok.Data;

// 지금은 정산이 없어서 feeRate는 현재 사용하지 않음
@Data
@Component
@ConfigurationProperties(prefix = "custom.settlement")
public class SettlementProperties {
	// 돈 계산은 정확해야하므로 2진수 기반의 double이나 float가 아닌 digDecimal로 지정 
    private BigDecimal feeRate;   // 0.05
    private int delayDays;
    
    // 수수료 계산(레코드 사용으로 실 정산액과 수수료 둘다 반환)
    public SettlementResult calculateSettlement(long grossAmount) {
        long feeAmount = BigDecimal.valueOf(grossAmount)
                .multiply(feeRate)
                .setScale(0, RoundingMode.DOWN)
                .longValue();

        long amount = grossAmount - feeAmount;

        return new SettlementResult(amount, feeAmount);
    }
}
