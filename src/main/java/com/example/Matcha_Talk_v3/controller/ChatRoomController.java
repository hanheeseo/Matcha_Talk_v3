package com.example.Matcha_Talk_v3.controller;

import com.example.Matcha_Talk_v3.domain.dto.ChatRoom;
import com.example.Matcha_Talk_v3.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    public List<ChatRoom> room() {
        return chatRoomRepository.findAllRoom();
    }

    // 채팅방 생성
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestBody Map<String, String> payload) {
        return chatRoomRepository.createChatRoom(payload.get("name"));
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    public ChatRoom roomInfo(@PathVariable("roomId") String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }
}
