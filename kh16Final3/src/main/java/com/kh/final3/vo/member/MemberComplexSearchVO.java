package com.kh.final3.vo.member;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) 언더바를 카멜케이스로 자동변환
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberComplexSearchVO {

	// 주소를 분할하여 반환하는 추가 Getter 메소드
	private String iId;
	private String nickname;
	private String email;
	private String contact;
	private String birth;
	private Integer minPoint, maxPoint;
	private String beginJoin, endJoin;
	private List<String> roleList;
	private String address;

	public Set<String> getAddressTokenList() {
		if (address == null)
			return null;
		String stripResult = address.strip();
		if (stripResult.isEmpty())
			return null;

		String[] tokens = stripResult.split("\\s+");// 분할
		// String[] -> Set<String>
		Set<String> set = Arrays.stream(tokens).collect(Collectors.toSet());

		return set;

	}
}
