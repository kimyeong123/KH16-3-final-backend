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
public class TokenRenewalInterceptor implements HandlerInterceptor{
	@Autowired
	private TokenService tokenService;
	@Autowired
	private JwtProperties jwtProperties;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// ... (OPTIONS, Authorization 헤더 없음 처리 생략)
		
		String bearerToken = request.getHeader("Authorization");
		if(bearerToken == null) {
			return true;
		}
        
		try { 
            // 1. 토큰 파싱 및 TokenVO 생성 (인증)
            // 토큰이 유효하지 않으면 여기서 예외 발생 (catch로 이동)
            TokenVO tokenVO = tokenService.parse(bearerToken); 

            // 2. TokenVO를 request attribute에 저장 (컨트롤러에게 전달)
            request.setAttribute("tokenVO", tokenVO); 
            request.setAttribute("memberNo", tokenVO.getMemberNo());
            
            // 3. 토큰의 남은 시간 구하기 (갱신 여부 검사)
			long ms = tokenService.getRemain(bearerToken);
            
			// 4. 잔여 시간이 충분한 경우 (갱신 불필요)
			if(ms >= jwtProperties.getRenewalLimit() * 60L * 1000L) {
				return true; // 갱신 필요 없으므로 바로 통과 (tokenVO는 이미 설정됨)
			}
			
			// 5. 토큰의 남은 시간이 촉박한 경우 → 재발급 로직 실행
			String newAccessToken = tokenService.generateAccessToken(
				MemberDto.builder()
				.memberNo(tokenVO.getMemberNo())
					.id(tokenVO.getLoginId())
					.role(tokenVO.getLoginLevel())
				.build()
			);
			
			// 발급한 토큰을 클라이언트에게 전송
			response.setHeader("Access-Control-Expose-Headers", "Access-Token");
			response.setHeader("Access-Token", newAccessToken);
			
			return true; // 요청 진행
		}
		
		catch(ExpiredJwtException e) {//토큰 만료 시(Plan B)
			
			response.setStatus(401);
			response.setContentType("application/json; charset=UTF-8");
			Map<String, String> body = new HashMap<>();
			body.put("status", "401");
			body.put("message", "TOKEN_EXPIRED");
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(body);
			response.getWriter().write(json);
			
			return false;//진행중인 요청 차단
		} 
        // 일반적인 JWT 파싱 실패 (서명 오류 등)를 처리하는 catch도 추가해야 안정적입니다.
		catch(Exception e) {
			// 토큰이 있지만 유효하지 않은 경우 (서명 오류 등)
			response.setStatus(403); 
			response.setContentType("application/json; charset=UTF-8");
			Map<String, String> body = new HashMap<>();
			body.put("status", "403");
			body.put("message", "TOKEN_INVALID");
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(body);
			response.getWriter().write(json);
			
			return false;
		}
	}
}