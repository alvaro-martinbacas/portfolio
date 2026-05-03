import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    isAuthenticated: localStorage.getItem('email') !== null,
    user: localStorage.getItem('email') ? { email: localStorage.getItem('email') } : null,
    role: localStorage.getItem('rol') || null
  }),

  actions: {
    setAuthenticated(value) {
      this.isAuthenticated = value
    },
    setUser(user) {
      this.user = user
    },
    setRole(role) {
      this.role = role
    },
    logout() {
      this.isAuthenticated = false
      this.user = null
      this.role = null
      localStorage.removeItem('email')
      localStorage.removeItem('rol')
    }
  },

  getters: {
    getIsAuthenticated: (state) => state.isAuthenticated,
    getUser: (state) => state.user,
    getRole: (state) => state.role
  }
})