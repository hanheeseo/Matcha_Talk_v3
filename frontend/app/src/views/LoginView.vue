<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="pa-4">
          <v-card-title class="text-center">
            <h1>로그인</h1>
          </v-card-title>
          <v-card-text>
            <v-form @submit.prevent="submitForm">
              <v-text-field v-model="formData.loginId" label="로그인 ID" required></v-text-field>
              <v-text-field v-model="formData.password" label="비밀번호" type="password" required></v-text-field>
              <v-btn type="submit" color="primary" block class="mt-4">로그인</v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const authStore = useAuthStore();
const formData = ref({
  loginId: '',
  password: '',
});

const submitForm = async () => {
  try {
    await api.post('/users/login', formData.value);
    authStore.login(); // Update authentication state
    router.push('/'); // Redirect to home
  } catch (error: any) {
    alert('로그인 실패: ' + (error.response?.data || error.message));
  }
};
</script>
