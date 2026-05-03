<template>
  <div id="app">
    <HeaderComponent v-if="!route.meta.hideHeader && isAuthenticated" />
    <router-view />
  </div>
</template>

<script setup>
import HeaderComponent from './components/HeaderComponent.vue'
import { useRoute } from 'vue-router'
import { useUserStore } from './stores/user'
import { computed } from 'vue'
import './style.css'

const route = useRoute()
const userStore = useUserStore()
const isAuthenticated = computed(() => userStore.getIsAuthenticated)
</script>