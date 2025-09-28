package com.example.Matcha_Talk_v3.config;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // CONNECT 요청 시, 헤더에서 사용자 이름을 가져와 세션에 Principal로 설정
            Optional.ofNullable(accessor.getFirstNativeHeader("username"))
                    .ifPresent(username -> {
                        Principal user = () -> username;
                        accessor.setUser(user);
                    });
        }
        return message;
    }
}
