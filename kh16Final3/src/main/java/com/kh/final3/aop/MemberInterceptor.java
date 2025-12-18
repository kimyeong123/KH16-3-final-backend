package com.kh.final3.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.final3.service.TokenService;
import com.kh.final3.vo.TokenVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class MemberInterceptor implements HandlerInterceptor {
	@Autowired
	private TokenService tokenService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	    if (request.getMethod().equalsIgnoreCase("options")) return true;

	    String authorization = request.getHeader("Authorization");
	    
	    if (authorization != null && !authorization.isBlank()) {
	        try {
	            // // TokenService에서 Bearer 제거 후 해석된 객체 반환
	            TokenVO tokenVO = tokenService.parse(authorization);
	            
	            // // 컨트롤러에서 request.getAttribute("tokenVO")로 꺼낼 수 있도록 세팅
	            request.setAttribute("tokenVO", tokenVO);
	        } catch (Exception e) {
	            System.out.println("인터셉터: 토큰 해석 실패");
	        }
	    }
	    return true; 
	}
}