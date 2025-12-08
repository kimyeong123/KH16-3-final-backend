package com.kh.final3.restcontroller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("/memberId/{memberId}")
    public boolean checkId(@PathVariable String memberId) {
        return memberService.checkId(memberId);
    }

    @GetMapping("/memberNickname/{memberNickname}")
    public boolean checkNickname(@PathVariable String memberNickname) {
        return memberService.checkNickname(memberNickname);
    }
    @GetMapping("/checkDuplicate")
    public boolean checkDuplicate(
            @RequestParam String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birth,
            @RequestParam String contact) {
        return memberService.selectOneByNameBirthContact(name, birth, contact) == null;
    }

    // 회원 로그인 (추후 필요 시)
    // @PostMapping("/login")
}
