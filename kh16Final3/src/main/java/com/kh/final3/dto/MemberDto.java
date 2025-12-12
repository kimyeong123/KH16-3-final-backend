package com.kh.final3.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDto {
    private Long memberNo;
    @NotBlank(message = "아이디는 필수 항목입니다.")
	@Pattern(regexp = "^[a-z][a-z0-9]{4,19}$")
    private String memberId;
    @NotBlank
	@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[\\!\\@\\#\\$])[A-Za-z0-9\\!\\@\\#\\$]{8,16}$")
    private String memberPw;
    @Pattern(regexp = "^[가-힣0-9]{2,10}$")
    private String memberNickname; 
    @Pattern(regexp = "^[가-힣]{2,6}$")
    private String memberName;
	@NotBlank
	@Email
	@Pattern(regexp="^[A-Za-z0-9.%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$")
    private String memberEmail;
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate memberBirth;
	@Pattern(regexp = "^010-[1-9][0-9]{3}-[0-9]{4}$")
    private String memberContact;
    @Pattern(regexp="^[0-9]{5,6}$")
    private String memberPost;
    @Size(max = 100)	
    private String memberAddress1;
    @Size(max = 100)
    private String memberAddress2; 	
    private String memberRole; 
    private Long memberPoint;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
//    private LocalDateTime memberCreatedTime;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
//    private LocalDateTime memberEditTime;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
//    private LocalDateTime memberRecentTime; 
}

