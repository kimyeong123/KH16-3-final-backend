package com.kh.final3.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.MemberDao;
import com.kh.final3.dto.MemberDto;
import com.kh.final3.vo.member.MemberRequestVO;
import com.kh.final3.vo.member.MemberUpdateVO;


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
        // 2.이름 + 생일 + 연락처 중복 체크
       if (memberDao.selectOneByNameBirthContact(
                memberDto.getMemberName(), memberDto.getMemberBirth(), memberDto.getMemberContact()) != null) {
            throw new IllegalArgumentException("이미 사용된 인증 휴대폰 번호입니다. 로그인 또는 비밀번호 찾기를 이용해 주세요.");
        }

        // 3. 비밀번호 암호화
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
    // 이름 + 생일 + 연락처 중복 확인
    public MemberDto selectOneByNameBirthContact(String name, LocalDate birth, String contact) {
        return memberDao.selectOneByNameBirthContact(name, birth, contact);
    }
    //비밀번호 확인
    public boolean checkPassword(MemberRequestVO requestVO) {
        if (requestVO.getMemberNo() == null || requestVO.getMemberPw() == null) {
            return false;
        }
        String originPw = memberDao.findPasswordByMemberNo(requestVO.getMemberNo());
        if (originPw == null) return false;
        // BCrypt 매칭
        return passwordEncoder.matches(requestVO.getMemberPw(), originPw);
    }
    // 회원 삭제
    public boolean deleteMember(Long memberNo) {
        return memberDao.deleteMember(memberNo) > 0;
    }
    @Transactional
    public boolean updateMember(Long memberNo, MemberUpdateVO vo) {
        // 1. 회원 조회
        MemberDto member = memberDao.selectOneByMemberNo(memberNo);
        if (member == null) return false;
        
        // 2. 수정할 필드 세팅
        if (vo.getEmail() != null && !vo.getEmail().isEmpty()) {
            member.setMemberEmail(vo.getEmail());
        }
        if (vo.getPost() != null && !vo.getPost().isEmpty()) {
            member.setMemberPost(vo.getPost());
        }
        if (vo.getAddress1() != null && !vo.getAddress1().isEmpty()) {
            member.setMemberAddress1(vo.getAddress1());
        }
        if (vo.getAddress2() != null && !vo.getAddress2().isEmpty()) {
            member.setMemberAddress2(vo.getAddress2());
        }
        if (vo.getContact() != null && !vo.getContact().isEmpty()) {
            member.setMemberContact(vo.getContact());
        }

        // 3. DB 업데이트
        return memberDao.updateMember(member) > 0;
    }


    
}
