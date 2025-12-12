package com.kh.final3.service;

import java.time.LocalDate;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.MemberDao;
import com.kh.final3.dao.TokenDao;
import com.kh.final3.dto.MemberDto;
import com.kh.final3.vo.member.MemberRequestVO;
import com.kh.final3.vo.member.MemberUpdateVO;


@Service
public class MemberService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenDao tokenDao;

    @Transactional
    public void add(MemberDto memberDto) {
        // 1. 중복 체크
        if (memberDao.selectOneByMemberId(memberDto.getId()) != null) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        if (memberDao.selectOneByNickname(memberDto.getNickname()) != null) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        // 2.이름 + 생일 + 연락처 중복 체크
       if (memberDao.selectOneByNameBirthContact(
                memberDto.getName(), memberDto.getBirth(), memberDto.getContact()) != null) {
            throw new IllegalArgumentException("이미 사용된 인증 휴대폰 번호입니다. 로그인 또는 비밀번호 찾기를 이용해 주세요.");
        }
        // 3. 비밀번호 암호화
        String encoded = passwordEncoder.encode(memberDto.getPw());
        memberDto.setPw(encoded);
        memberDao.insert(memberDto);
    }
    // 아이디 사용 가능 여부
    public boolean checkId(String memberId) {
        return memberDao.selectOneByMemberId(memberId) == null;
    }
    // 닉네임 사용 가능 여부
    public boolean checkNickname(String memberNickname) {
        return memberDao.selectOneByNickname(memberNickname) == null;
    }
    // 이름 + 생일 + 연락처 중복 확인
    public MemberDto selectOneByNameBirthContact(String name, LocalDate birth, String contact) {
        return memberDao.selectOneByNameBirthContact(name, birth, contact);
    }
    //비밀번호 확인
    public boolean checkPassword(MemberRequestVO requestVO) {
        if (requestVO.getMemberNo() == null || requestVO.getPw() == null) {
            return false;
        }
        String originPw = memberDao.findPasswordByMemberNo(requestVO.getMemberNo());
        if (originPw == null) return false;
        // BCrypt 매칭
        return passwordEncoder.matches(requestVO.getPw(), originPw);
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
            member.setEmail(vo.getEmail());
        }
        if (vo.getPost() != null && !vo.getPost().isEmpty()) {
            member.setPost(vo.getPost());
        }
        if (vo.getAddress1() != null && !vo.getAddress1().isEmpty()) {
            member.setAddress1(vo.getAddress1());
        }
        if (vo.getAddress2() != null && !vo.getAddress2().isEmpty()) {
            member.setAddress2(vo.getAddress2());
        }
        if (vo.getContact() != null && !vo.getContact().isEmpty()) {
            member.setContact(vo.getContact());
        }
        return memberDao.updateMember(member) > 0;
    }
    @Transactional
    public boolean changePassword(Long memberNo, String currentPassword, String newPassword) {
        // 1) 현재 비밀번호 확인
        boolean isCurrentPasswordValid = checkPassword(new MemberRequestVO(memberNo, currentPassword));
        if (!isCurrentPasswordValid) {
            return false;
        }
        // 2) 새 비밀번호가 현재 비밀번호와 같은지
        String originPw = memberDao.findPasswordByMemberNo(memberNo);
        if (originPw != null && passwordEncoder.matches(newPassword, originPw)) {
            return false; 
        }
        // 3) 새 비밀번호 암호화, 저장
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        boolean updated = memberDao.updatePassword(memberNo, encodedNewPassword) > 0;
        // 성공 시: refresh token(또는 토큰 테이블) 전부 삭제
        if (updated) {
            MemberDto member = memberDao.selectOneByMemberNo(memberNo);
            if (member != null) {
                tokenDao.deleteByTarget(member.getId()); // 네 프로젝트에서 쓰던 방식
            }
        }

        return updated;
    }
    @Transactional
    public boolean updatePassword(Long memberNo, String newPassword) {
        // 비밀번호 업데이트
        return memberDao.updatePassword(memberNo, newPassword) > 0;
    }
    // 이메일로 아이디 찾기
    public void sendMemberIdByEmail(String email) {
        MemberDto member = memberDao.selectOneByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("해당 이메일로 가입된 계정을 찾을 수 없습니다.");
        }
        emailService.sendFindIdMail(email, member.getId());
    }
    //비밀번호 임시발급
    private String generateTempPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String number = "0123456789";
        String special = "!@#$";
        String all = upper + lower + number + special;

        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(number.charAt(random.nextInt(number.length())));
        sb.append(special.charAt(random.nextInt(special.length())));

        // 나머지 랜덤 (총 10자리 정도 추천)
        for (int i = 0; i < 6; i++) {
            sb.append(all.charAt(random.nextInt(all.length())));
        }
        // 섞기
        char[] chars = sb.toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int j = random.nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    public void resetPasswordByIdAndEmail(String memberId, String email) {
        MemberDto member = memberDao.selectOneByIdAndEmail(memberId, email);
        if (member == null) {
            throw new IllegalArgumentException("입력하신 아이디와 이메일이 일치하는 계정을 찾을 수 없습니다.");
        }
        String tempPassword = generateTempPassword(); // 임시로 발급한 비밀번호
        //임시로 발급한 비밀번호를 디비에서 갈아끼움
        String encoded = passwordEncoder.encode(tempPassword); 
        memberDao.updatePassword(member.getMemberNo(), encoded);
        emailService.sendTempPasswordMail(email, tempPassword);
    }

}
