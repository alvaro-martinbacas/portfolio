<template>
  <div class="login-container">
    <!-- Imagen de fondo -->
    <div class="background-image"></div>
    
    <!-- Gradiente de fondo -->
    <div class="gradient-overlay"></div>

    <div class="container d-flex flex-column justify-content-center align-items-center min-vh-100 position-relative" style="z-index: 10;">
      <!-- Logo arriba con animación -->
      <div class="logo-container mb-3">
        <img
          src="../assets/logo.png"
          alt="Logo gimnasio"
          class="app-logo"
        />
        <div class="logo-glow"></div>
      </div>
      
      <h2 class="mb-4 text-center login-title">
        <span class="gradient-text">Iniciar Sesión</span>
      </h2>
      
      <div class="form-container">
        <form class="w-100 login-form" style="max-width: 350px;" @submit.prevent="login">
          <div class="mb-3">
            <label for="email" class="form-label">
              <i class="fas fa-envelope me-2"></i>Correo electrónico
            </label>
            <input v-model="email" type="email" class="form-control" id="email" required autocomplete="username" />
          </div>
          <div class="mb-3">
            <label for="clave" class="form-label">
              <i class="fas fa-lock me-2"></i>Contraseña
            </label>
            <input v-model="clave" type="password" class="form-control" id="clave" required autocomplete="current-password" />
          </div>
          <button class="button btn-lg w-100 btn-primary-custom" type="submit">
            <i class="fas fa-sign-in-alt me-2"></i>Entrar
          </button>
          <div class="mt-3 text-center">
            <router-link to="/registro" class="small-link">
              <i class="fas fa-user-plus me-1"></i>¿No estás apuntado? ¡Regístrate!
            </router-link>
          </div>
        </form>
      </div>
      <AlertMessage />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../stores/user';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';

const router = useRouter();
const userStore = useUserStore();
const { mostrarMensaje } = useAlertMessage();
const email = ref('');
const clave = ref('');

async function login() {
  try {
    // Usamos el endpoint unificado de autenticación
    const endpoint = '/gestiongimnasios/auth/login';

    const res = await fetch(`http://localhost:8080${endpoint}`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: email.value,
        clave: clave.value
      })
    });

    if (res.status === 404) {
      mostrarMensaje('Error del servidor: Endpoint no encontrado', 'danger');
      return;
    }

    let responseData;
    try {
      responseData = await res.json();
    } catch (e) {
      console.error('Error parsing response:', e);
      mostrarMensaje('Error al procesar la respuesta del servidor', 'danger');
      return;
    }

    if (!res.ok || responseData.error) {
      mostrarMensaje(responseData.error || 'Usuario o contraseña incorrectos', 'danger');
      return;
    }

    if (!responseData.token) {
      mostrarMensaje('No se recibió token de autenticación', 'danger');
      return;
    }

    localStorage.setItem('token', responseData.token);
    localStorage.setItem('email', email.value);
    localStorage.setItem('rol', responseData.rol);
    
    userStore.setAuthenticated(true);
    userStore.setUser({ email: email.value });
    userStore.setRole(responseData.rol);

    router.push('/home');

  } catch (error) {
    console.error('Error en login:', error);
    mostrarMensaje('Error de conexión al servidor', 'danger');
  }
}
</script>

<style scoped>
/* Contenedor principal con mismo fondo que Landing */
.login-container {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #F1F0EA 0%, #E0DDCF 50%, #F1F0EA 100%);
  overflow: hidden;
}

/* Imagen de fondo */
.background-image {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('../assets/fondo1.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.15;
  z-index: 0;
  filter: grayscale(20%) blur(1px);
}

/* Gradiente superpuesto igual que Landing */
.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, 
    rgba(45, 35, 46, 0.3) 0%, 
    rgba(83, 75, 82, 0.2) 50%,
    rgba(241, 240, 234, 0.8) 100%);
  z-index: 1;
}

/* Los estilos del logo ahora están en global.css */

.login-title {
  color: #2D232E;
  font-weight: 700;
  font-size: 2rem;
  animation: fadeInUp 1s ease-out 0.3s both;
}

.gradient-text {
  background: linear-gradient(45deg, #474448, #534B52, #2D232E);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
  0%, 100% { filter: hue-rotate(0deg); }
  50% { filter: hue-rotate(15deg); }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-container {
  animation: fadeInUp 1s ease-out 0.6s both;
  z-index: 15;
  position: relative;
}

.login-form {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(45, 35, 46, 0.1);
  border-radius: 20px;
  padding: 32px 24px 24px 24px;
  box-shadow: 0 16px 48px rgba(45, 35, 46, 0.15);
}

.form-label {
  color: #2D232E;
  font-weight: 600;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
}

.form-control {
  border: 2px solid rgba(45, 35, 46, 0.1);
  border-radius: 12px;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(5px);
  z-index: 20;
  position: relative;
}

.form-control:focus {
  border-color: #474448;
  box-shadow: 0 0 0 0.2rem rgba(71, 68, 72, 0.25), 0 4px 12px rgba(45, 35, 46, 0.1);
  background: #fff;
  transform: translateY(-1px);
}

.btn-primary-custom {
  background: linear-gradient(135deg, #474448 0%, #534B52 100%);
  border: 2px solid #474448;
  color: #F1F0EA;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 4px 12px rgba(45, 35, 46, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 20;
  position: relative;
}

.btn-primary-custom:hover {
  background: linear-gradient(135deg, #534B52 0%, #474448 100%);
  border-color: #534B52;
  color: #F1F0EA;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(45, 35, 46, 0.3);
}

.btn-lg {
  font-size: 1.1rem;
  padding: 0.85em 2em;
}

.small-link {
  font-size: 0.98rem;
  color: #534B52;
  text-decoration: none;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  padding: 0.5rem 1rem;
  border-radius: 8px;
}

.small-link:hover {
  color: #2D232E;
  text-decoration: none;
  background: rgba(45, 35, 46, 0.05);
  transform: translateY(-1px);
}

/* Responsive */
@media (max-width: 768px) {
  .background-image {
    opacity: 0.1;
    filter: grayscale(30%) blur(2px);
    background-position: center center;
  }
  
  .gradient-overlay {
    background: linear-gradient(45deg, 
      rgba(45, 35, 46, 0.4) 0%, 
      rgba(83, 75, 82, 0.3) 50%,
      rgba(241, 240, 234, 0.85) 100%);
  }
  
  /* Estilos responsive del logo en global.css */
  
  .login-title {
    font-size: 1.7rem;
  }
  
  .login-form {
    padding: 24px 16px;
    margin: 0 16px;
  }
  
  .form-control {
    border-radius: 10px;
  }
  
  .btn-primary-custom {
    border-radius: 10px;
  }
}
</style>
