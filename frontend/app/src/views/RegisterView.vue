<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="8" md="6">
        <v-card class="pa-4">
          <v-card-title class="text-center">
            <h1>회원가입</h1>
          </v-card-title>
          <v-card-text>
            <v-form @submit.prevent="submitForm">
              <v-text-field v-model="formData.loginId" label="로그인 ID" required></v-text-field>
              <v-text-field v-model="formData.password" label="비밀번호" type="password" required></v-text-field>
              <v-text-field v-model="formData.nickName" label="닉네임" required></v-text-field>
              <v-text-field v-model="formData.email" label="이메일" type="email" required></v-text-field>
              <v-text-field v-model="formData.countryCode" label="국가 코드" required></v-text-field>
              <v-text-field v-model="formData.gender" label="성별 (M/F)" required></v-text-field>
              <v-text-field v-model="formData.birthDate" label="생년월일 (YYYY-MM-DD)" required></v-text-field>
              <v-btn type="submit" color="primary" block class="mt-4">가입하기</v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const formData = ref({
  loginId: '',
  password: '',
  nickName: '',
  email: '',
  countryCode: '',
  gender: '',
  birthDate: '',
});

const submitForm = () => {
  // This is where we will call the registration API
  console.log('Form submitted:', formData.value);
};
</script>
