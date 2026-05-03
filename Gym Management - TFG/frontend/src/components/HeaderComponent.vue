<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LogoutButton from '../components/LogoutBoton.vue' // Ajusta ruta según donde esté tu archivo

const router = useRouter()
const rol = ref(localStorage.getItem('rol'))
const nombreGimnasio = ref('')
const email = ref(localStorage.getItem('email'))
const userInfo = ref(null)
const isMobileMenuOpen = ref(false)

const isUsuario = computed(() => rol.value === 'USUARIO')
const isEntrenador = computed(() => rol.value === 'ENTRENADOR')
const isAdmin = computed(() => rol.value === 'ADMIN')

async function cargarInformacionUsuario() {
  if ((isUsuario.value || isEntrenador.value) && email.value) {
    try {
      let responseGimnasio;
      const token = localStorage.getItem('token');
      const authHeader = 'Bearer ' + token;
      if (isUsuario.value) {
        responseGimnasio = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email.value}/gimnasio`, {
          headers: { 'Authorization': authHeader }
        });
      } else if (isEntrenador.value) {
        responseGimnasio = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${email.value}/gimnasio`, {
          headers: { 'Authorization': authHeader }
        });
      }
      if (responseGimnasio && responseGimnasio.ok) {
        const gimnasio = await responseGimnasio.json();
        nombreGimnasio.value = gimnasio.nombre;
      }

      let responseInfo;
      if (isUsuario.value) {
        responseInfo = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email.value}`, {
          headers: { 'Authorization': authHeader }
        });
      } else if (isEntrenador.value) {
        responseInfo = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${email.value}`, {
          headers: { 'Authorization': authHeader }
        });
      }
      if (responseInfo && responseInfo.ok) {
        userInfo.value = await responseInfo.json();
      }
    } catch (error) {
      console.error('Error al cargar información:', error);
    }
  }
}


function toggleMenu() {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
  document.body.style.overflow = isMobileMenuOpen.value ? 'hidden' : ''
}

function closeMobileMenu() {
  isMobileMenuOpen.value = false
  document.body.style.overflow = ''
}

onMounted(() => {
  cargarInformacionUsuario()
})
</script>

<template>
  <header class="header">
    <div class="header-left">
      <router-link to="/home" class="brand-link">
        <i class="fas fa-dumbbell me-2"></i>
        <span class="brand-text">Gimnasios TFG</span>
      </router-link>
      <div v-if="nombreGimnasio" class="gym-name">
        <i class="fas fa-building me-2"></i>
        {{ nombreGimnasio }}
      </div>
    </div>

    <!-- Botón hamburguesa para móvil -->
    <button class="mobile-menu-btn" @click="toggleMenu" aria-label="Menú">
      <div class="hamburger-icon">
        <span class="line"></span>
        <span class="line"></span>
        <span class="line"></span>
      </div>
    </button>

    <nav class="nav" :class="{ 'nav-mobile-open': isMobileMenuOpen }">
      <template v-if="isUsuario">
        <router-link to="/user/rutinas" class="nav-link" @click="closeMobileMenu">
          <i class="fas fa-dumbbell"></i>
          <span>Rutinas</span>
        </router-link>
        <router-link to="/user/clases-colectivas" class="nav-link" @click="closeMobileMenu">
          <i class="fas fa-users"></i>
          <span>Clases</span>
        </router-link>
        <router-link to="/user/medallas" class="nav-link" @click="closeMobileMenu">
          <i class="fas fa-medal"></i>
          <span>Medallas</span>
        </router-link>
        <router-link to="/user/accesos" class="nav-link" @click="closeMobileMenu">
          <i class="fas fa-history"></i>
          <span>Accesos</span>
        </router-link>
        <router-link to="/user/aforo" class="nav-link" @click="closeMobileMenu">
          <i class="fas fa-chart-bar"></i>
          <span>Aforo</span>
        </router-link>
        <div class="nav-divider"></div>
      </template>

      <template v-if="isEntrenador">

        <router-link to="/entrenador/horario" class="nav-link" @click="closeMobileMenu">
          <i class="fas fa-clock"></i>
          <span>Horario</span>
        </router-link>
        <router-link to="/entrenador/rutinas-entrenador" class="nav-link" @click="closeMobileMenu">
          <i class="fas fa-clipboard-list"></i>
          <span>Rutinas</span>
        </router-link>
        <div class="nav-divider"></div>
      </template>

      <router-link v-if="isUsuario" 
                   to="/user/entrada-salida"
                   class="nav-link qr-button"
                   @click="closeMobileMenu">
        <i class="fas fa-qrcode"></i>
        <span>QR Acceso</span>
      </router-link>

     <template v-if="isUsuario || isEntrenador || isAdmin">
        <LogoutButton @click="closeMobileMenu()" />
      </template>
    </nav>
  </header>
</template>
<style scoped>
.header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #474448;
  color: #fff;
  padding: 1rem 2rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.brand-link {
  display: flex;
  align-items: center;
  font-size: 1.5rem;
  font-weight: bold;
  text-decoration: none;
  color: #fff !important;
  transition: all 0.3s ease;
}

.brand-link:hover,
.brand-link:focus {
  color: #4caf50 !important;
  outline: none;
}

.brand-link:focus-visible {
  outline: 2px solid #4caf50;
  outline-offset: 2px;
}

.gym-name {
  font-size: 1.1rem;
  color: #e0e0e0;
  padding-left: 1.5rem;
  border-left: 2px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.nav-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #e0e0e0;
  text-decoration: none;
  font-weight: 500;
  padding: 1.1rem 2rem;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  min-width: 140px;
  gap: 0.5rem;
  cursor: pointer;
  border: none;
  background: transparent;
}

.nav-link span {
  white-space: nowrap;
}

.nav-link i {
  font-size: 1.2rem;
  width: 20px;
  text-align: center;
  transition: transform 0.3s ease;
}

.nav-link:hover,
.logout-button:hover,
.qr-button:hover {
  color: #2d232e;
  background: #e0ddcf;
}

.nav-link:hover i,
.logout-button:hover i,
.qr-button:hover i {
  transform: scale(1.1);
}

.nav-link:focus-visible,
.logout-button:focus-visible,
.qr-button:focus-visible {
  outline: 2px solid #e0ddcf;
  outline-offset: 2px;
}

.nav .router-link-active {
  color: #2d232e;
  background: #e0ddcf;
  font-weight: 600;
}

.nav-divider {
  width: 2px;
  height: 2rem;
  background: rgba(255, 255, 255, 0.15);
  margin: 0 0.5rem;
}

.logout-button {
  color: #e0e0e0;
  border: none;
  background: transparent;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 1.1rem 2rem;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-weight: 500;
  min-width: 140px;
  gap: 0.5rem;
}

.mobile-menu-btn {
  display: none;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  cursor: pointer;
  padding: 0.5rem;
  width: 48px;
  height: 48px;
  align-items: center;
  justify-content: center;
  z-index: 1001;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.hamburger-icon {
  width: 24px;
  height: 18px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.hamburger-icon .line {
  display: block;
  width: 100%;
  height: 2px;
  background-color: #fff;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.mobile-menu-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

.mobile-menu-btn:hover .line {
  background-color: #e0ddcf;
}

.qr-button {
  color: #e0e0e0;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 1.1rem 2rem;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-weight: 500;
  min-width: 140px;
  margin-right: 1rem;
  gap: 0.5rem;
}

.qr-button span {
  white-space: nowrap;
}

.qr-button i {
  font-size: 1.2rem;
  width: 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .header {
    padding: 0.5rem 1rem;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .nav {
    display: block;
    position: fixed;
    top: -100%;
    left: 0;
    right: 0;
    bottom: 0;
    background: #474448;
    flex-direction: column;
    padding: 1rem;
    gap: 0.5rem;
    overflow-y: auto;
    z-index: 1000;
    opacity: 0;
    transition: all 0.3s ease-in-out;
    height: calc(100vh - 60px);
    margin-top: 60px;
  }

  .nav-mobile-open {
    top: 0;
    opacity: 1;
  }

  .nav-link {
    width: 100%;
    padding: 1rem;
    justify-content: flex-start;
    border-radius: 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  .nav-link span {
    margin-left: 1rem;
  }

  .nav-link i {
    width: 24px;
    margin: 0;
  }

  .nav-divider {
    display: none;
  }

  .header-left {
    flex: 1;
  }

  .gym-name {
    display: none;
  }

  .brand-link {
    font-size: 1.2rem;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 0.75rem;
  }

  .brand-link {
    font-size: 1rem;
  }
}
</style>
