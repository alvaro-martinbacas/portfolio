<template>
  <div class="registro-container">
    <div class="background-image"></div>
    
    <div class="gradient-overlay"></div>

    <div class="container d-flex flex-column justify-content-center align-items-center min-vh-100 position-relative" style="z-index: 10;">
      <div v-if="!gimnasioSeleccionado" class="logo-container mb-4">
        <img
          src="../assets/logo.png"
          alt="Logo gimnasio"
          class="app-logo"
        />
        <div class="logo-glow"></div>
      </div>

      <h2 v-if="!gimnasioSeleccionado" class="mb-4 text-center registro-title">
        <span class="gradient-text">
          <i class="fas fa-dumbbell me-2"></i>Selecciona un gimnasio
        </span>
      </h2>

      <div
        v-if="!gimnasioSeleccionado"
        class="gimnasios-grid w-100"
      >
        <div
          v-for="gim in gimnasios"
          :key="gim.id"
          class="gimnasio-card"
          @click="seleccionarGimnasio(gim)"
        >
          <div class="gimnasio-content">
            <h3 class="gimnasio-nombre">
              <i class="fas fa-building me-2"></i>{{ gim.nombre }}
            </h3>
            <p class="gimnasio-info">
              <i class="fas fa-map-marker-alt me-2"></i>{{ gim.direccion }}
            </p>
            <p class="gimnasio-info">
              <i class="fas fa-phone me-2"></i>{{ gim.telefono }}
            </p>
          </div>
        </div>
      </div>

      <div v-else class="form-container">
        <h2 class="registro-title mb-4 text-center">
          <span class="gradient-text">
            <i class="fas fa-user-plus me-2"></i>Registro en {{ gimnasioSeleccionado.nombre }}
          </span>
        </h2>
        <form @submit.prevent="registrarUsuario" class="registro-form">
          <div class="mb-3">
            <label class="form-label">
              <i class="fas fa-user me-2"></i>Nombre:
            </label>
            <input v-model="usuario.nombre" class="form-control" required />
          </div>
          <div class="mb-3">
            <label class="form-label">
              <i class="fas fa-user-tag me-2"></i>Apellidos:
            </label>
            <input v-model="usuario.apellidos" class="form-control" required />
          </div>
          <div class="mb-3">
            <label class="form-label">
              <i class="fas fa-phone me-2"></i>Teléfono:
            </label>
            <input v-model="usuario.tlf" class="form-control" required />
          </div>
          <div class="mb-3">
            <label class="form-label">
              <i class="fas fa-envelope me-2"></i>Email:
            </label>
            <input 
              :value="usuario.email"
              @input="e => usuario.email = e.target.value.toLowerCase()"
              type="email"
              class="form-control"
              required
            />
          </div>
          <div class="mb-3">
            <label class="form-label">
              <i class="fas fa-lock me-2"></i>Contraseña:
            </label>
            <input v-model="usuario.clave" type="password" class="form-control" required />
          </div>
          <!-- Custom Checkbox -->
          <div class="custom-checkbox-container mb-3">
            <label class="custom-checkbox-label">
              <input
                type="checkbox"
                v-model="pagarCuota"
                required
                class="custom-checkbox-input"
                id="pagarCuota"
              />
              <span class="custom-checkbox-box">
                <svg v-if="pagarCuota" width="16" height="16" viewBox="0 0 16 16">
                  <polyline points="3,9 7,13 13,5" style="fill:none;stroke:#4caf50;stroke-width:2"/>
                </svg>
              </span>
              <span class="ms-2">
                <i class="fas fa-credit-card me-1"></i>Pagar primera cuota ahora (simulado)
              </span>
            </label>
          </div>
          <button
            class="btn btn-lg w-100 btn-primary-custom mb-3"
            type="submit"
            :disabled="cargando || !pagarCuota"
          >
            <i class="fas fa-user-plus me-2"></i>Registrarse
          </button>
          <button class="btn-secondary-custom w-100" type="button" @click="gimnasioSeleccionado = null">
            <i class="fas fa-arrow-left me-2"></i>Cambiar gimnasio
          </button>
        </form>
      </div>
    </div>
    <AlertMessage />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const { mostrarMensaje } = useAlertMessage();

const gimnasios = ref([])
const gimnasioSeleccionado = ref(null)
const usuario = ref({
    nombre: '',
    apellidos: '',
    tlf: '',
    email: '',
    clave: ''
})
const cargando = ref(false)
const pagarCuota = ref(false)

const seleccionarGimnasio = (gim) => {
    gimnasioSeleccionado.value = gim
    pagarCuota.value = false
}

const registrarUsuario = async () => {
    cargando.value = true;
    try {
        // Convertir el email a minúsculas antes de enviarlo
        const usuarioData = {
            ...usuario.value,
            email: usuario.value.email.toLowerCase()
        };
        
        const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${gimnasioSeleccionado.value.id}/nuevousuario`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(usuarioData)
        });
        
        if (res.ok) {
            mostrarMensaje('Usuario creado correctamente', 'success');
            
            // Simular pago de cuota si está seleccionado
            if (pagarCuota.value) {
                await new Promise(resolve => setTimeout(resolve, 800));
                mostrarMensaje('Usuario creado correctamente. Cuota pagada (simulado). Redirigiendo al login...', 'success');
            } else {
                // Esperar un momento y mostrar mensaje de redirección
                await new Promise(resolve => setTimeout(resolve, 800));
                mostrarMensaje('Usuario creado correctamente. Redirigiendo al login...', 'success');
            }
            
            // Redirección tras el registro exitoso
            setTimeout(() => {
                router.push('/login');
            }, 2000);
        } else {
            const errorText = await res.text();
            mostrarMensaje(errorText, 'danger');
        }
    } catch (e) {
        mostrarMensaje('Error de conexión al registrar usuario', 'danger');
    } finally {
        cargando.value = false;
    }
}

onMounted(async () => {
    try {
        const token = localStorage.getItem('token');
        const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        const data = await res.json();
        gimnasios.value = Array.isArray(data) ? data : [];
    } catch (e) {
        mostrarMensaje('No se pudieron cargar los gimnasios', 'danger');
    }
});
</script>

<style scoped>
/* Contenedor principal con mismo fondo que Landing */
.registro-container {
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
  background-image: url('../assets/fondo2.jpg');
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

@keyframes shimmer {
  0%, 100% { filter: hue-rotate(0deg); }
  50% { filter: hue-rotate(15deg); }
}

.registro-title {
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

/* Gimnasios Grid */
.gimnasios-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin: 20px auto 32px auto;
  max-width: 1200px;
  animation: fadeInUp 1s ease-out 0.6s both;
}

.gimnasio-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(45, 35, 46, 0.1);
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 32px rgba(45, 35, 46, 0.1);
  position: relative;
  overflow: hidden;
}

.gimnasio-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(71, 68, 72, 0.1), transparent);
  transition: left 0.5s ease;
}

.gimnasio-card:hover::before {
  left: 100%;
}

.gimnasio-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(45, 35, 46, 0.2);
  border-color: rgba(71, 68, 72, 0.3);
}

.gimnasio-content {
  position: relative;
  z-index: 2;
}

.gimnasio-nombre {
  color: #2D232E;
  font-weight: 700;
  font-size: 1.5rem;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
}

.gimnasio-info {
  color: #474448;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  font-weight: 500;
}

/* Form Container */
.form-container {
  max-width: 500px;
  width: 100%;
  animation: fadeInUp 1s ease-out 0.6s both;
  z-index: 15;
  position: relative;
}

.registro-form {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(45, 35, 46, 0.1);
  border-radius: 20px;
  padding: 32px;
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
  padding: 0.85em 2em;
}

.btn-primary-custom:hover {
  background: linear-gradient(135deg, #534B52 0%, #474448 100%);
  border-color: #534B52;
  color: #F1F0EA;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(45, 35, 46, 0.3);
}

.btn-primary-custom:disabled {
  background: #ccc;
  border-color: #ccc;
  color: #666;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-secondary-custom {
  background: rgba(241, 240, 234, 0.9);
  border: 2px solid rgba(45, 35, 46, 0.2);
  color: #474448;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.3s ease;
  padding: 0.75em 2em;
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-secondary-custom:hover {
  background: rgba(224, 221, 207, 0.9);
  border-color: #474448;
  color: #2D232E;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(45, 35, 46, 0.2);
}

/* Custom checkbox with enhanced styling */
.custom-checkbox-container {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  padding: 16px;
  border: 2px solid rgba(45, 35, 46, 0.1);
  transition: all 0.3s ease;
}

.custom-checkbox-container:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(71, 68, 72, 0.2);
}

.custom-checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
  font-weight: 500;
  color: #2D232E;
}

.custom-checkbox-input {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}

.custom-checkbox-box {
  width: 24px;
  height: 24px;
  border: 2px solid #4caf50;
  border-radius: 8px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  margin-right: 12px;
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.2);
}

.custom-checkbox-input:focus + .custom-checkbox-box {
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.3);
}

.custom-checkbox-input:checked + .custom-checkbox-box {
  background: #4caf50;
  border-color: #4caf50;
  transform: scale(1.1);
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
  
  .registro-title {
    font-size: 1.5rem;
  }
  
  .gimnasios-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    margin: 16px auto 24px auto;
    padding: 0 16px;
  }
  
  .gimnasio-card {
    padding: 20px;
  }
  
  .gimnasio-nombre {
    font-size: 1.3rem;
  }
  
  .registro-form {
    padding: 24px 20px;
    margin: 0 16px;
  }
  
  .form-control {
    border-radius: 10px;
  }
  
  .btn-primary-custom,
  .btn-secondary-custom {
    border-radius: 10px;
  }
  
  .custom-checkbox-container {
    padding: 12px;
  }
}

@media (max-width: 480px) {
  .registro-title {
    font-size: 1.3rem;
  }
  
  .gimnasio-card {
    padding: 16px;
  }
  
  .registro-form {
    padding: 20px 16px;
  }
}
</style>