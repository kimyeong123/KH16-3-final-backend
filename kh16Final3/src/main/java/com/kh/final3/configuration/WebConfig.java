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
        // 기존 CORS 설정 유지
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        
        // 1. [MemberInterceptor 등록]: 인증(tokenVO 저장) 및 권한 검사 담당
        registry.addInterceptor(memberInterceptor)
                .addPathPatterns(
                    "/board/**", // 게시판의 모든 요청을 인증 대상으로 포함
                    "/member/mypage",
                    "/member/edit",
                    "/account/logout",
                    "/message/**"
                )
                .excludePathPatterns(
                    // 비회원 접근 가능
                		"/board/list", 
                        "/board/{boardNo:\\d+}",//상세보기
                        "/account/login", 
                        "/account/join"
                );
        
        // 2. [TokenRenewalInterceptor 등록]: 토큰 갱신 담당
        registry.addInterceptor(tokenRenewalInterceptor)
                .addPathPatterns("/**") // 모든 요청을 대상으로 설정
                .excludePathPatterns(
                    // TokenRenewalInterceptor가 불필요한 경로 제외
                    "/account/refresh", 
                    "/account/join",    
                    "/account/login",   
                    "/cert/**",         
                    "/ws",              
                    "/websocket/**"     
                );
    }
}