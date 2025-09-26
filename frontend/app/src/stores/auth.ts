import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(false)

  function login() {
    // This will be replaced with token-based logic later
    isAuthenticated.value = true
  }

  function logout() {
    isAuthenticated.value = false
    // This will also clear the token later
  }

  return { isAuthenticated, login, logout }
})
