<script setup>
import { ref, computed } from 'vue'
import HomeUsuario from './HomeUser.vue'
import HomeEntrenador from './HomeEntrenador.vue'
import HomeAdmin from './HomeAdmin.vue'

const rol = ref(localStorage.getItem('rol'))

const isUsuario = computed(() => rol.value === 'USUARIO')
const isEntrenador = computed(() => rol.value === 'ENTRENADOR')
const isAdmin = computed(() => rol.value === 'ADMIN')
</script>

<template>
  <HomeUsuario v-if="isUsuario" />
  <HomeEntrenador v-else-if="isEntrenador" />
  <HomeAdmin v-else-if="isAdmin" />
  <div v-else class="text-center mt-5">
    <h2>No has iniciado sesión.</h2>
    <router-link to="/">Volver al inicio</router-link>
  </div>
</template>