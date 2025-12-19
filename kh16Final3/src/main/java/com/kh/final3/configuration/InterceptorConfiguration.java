package com.kh.final3.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.final3.aop.MemberInterceptor;
import com.kh.final3.aop.TokenRenewalInterceptor;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Autowired
    private MemberInterceptor memberInterceptor;

    @Autowired
    private TokenRenewalInterceptor tokenRenewalInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(memberInterceptor)
            .addPathPatterns(
//                "/product/**",
                "/member/joinfinish",
                "/member/logout",
                "/member/mypage"
            )
            .excludePathPatterns(
                "/product",
                "/product/"
            );

//        registry.addInterceptor(tokenRenewalInterceptor)
//            .addPathPatterns("/**")
//            .excludePathPatterns(
//                "/member/refresh",
//                "/member/join",
//                "/member/login",
//                "/member/logout",
//                "/member/memberId"
//            );
    }
}
