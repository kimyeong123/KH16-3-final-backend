package com.kh.final3.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker // STOMP를 사용하도록 허용
@Configuration
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer{
	
	// 웹소켓 연결방식과 관련된 설정
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws") // 클라이언트가 접속해야 하는 주소 설정
			.setAllowedOriginPatterns("*") // CORS처럼 접속 가능한 클라이언트 주소 패턴 설정
			.withSockJS(); // SockJS라는 기술을 적용시켜 웹소켓 성능 향상 (웹소켓을 HTTP처럼 쓸 수 있게 해줌)
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 사용자가 메세지를 보낼 수 있는 창구를 개설
		registry.setApplicationDestinationPrefixes("/topic");
		
		// 사용자가 메세지를 받을 수 있는 구독 채널을 개설
		registry.enableSimpleBroker("/app");
	}
}
