package com.kh.final3.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kh.final3.dto.MemberDto;
import com.kh.final3.service.MemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/member")
@CrossOrigin
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    // 회원가입
    @PostMapping("/register")
    public String register(@Valid @RequestBody MemberDto memberDto) {
        memberService.add(memberDto);
        return "회원가입 성공";
    }
    // 아이디 중복 체크
    @GetMapping("/check/id/{memberId}")
    public boolean checkId(@PathVariable String memberId) {
        return memberService.checkId(memberId);
    }
    // 닉네임 중복 체크
    @GetMapping("/check/nickname/{memberNickname}")
    public boolean checkNickname(@PathVariable String memberNickname) {
        return memberService.checkNickname(memberNickname);
    }
}
