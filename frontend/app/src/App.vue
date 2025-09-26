<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { storeToRefs } from 'pinia'

const router = useRouter()
const authStore = useAuthStore()
const { isAuthenticated } = storeToRefs(authStore)

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <v-app>
    <v-app-bar app>
      <v-toolbar-title>MatchaTalk</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn to="/" text>Home</v-btn>
      
      <template v-if="!isAuthenticated">
        <v-btn to="/register" text>Register</v-btn>
        <v-btn to="/login" text>Login</v-btn>
      </template>
      
      <template v-else>
        <v-btn @click="handleLogout" text>Logout</v-btn>
      </template>

    </v-app-bar>

    <v-main>
      <RouterView />
    </v-main>
  </v-app>
</template>

<style scoped>
/* Specific styles can be added here if needed */
</style>
