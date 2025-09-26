<script setup lang="ts">
import { ref, onMounted } from 'vue'

const message = ref('Loading...')

onMounted(async () => {
  try {
    const response = await fetch('/api/hello')
    const data = await response.json()
    message.value = data.message
  } catch (error) {
    message.value = 'Failed to load message from backend.'
    console.error(error)
  }
})
</script>

<template>
  <main>
    <h1>{{ message }}</h1>
  </main>
</template>

<style scoped>
main {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 2rem;
}
</style>
