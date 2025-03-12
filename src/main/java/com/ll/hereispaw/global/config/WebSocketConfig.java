package com.ll.hereispaw.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
        // 표준 WebSocket도 지원
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173","https://pawpaw.kknaks.site"); // 프론트엔드 주소
//        registry.addEndpoint("/ws")
//                .setAllowedOrigins("http://localhost:5173", "http://localhost:3000") // 프론트엔드 주소
//                .withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}