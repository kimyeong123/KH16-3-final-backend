package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.MemberDao;
import com.kh.final3.dto.MemberDto;

@Service
public class MemberService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void add(MemberDto memberDto) {
        // 1. 중복 체크
        if (memberDao.selectOneByMemberId(memberDto.getMemberId()) != null) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        if (memberDao.selectOneByMemberNickname(memberDto.getMemberNickname()) != null) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        // 2. 비밀번호 암호화
        String encoded = passwordEncoder.encode(memberDto.getMemberPw());
        memberDto.setMemberPw(encoded);
        memberDao.insert(memberDto);
    }
    // 아이디 사용 가능 여부
    public boolean checkId(String memberId) {
        return memberDao.selectOneByMemberId(memberId) == null;
    }
    // 닉네임 사용 가능 여부
    public boolean checkNickname(String memberNickname) {
        return memberDao.selectOneByMemberNickname(memberNickname) == null;
    }
}
