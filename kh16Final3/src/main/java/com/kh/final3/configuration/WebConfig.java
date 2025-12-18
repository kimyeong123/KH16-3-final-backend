package com.kh.final3.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.final3.aop.MemberInterceptor;
import com.kh.final3.aop.TokenRenewalInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private MemberInterceptor memberInterceptor;

    @Autowired
    private TokenRenewalInterceptor tokenRenewalInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // // 1. 토큰 복구 및 자동 갱신 (순서 1)
        registry.addInterceptor(tokenRenewalInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/member/login", "/member/register", "/cert/**"
                )
                .order(1);

        // // 2. 회원 권한 검사 (순서 2)
        registry.addInterceptor(memberInterceptor)
                .addPathPatterns("/qna/**", "/board/**", "/member/mypage")
                .excludePathPatterns(
                    "/board/list", 
                    "/board/detail/**", // // 자유게시판 상세는 누구나 열람
                    "/member/login",
                    "/member/register"
                    // // /qna/list와 /qna/detail/**는 제외하지 않음 (토큰 필요)
                )
                .order(2);
    }
}