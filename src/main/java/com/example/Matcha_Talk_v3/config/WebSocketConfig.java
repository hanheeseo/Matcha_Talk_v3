package com.example.Matcha_Talk_v3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커가 /topic으로 시작하는 주소를 구독하는 클라이언트들에게 메시지를 전달하도록 설정
        config.enableSimpleBroker("/topic");
        // 클라이언트에서 서버로 메시지를 보낼 때 /app으로 시작하는 주소를 사용하도록 설정
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트들이 WebSocket에 연결할 때 사용할 엔드포인트를 /ws로 설정
        // SockJS는 WebSocket을 지원하지 않는 브라우저에서도 비슷한 경험을 제공하기 위한 폴백 옵션
        registry.addEndpoint("/ws").withSockJS();
    }
}
