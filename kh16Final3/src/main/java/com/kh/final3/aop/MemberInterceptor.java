package com.kh.final3.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.TokenService;
import com.kh.final3.vo.TokenVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class MemberInterceptor implements HandlerInterceptor {
	@Autowired
	private TokenService tokenService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, 
										HttpServletResponse response, Object handler)
			throws Exception {
		//목표 : 사용자가 보낸 요청의 헤더에 있는 Authorization 분석 및 판정
		
		//[1] OPTIONS 요청은 통과시킨다
		//- 통신이 가능한 대상인지 확인하는 선발대 형식의 통신
		//- CORS 상황이거나, 일반적인 요청방식(GET/POST/HEAD)이 아니면 발생	
		if(request.getMethod().equalsIgnoreCase("options")) {
			return true;
		}
		
		//[2] Authorization 헤더 검사
		try {//Plan A : 정상적인 로그인 상태
			String authorization = request.getHeader("Authorization");
			if(authorization == null)//헤더가 없음 = 비회원
				throw new UnauthorizationException();//플랜 B로 던져!	
			
			//토큰 해석
			TokenVO tokenVO = tokenService.parse(authorization);
			//-> 컨트롤러에서는 @RequestAttribute TokenVO tokenVO로 수신 가능
			request.setAttribute("tokenVO", tokenVO);
			return true;
		}
		catch(Exception e) {//Plan B : 비회원, 토큰만료/위변조 상황
			e.printStackTrace();
			response.sendError(401);//Unauthorized
			return false;
		}
	}
}