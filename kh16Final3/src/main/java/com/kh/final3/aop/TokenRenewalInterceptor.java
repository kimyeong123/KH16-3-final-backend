package com.kh.final3.aop;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.final3.configuration.JwtProperties;
import com.kh.final3.dto.MemberDto;
import com.kh.final3.service.TokenService;
import com.kh.final3.vo.TokenVO;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class TokenRenewalInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        if (request.getMethod().equalsIgnoreCase("options")) return true;

        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || bearerToken.isBlank()) {
            return true; // // 토큰 없으면 그냥 통과 (컨트롤러에서 처리)
        }

        try { 
            TokenVO tokenVO = tokenService.parse(bearerToken); 

            request.setAttribute("tokenVO", tokenVO); 
            request.setAttribute("memberNo", tokenVO.getMemberNo());
            
            long ms = tokenService.getRemain(bearerToken);
            
            if (ms < jwtProperties.getRenewalLimit() * 60L * 1000L) {
                String newAccessToken = tokenService.generateAccessToken(
                    MemberDto.builder()
                        .memberNo(tokenVO.getMemberNo())
                        .id(tokenVO.getLoginId())
                        .role(tokenVO.getLoginLevel())
                        .build()
                );
                response.setHeader("Access-Control-Expose-Headers", "Access-Token");
                response.setHeader("Access-Token", newAccessToken);
            }
            return true; 
        }
        catch (ExpiredJwtException e) {
            // // [수정] 만료되어도 차단하지(false) 않고 보냅니다.
            // // 대신 클라이언트가 알 수 있게 헤더만 살짝 남기거나 그냥 통과시킵니다.
            System.out.println("토큰 만료됨: 일단 통과");
            return true; 
        } 
        catch (Exception e) {
            // // [수정] 서명 오류 등 어떤 에러가 나도 일단 컨트롤러로 보냅니다.
            System.out.println("토큰 기타 오류: 일단 통과");
            return true;
        }
    }
}