<template>
  <v-container class="py-6">
    <v-card class="chat-card" elevation="2">
      <v-toolbar color="pink-lighten-4" density="compact">
        <v-toolbar-title class="text-pink-darken-2">{{ room.name }}</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn icon to="/chat">
          <v-icon>mdi-arrow-left</v-icon>
        </v-btn>
      </v-toolbar>

      <v-card-text class="chat-messages" ref="messageContainer">
        <div v-for="(msg, index) in messages" :key="index" class="message-item" :class="{ 'my-message': msg.sender === sender }">
          <div v-if="msg.type === 'TALK'" class="message-bubble">
            <div class="font-weight-bold text-caption">{{ msg.sender }}</div>
            <div class="message-content">{{ msg.message }}</div>
          </div>
          <div v-else class="system-message">
            {{ msg.message }}
          </div>
        </div>
      </v-card-text>

      <v-divider></v-divider>

      <v-card-actions class="pa-4">
        <v-text-field
          v-model="newMessage"
          label="메시지를 입력하세요"
          variant="outlined"
          density="compact"
          hide-details
          @keyup.enter="sendMessage"
        ></v-text-field>
        <v-btn icon="mdi-send" color="pink" class="ml-2" @click="sendMessage"></v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import api from '@/services/api';
import SockJS from 'sockjs-client';
import { Client, type IFrame, type IMessage } from '@stomp/stompjs';

interface ChatRoom {
  roomId: string;
  name: string;
}

interface ChatMessage {
  type: 'ENTER' | 'TALK';
  roomId: string;
  sender: string;
  message: string;
}

const route = useRoute();
const roomId = route.params.roomId as string;

const room = ref<ChatRoom>({ roomId: '', name: '' });
const messages = ref<ChatMessage[]>([]);
const newMessage = ref('');
const sender = ref('');
const stompClient = ref<Client | null>(null);
const messageContainer = ref<HTMLElement | null>(null);

const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
    }
  });
};

const connect = () => {
  const socket = new SockJS('http://localhost:8080/ws');
  const client = new Client({
    webSocketFactory: () => socket,
    debug: (str) => console.log(str),
    onConnect: (frame: IFrame) => {
      console.log('STOMP 연결 성공:', frame);
      
      // 채팅방 구독
      client.subscribe(`/topic/chat/room/${roomId}`, (message: IMessage) => {
        const receivedMessage: ChatMessage = JSON.parse(message.body);
        messages.value.push(receivedMessage);
        scrollToBottom();
      });

      // 입장 메시지 전송
      client.publish({
        destination: '/app/chat/message',
        body: JSON.stringify({
          type: 'ENTER',
          roomId: roomId,
          sender: sender.value,
          message: ''
        } as ChatMessage),
      });
    },
    onStompError: (frame: IFrame) => {
      console.error('STOMP 에러:', frame);
    },
  });

  client.activate();
  stompClient.value = client;
};

const disconnect = () => {
  if (stompClient.value) {
    stompClient.value.deactivate();
    console.log('STOMP 연결 해제');
  }
};

const sendMessage = () => {
  if (stompClient.value && stompClient.value.connected && newMessage.value.trim()) {
    stompClient.value.publish({
      destination: '/app/chat/message',
      body: JSON.stringify({
        type: 'TALK',
        roomId: roomId,
        sender: sender.value,
        message: newMessage.value,
      } as ChatMessage),
    });
    newMessage.value = '';
  }
};

onMounted(async () => {
  try {
    const response = await api.get<ChatRoom>(`/chat/room/${roomId}`);
    room.value = response.data;
  } catch (error) {
    console.error('채팅방 정보를 불러오는 데 실패했습니다.', error);
    alert('채팅방 정보를 불러올 수 없습니다.');
  }

  let storedSender = localStorage.getItem('chat_sender');
  if (!storedSender) {
    storedSender = prompt('채팅에서 사용할 닉네임을 입력하세요:', '익명');
    if (storedSender) {
      localStorage.setItem('chat_sender', storedSender);
    }
  }
  sender.value = storedSender || '익명';

  connect();
});

onUnmounted(() => {
  disconnect();
});
</script>

<style scoped>
.chat-card {
  height: 85vh;
  display: flex;
  flex-direction: column;
}
.chat-messages {
  flex-grow: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #fce4ec; /* light pink background */
}
.message-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}
.message-bubble {
  padding: 8px 12px;
  border-radius: 16px;
  max-width: 70%;
  word-wrap: break-word;
}
.message-item:not(.my-message) .message-bubble {
  background-color: #ffffff;
  align-self: flex-start;
}
.message-item.my-message .message-bubble {
  background-color: #ffc1d5; /* pink for my messages */
  align-self: flex-end;
}
.message-content {
  font-size: 0.9rem;
}
.system-message {
  text-align: center;
  font-size: 0.8rem;
  color: #666;
  margin: 8px 0;
}
</style>