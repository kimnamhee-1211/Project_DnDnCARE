package com.kh.dndncare.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
	    registry.setApplicationDestinationPrefixes("/send"); // 클라이언트가 메시지를 보낼 때 사용할 prefix 설정
	    registry.enableSimpleBroker("/room"); // 메시지를 구독한 클라이언트들에게 전달할 주소
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
	    registry.addEndpoint("/ws-stomp") // 클라이언트가 WebSocket에 연결할 엔드포인트 설정
	    		.setAllowedOriginPatterns("http://192.168.40.*")
	            .withSockJS(); // SockJS를 지원하여 낮은 버전의 브라우저도 WebSocket을 사용할 수 있도록 설정
	}
	
	
}