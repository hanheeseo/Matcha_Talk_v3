<template>
  <v-container class="py-6 fill-height">
    <v-row class="fill-height">
      <!-- Video Chat Section -->
      <v-col cols="12" md="8">
        <v-row>
          <v-col cols="12">
            <v-card class="video-card mb-4" elevation="2">
              <v-toolbar density="compact">
                <v-toolbar-title class="text-subtitle-2">Remote Camera</v-toolbar-title>
              </v-toolbar>
              <video ref="remoteVideo" autoplay playsinline class="video-player"></video>
            </v-card>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="12" md="6">
            <v-card class="video-card" elevation="2">
              <v-toolbar density="compact">
                <v-toolbar-title class="text-subtitle-2">My Camera</v-toolbar-title>
              </v-toolbar>
              <video ref="localVideo" autoplay playsinline muted class="video-player"></video>
            </v-card>
          </v-col>
          <v-col cols="12" md="6" class="d-flex align-center justify-center">
            <div class="controls-container">
              <v-btn class="mx-2" color="primary" @click="initiateCall">
                <v-icon left>mdi-camera</v-icon>
                Start Call
              </v-btn>
              <v-btn class="mx-2" :color="isMuted ? 'grey' : 'warning'" @click="toggleMute">
                <v-icon left>{{ isMuted ? 'mdi-microphone-off' : 'mdi-microphone' }}</v-icon>
                {{ isMuted ? 'Mute' : 'Unmute' }}
              </v-btn>
              <v-btn class="mx-2" color="error" @click="hangUp">
                <v-icon left>mdi-phone-hangup</v-icon>
                Hang Up
              </v-btn>
            </div>
          </v-col>
        </v-row>
      </v-col>

      <!-- Text Chat Section -->
      <v-col cols="12" md="4">
        <v-card class="d-flex flex-column fill-height" elevation="2">
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
              @keyup.enter="handleSendMessage"
            ></v-text-field>
            <v-btn icon="mdi-send" color="pink" class="ml-2" @click="handleSendMessage"></v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import api from '@/services/api';
import { useWebRTC } from '@/composables/useWebRTC';
import { useStompChat } from '@/composables/useStompChat';
import type { ChatRoom, SignalMessage } from '@/types/chat';

// Basic Setup
const route = useRoute();
const roomId = route.params.roomId as string;
const sender = ref('');
const room = ref<ChatRoom>({ roomId: '', name: '' });

// Initialize composables with a wrapper to handle circular dependency
const webrtc = useWebRTC((type: 'offer' | 'answer' | 'candidate', data: any) => {
  stomp.sendSignal(type, data);
});

const stomp = useStompChat(roomId, sender, (signal: SignalMessage) => {
  webrtc.handleSignal(signal);
});

// Destructure properties and methods for easy access in the template and script
const { localVideo, remoteVideo, isMuted, initiateCall, toggleMute, hangUp } = webrtc;
const { messages, connect, sendMessage } = stomp;

// Text Chat state and UI
const newMessage = ref('');
const messageContainer = ref<HTMLElement | null>(null);

const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
    }
  });
};

const handleSendMessage = () => {
  if (newMessage.value.trim()) {
    sendMessage(newMessage.value);
    newMessage.value = '';
    scrollToBottom();
  }
};

// Lifecycle Hooks
onMounted(async () => {
  // Fetch room details
  try {
    const response = await api.get<ChatRoom>(`/chat/room/${roomId}`);
    room.value = response.data;
  } catch (error) {
    console.error('Failed to load chat room info.', error);
    alert('Could not load chat room info.');
  }

  // Set sender username
  let storedSender = localStorage.getItem('chat_sender');
  if (!storedSender) {
    storedSender = prompt('Enter your nickname for the chat:', 'Anonymous');
    if (storedSender) {
      localStorage.setItem('chat_sender', storedSender);
    }
  }
  sender.value = storedSender || 'Anonymous';

  // Connect to WebSocket
  connect();
});
</script>

<style scoped>
.chat-card {
  height: 100%;
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

.video-card {
  display: flex;
  flex-direction: column;
  height: 100%; /* Fill the column height */
}

.video-player {
  width: 100%;
  background-color: #2c2c2c;
  object-fit: cover;
  flex-grow: 1;
}

.controls-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
</style>
