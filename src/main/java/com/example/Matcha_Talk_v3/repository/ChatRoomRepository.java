package com.example.Matcha_Talk_v3.repository;

import com.example.Matcha_Talk_v3.domain.dto.ChatRoom;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Repository
public class ChatRoomRepository {

    private Map<String, ChatRoom> chatRoomMap;

    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    public List<ChatRoom> findAllRoom() {
        // 채팅방 생성순서 최근 순으로 반환
        List<ChatRoom> chatRooms = new ArrayList<>(chatRoomMap.values());
        Collections.reverse(chatRooms);
        return chatRooms;
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomMap.get(roomId);
    }

    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    public void addParticipant(String roomId, String participant) {
        ChatRoom room = chatRoomMap.get(roomId);
        if (room != null) {
            room.getParticipants().add(participant);
        }
    }

    public void removeParticipant(String roomId, String participant) {
        ChatRoom room = chatRoomMap.get(roomId);
        if (room != null) {
            room.getParticipants().remove(participant);
        }
    }
}
