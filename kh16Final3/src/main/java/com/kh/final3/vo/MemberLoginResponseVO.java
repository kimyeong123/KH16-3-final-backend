package com.kh.final3.vo;



import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class MemberLoginResponseVO {
	private Long loginNo;
	private String loginId;//로그인한 사용자의 ID
	private String loginLevel;//로그인한 사용자의 등급
	private String nickname;//로그인한 사용자의 닉네임
	private String email; // 이메일
    private String address1;//기본주소 
    private String address2;//상세주소 
	private String accessToken;//나중에 사용자가 들고올 토큰
	private String refreshToken;//accessToken에 문제가 있을 때 갱신할 토큰
	private LocalDateTime createdTime;
	private Integer point;//보유 머니
	private String contact;
}
