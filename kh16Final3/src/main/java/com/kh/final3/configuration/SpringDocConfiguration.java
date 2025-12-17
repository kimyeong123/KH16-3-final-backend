package com.kh.final3.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

//SpringDoc의 대표 정보 설정
@Configuration
public class SpringDocConfiguration {
	//정보 객체를 등록
	@Bean
	public OpenAPI info() {
		//문서의 대표 정보 생성
		Info info = new Info();
		info.setVersion("0.0.1");
		info.setTitle("KH정보교육원 수업용 REST API");
		info.setDescription("ReactJS와 통신하기 위한 모든 REST service 정보 명세");
		
		//(+추가) JWT 기반의 인증방식이 적용되었으므로 문서에 이를 반영해야한다
		String jwtHeaderName = "Authorization";//헤더 이름
		SecurityRequirement requirement = new SecurityRequirement();//보안 요구사항 객체
		requirement.addList(jwtHeaderName);//헤더 등록
		
		//(+추가) 임시 로그인을 처리하는 도구
		Components components = new Components();
		components.addSecuritySchemes(
				jwtHeaderName,
				new SecurityScheme()
						.name(jwtHeaderName)//헤더이름
						.type(SecurityScheme.Type.HTTP)//통신유형
						.scheme("bearer")//토큰의 종류
		);
		
		return new OpenAPI().info(info)
										.addSecurityItem(requirement)
										.components(components);
	}
}