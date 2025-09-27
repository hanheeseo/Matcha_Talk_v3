<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4">채팅방 목록</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>새 채팅방 만들기</v-card-title>
          <v-card-text>
            <v-text-field
              v-model="newRoomName"
              label="채팅방 이름"
              variant="outlined"
              @keyup.enter="createRoom"
            ></v-text-field>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="createRoom">만들기</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-list lines="one">
          <v-list-item
            v-for="room in chatRooms"
            :key="room.roomId"
            :title="room.name"
            @click="enterRoom(room.roomId)"
          >
            <template v-slot:append>
              <v-btn color="grey" icon="mdi-arrow-right"></v-btn>
            </template>
          </v-list-item>
        </v-list>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/services/api';

interface ChatRoom {
  roomId: string;
  name: string;
}

const router = useRouter();
const chatRooms = ref<ChatRoom[]>([]);
const newRoomName = ref('');

const fetchChatRooms = async () => {
  try {
    const response = await api.get<ChatRoom[]>('/chat/rooms');
    chatRooms.value = response.data;
  } catch (error) {
    console.error('채팅방 목록을 불러오는 데 실패했습니다.', error);
    alert('채팅방 목록을 불러오는 데 실패했습니다.');
  }
};

const createRoom = async () => {
  if (!newRoomName.value.trim()) {
    alert('채팅방 이름을 입력하세요.');
    return;
  }
  try {
    await api.post('/chat/room', { name: newRoomName.value });
    newRoomName.value = '';
    await fetchChatRooms(); // 목록 새로고침
  } catch (error) {
    console.error('채팅방 생성에 실패했습니다.', error);
    alert('채팅방 생성에 실패했습니다.');
  }
};

const enterRoom = (roomId: string) => {
  router.push(`/chat/${roomId}`);
};

onMounted(() => {
  fetchChatRooms();
});
</script>
