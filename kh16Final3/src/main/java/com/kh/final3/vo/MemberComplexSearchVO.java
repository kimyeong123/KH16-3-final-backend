package com.kh.final3.vo;

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
	private String memberId;
	private String memberNickname;
	private String memberEmail;
	private String memberContact;
	private String memberBirth;
	private Integer minMemberPoint, maxMemberPoint;
	private String beginMemberJoin, endMemberJoin;
	private List<String> memberRoleList;
	private String memberAddress;

	public Set<String> getAddressTokenList() {
		if (memberAddress == null)
			return null;
		String stripResult = memberAddress.strip();
		if (stripResult.isEmpty())
			return null;

		String[] tokens = stripResult.split("\\s+");// 분할
		// String[] -> Set<String>
		Set<String> set = Arrays.stream(tokens).collect(Collectors.toSet());

		return set;

	}
}
