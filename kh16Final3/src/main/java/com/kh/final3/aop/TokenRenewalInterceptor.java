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
		
		//이 인터셉터는 모든 주소에 적용할 예정
		//다음 경우는 고려할 필요가 없음
		//1. OPTIONS 요청인 경우
		//2. Authorization 헤더가 없는 경우 (비회원)
		//3. 액세스 토큰의 남은 시간이 충분한 경우 (10분 이상)
		
		//1
		//if(request.getMethod().equals("OPTIONS")) {
		if(request.getMethod().equalsIgnoreCase("options")) {
			return true;
		}
		//2
		String bearerToken = request.getHeader("Authorization");
		if(bearerToken == null) {//없으면 비회원
			return true;
		}
		try {//Plan A : 토큰 재발급 여부 검사
			
			//3. 토큰의 남은 시간 구하기
			//- 이미 만료된 토큰이라면 예외가 발생한다는 것을 주의!
			long ms = tokenService.getRemain(bearerToken);
			//if(ms >= 10 * 60 * 1000) {//10분 이상 남았다면
			if(ms >= jwtProperties.getRenewalLimit() * 60L * 1000L) {
				return true;
			}
			
			//위에서 통과되지 못했다면 토큰의 남은시간이 촉박하다는 뜻
			//→ 토큰을 재발급해준다
			TokenVO tokenVO = tokenService.parse(bearerToken);
			String newAccessToken = tokenService.generateAccessToken(
				MemberDto.builder()
					.id(tokenVO.getLoginId())
					.role(tokenVO.getLoginLevel())
				.build()
			);
			
			//발급한 토큰을 클라이언트에게 전송해야함
			//→ 응답 헤더(response header)에 정보를 추가하여 전달
			//→ 이렇게 처리해야 컨트롤러의 처리를 방해하지 않음
			//→ response.setHeader("이름", "값");
			
			//response.setHeader("x-access-token", newAccessToken);//옛날방식(x- 접두사 추가)
			
			response.setHeader("Access-Control-Expose-Headers", "Access-Token");//노출시킬 헤더명을 기재
			response.setHeader("Access-Token", newAccessToken);//최근방식(x- 없이 의미 명확하게)
			
			return true;
		}
		//catch(Exception e) {//전체 오류 시
		catch(ExpiredJwtException e) {//토큰 만료 시(Plan B)
			//클라이언트에게 에러 전송
			//response.sendError(401, "TOKEN_EXPIRED");//이렇게 보내면 클라이언트가 정보 부족

			response.setStatus(401);
			response.setContentType("application/json; charset=UTF-8");
			Map<String, String> body = new HashMap<>();
			body.put("status", "401");
			body.put("message", "TOKEN_EXPIRED");
			ObjectMapper mapper = new ObjectMapper();//JSON 수동 생성기
			String json = mapper.writeValueAsString(body);//JSON 생성
			response.getWriter().write(json);//내보내도록 처리
			
			return false;//진행중인 요청 차단
		}
		
	}
}
