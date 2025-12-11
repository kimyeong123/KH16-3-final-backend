package com.kh.final3.vo.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateVO {
    private String email;      //이메일
    private String post;       //우편번호
    private String address1;   //기본 주소
    private String address2;   //상세 주소
    private String contact;    // 연락처
}
