package com.example.Matcha_Talk_v3.controller;

import com.example.Matcha_Talk_v3.domain.dto.ChatMessage;
import com.example.Matcha_Talk_v3.domain.dto.SignalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        messagingTemplate.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping("/chat/signal")
    public void signal(@Payload SignalMessage signalMessage) {
        log.info("Signal received: from={}, to={}, type={}", signalMessage.getSender(), signalMessage.getReceiver(), signalMessage.getType());
        // 수신자에게 직접 시그널링 메시지 전송
        messagingTemplate.convertAndSendToUser(
                signalMessage.getReceiver(),
                "/queue/signal",
                signalMessage
        );
    }
}
