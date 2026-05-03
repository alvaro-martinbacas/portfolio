<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import QRCode from 'qrcode.vue'

const router = useRouter()
const usuarioEnGimnasio = ref(false)
const gimnasioId = ref(null)
const qrValue = ref('')

function generateRandomQR() {
  const timestamp = Date.now()
  const random = Math.random().toString(36).substring(2, 15)
  qrValue.value = `GYM-${gimnasioId.value}-${timestamp}-${random}`
}

async function cargarDatosUsuario() {
  try {
    const email = localStorage.getItem('email')
    const token = localStorage.getItem('token')
    const authHeader = 'Bearer ' + token

    // Primero obtener el gimnasio del usuario
    const responseGimnasio = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/gimnasio`, {
      headers: {
        'Authorization': authHeader,
        'Content-Type': 'application/json'
      }
    })

    if (!responseGimnasio.ok) {
      throw new Error('Error al obtener el gimnasio del usuario')
    }

    const gimnasio = await responseGimnasio.json()
    gimnasioId.value = gimnasio.id
    localStorage.setItem('gimnasioId', gimnasio.id)
    generateRandomQR()

    // Obtener el último acceso del usuario para saber si está dentro o fuera
    const responseAccesos = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/accesos/ultimo`, {
      headers: {
        'Authorization': authHeader,
        'Content-Type': 'application/json'
      }
    })

    if (responseAccesos.ok) {
      const ultimoAcceso = await responseAccesos.json()
      console.log('Último acceso:', ultimoAcceso)
      
      // Si tiene horaEntrada pero no horaSalida, está dentro
      // Si tiene ambas o ninguna, está fuera
      const estaEnGimnasio = ultimoAcceso?.horaEntrada && !ultimoAcceso?.horaSalida
      usuarioEnGimnasio.value = estaEnGimnasio
      
      console.log(`Estado del usuario: ${estaEnGimnasio ? 'DENTRO' : 'FUERA'} del gimnasio`)
    } else {
      usuarioEnGimnasio.value = false
      console.log('No se pudo obtener el último acceso, asumiendo que está fuera')
    }
  } catch (error) {
    console.error('Error al cargar datos:', error)
    router.push('/home')
  }
}

async function registrarAcceso() {
  try {
    const token = localStorage.getItem('token')
    const authHeader = 'Bearer ' + token
    const endpoint = usuarioEnGimnasio.value ? 'salida' : 'entrada'
    
    console.log(`Registrando ${endpoint} en gimnasio ${gimnasioId.value}`)
    
    const response = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${gimnasioId.value}/accesos/${endpoint}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': authHeader
      }
    })

    console.log(`Respuesta del servidor: ${response.status}`)

    if (!response.ok) {
      const errorData = await response.text()
      console.error('Error del servidor:', errorData)
      throw new Error(errorData || 'Error al registrar ' + endpoint)
    }

    // Actualizar el estado inmediatamente después del registro exitoso
    if (endpoint === 'entrada') {
      usuarioEnGimnasio.value = true
    } else {
      usuarioEnGimnasio.value = false
    }

    console.log(`${endpoint} registrada correctamente. Estado actualizado: usuarioEnGimnasio = ${usuarioEnGimnasio.value}`)
    
    // También recargar los datos para estar seguros
    await cargarDatosUsuario()
    
  } catch (error) {
    console.error('Error al registrar acceso:', error)
    alert(error.message || 'Error al registrar el acceso. Por favor, inténtalo de nuevo.')
  }
}

onMounted(async () => {
  await cargarDatosUsuario()
})
</script>

<template>
  <div class="background-container">
    <!-- Imagen de fondo -->
    <div class="background-image bg-fondo2"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="qr-container position-relative" style="z-index: 10;">
      <div class="qr-card">
        <h2 class="text-center mb-4">{{ usuarioEnGimnasio ? 'Registrar Salida' : 'Registrar Entrada' }}</h2>
      
      <!-- QR generado -->
      <div class="qr-code" @click="generateRandomQR">
        <QRCode 
          :value="qrValue"
          :size="280"
          level="H"
          render-as="svg"
          class="qr-image"
        />
        <p class="mt-3">Toca el QR para generar uno nuevo</p>
      </div>

      <!-- Botón de acción -->
      <button 
        @click="registrarAcceso"
        :class="['action-button', usuarioEnGimnasio ? 'exit-button' : 'enter-button']">
        <i :class="['fas', usuarioEnGimnasio ? 'fa-sign-out-alt' : 'fa-sign-in-alt']"></i>
        {{ usuarioEnGimnasio ? 'Registrar Salida' : 'Registrar Entrada' }}
      </button>
    </div>
  </div>
  </div>
</template>

<style>
/* Estilos específicos de UserEntradaSalida.vue */
.qr-container {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}
.qr-image {
  width: 100%;
  height: auto;
}
.qr-code p {
  color: #6c757d;
  font-size: 0.9rem;
  margin-top: 1rem;
}
.action-button {
  padding: 1rem 2rem;
  font-size: 1.1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-weight: 500;
}
.enter-button {
  background: #474448;
  color: white;
}
.enter-button:hover {
  background: #2D232E;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(45, 35, 46, 0.2);
}
.exit-button {
  background: #E0DDCF;
  color: #2D232E;
}
.exit-button:hover {
  background: #d1cec0;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(45, 35, 46, 0.1);
}
h2 {
  color: #2D232E;
  font-weight: 600;
}
@media (max-width: 480px) {
  .qr-container {
    padding: 0.5rem;
  }
  .qr-card {
    padding: 1rem;
  }
  .qr-code {
    width: 100%;
    padding: 1rem;
    margin: 1rem auto;
  }
}
</style>
