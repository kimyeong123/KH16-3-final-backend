package com.kh.final3.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private String id;

    @NotBlank
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[\\!\\@\\#\\$])[A-Za-z0-9\\!\\@\\#\\$]{8,16}$")
    private String pw;

    @Pattern(regexp = "^[가-힣0-9]{2,10}$")
    private String nickname;

    @Pattern(regexp = "^[가-힣]{2,6}$")
    private String name;

    @NotBlank
    @Email
    @Pattern(regexp="^[A-Za-z0-9.%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$")
    private String email;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @Pattern(regexp = "^010-[1-9][0-9]{3}-[0-9]{4}$")
    private String contact;

    @Pattern(regexp="^[0-9]{5,6}$")
    private String post;

    @Size(max = 100)
    private String address1;

    @Size(max = 100)
    private String address2;

    private String role;

    private Long point;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdTime;

    // 필요 시 아래도 동일하게 적용
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    // private LocalDateTime editTime;

    // private LocalDateTime recentTime;
}
