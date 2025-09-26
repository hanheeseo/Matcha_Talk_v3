import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(false) // Placeholder

  // In the future, we will have actions to login, logout, etc.
  // and update isAuthenticated accordingly.

  return { isAuthenticated }
})
