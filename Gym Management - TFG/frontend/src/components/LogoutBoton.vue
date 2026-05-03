<template>
  <button @click="logoutUsuario" class="nav-link logout-button">
    <i class="fas fa-sign-out-alt"></i>
    <span>Cerrar sesión</span>
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

function logoutUsuario() {
  localStorage.removeItem('token')
  localStorage.removeItem('rol')
  localStorage.removeItem('email')
  router.push('/')
}
</script>
