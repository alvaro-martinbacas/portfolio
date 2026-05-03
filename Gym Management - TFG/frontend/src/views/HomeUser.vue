<template>
  <div class="background-container">
    <!-- Imagen de fondo -->
    <div class="background-image bg-fondo3"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="container py-4 position-relative" style="z-index: 10;">
      <header class="mb-5 text-center">
        <h2 class="welcome-text mb-2">¡Bienvenido de nuevo, {{ nombreUsuario }}!</h2>
        <div class="saludo-box mx-auto mt-2">
          <span class="saludo-text">{{ obtenerSaludo() }}</span>
        </div>
      </header>

      <div class="row g-4">
        <!-- Panel de Estado -->
        <div class="col-md-4">
          <div class="card h-100 shadow-sm border-0 dashboard-card">
            <div class="card-body">
              <h5 class="card-title mb-4">Estado Actual</h5>
              <div class="row g-2">
                <div class="col-12 mb-2">
                  <div class="status-card bg-warning bg-opacity-10 border-0 d-flex align-items-center p-3 rounded shadow-sm">
                    <div class="icon-badge bg-warning text-white me-3 fs-4 d-flex align-items-center justify-content-center">
                      {{ medallas.length }}
                    </div>
                    <div class="fw-semibold fs-6" style="color: #111">Medallas obtenidas</div>
                  </div>
                </div>
                <div class="col-12 mb-2">
                  <div class="status-card bg-success bg-opacity-10 border-0 d-flex align-items-center p-3 rounded shadow-sm">
                    <div class="icon-badge bg-success text-white me-3 fs-4 d-flex align-items-center justify-content-center">
                      {{ clasesReservadas.length }}
                    </div>
                    <div class="fw-semibold fs-6" style="color: #111">Clases reservadas</div>
                  </div>
                </div>
                <div class="col-12 mb-2">
                  <div class="status-card bg-primary bg-opacity-10 border-0 d-flex align-items-center p-3 rounded shadow-sm">
                    <div class="icon-badge bg-primary text-white me-3 fs-4 d-flex align-items-center justify-content-center">
                      {{ rutinasActivas.length }}
                    </div>
                    <div class="fw-semibold fs-6" style="color: #111">Rutinas activas</div>
                  </div>
                </div>
                <div class="col-12">
                  <div class="status-card bg-dark bg-opacity-10 border-0 d-flex align-items-center p-3 rounded shadow-sm">
                    <div class="icon-badge bg-dark text-white me-3 fs-4 d-flex align-items-center justify-content-center">
                      {{ numAccesos }}
                    </div>
                    <div class="fw-semibold fs-6" style="color: #111">Veces asistidas</div>
                  </div>
                </div>
              </div>
              <!-- Progreso mensual eliminado -->
            </div>
          </div>
        </div>

        <!-- Panel de Calendario Mensual -->
        <div class="col-md-4">
          <div class="card h-100 shadow-sm border-0 dashboard-card">
            <div class="card-body">
              <h5 class="card-title mb-3 d-flex justify-content-between align-items-center">
                <span>Actividad - {{ mesActualTexto }}</span>
              </h5>
              <div class="calendario-container">
                <Calendario
                  :accesos="accesos"
                  :current-month="currentMonth"
                  :solo-mes-actual="true"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- Panel de Próximas Actividades -->
        <div class="col-md-4">
          <div class="card h-100 shadow-sm border-0 dashboard-card">
            <div class="card-body">
              <h5 class="card-title mb-4">Próximas Actividades</h5>
              <div class="activities-list">
                <div v-if="actividadesProximas.length > 0">
                  <div v-for="solicitud in actividadesProximas" :key="solicitud.id" class="activity-item mb-3 p-3 border rounded">
                    <div class="d-flex justify-content-between align-items-center">
                      <div>
                        <h6 class="mb-1" style="color: #111">{{ solicitud.clase?.nombre || 'Clase sin nombre' }}</h6>
                        <small class="text-muted">
                          <i class="fas fa-calendar-day me-1"></i>{{ formatearFecha(solicitud.fechaSolicitud + 'T' + solicitud.clase?.horaIni) }}
                        </small><br>
                        <small class="text-muted">
                          <i class="fas fa-clock me-1"></i>{{ formatearHora(solicitud.clase?.horaIni) }} - {{ formatearHora(solicitud.clase?.horaFin) }}
                        </small>
                      </div>
                      <div>
                        <button @click="cancelarReserva(solicitud.id)" class="btn btn-sm btn-outline-danger">
                          <i class="fas fa-trash-alt"></i>
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else class="text-center text-muted py-3">
                  <i class="fas fa-calendar-day mb-2 fs-4 d-block"></i>
                  <p class="mb-0">No hay actividades programadas</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { formatearHora } from '../utils/dateUtils';
import Calendario from '../components/Calendario.vue';

export default {
  name: 'HomeUsuario',
  components: {
    Calendario
  },
  data() {
      return {
      nombreUsuario: '',
      usuarioEnGimnasio: false,
      medallas: [],
      clasesReservadas: [],
      rutinasActivas: [],
      numAccesos: 0,
      progresoMensual: 75,
      proximasClases: [],
      // Datos para el calendario
      accesos: [],
      currentMonth: new Date(),
      accesosMesActual: 0
    }
  },
  computed: {
    actividadesProximas() {
      return this.clasesReservadas
        .sort((a, b) => {
          const fechaA = new Date(`${a.fechaSolicitud}T${a.clase.horaIni}`);
          const fechaB = new Date(`${b.fechaSolicitud}T${b.clase.horaIni}`);
          return fechaA - fechaB;
        });
    },
    mesActualTexto() {
      return this.currentMonth.toLocaleDateString('es-ES', { 
        month: 'long', 
        year: 'numeric' 
      });
    }
  },
  methods: {
    obtenerSaludo() {
      const hora = new Date().getHours()
      if (hora < 12) return '¡Buenos días! ¿Te apetece entrenar hoy?'
      if (hora < 20) return '¡Buenas tardes! Date una vuelta por el gimnasio'
      return '¡Buenas noches! Descansar también es parte de la rutina'
    },
    async registrarEntrada() {
      try {
        const idGimnasio = localStorage.getItem('gimnasioId');
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/accesos/entrada`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': authHeader
          }
        });
        this.usuarioEnGimnasio = true;
      } catch (error) {
        console.error('Error al registrar entrada:', error);
      }
    },
    async registrarSalida() {
      try {
        const idGimnasio = localStorage.getItem('gimnasioId');
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/accesos/salida`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': authHeader
          }
        });
        this.usuarioEnGimnasio = false;
      } catch (error) {
        console.error('Error al registrar salida:', error);
      }
    },
    async cargarDatosUsuario() {
      try {
        const email = localStorage.getItem('email');
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;

        // Datos básicos usuario
        const response = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}`, {
          headers: {
            'Authorization': authHeader,
            'Content-Type': 'application/json'
          }
        });
        //console.log('[USUARIO] status:', response.status);
        if (!response.ok) {
          const errorText = await response.text();
          console.error('[USUARIO] Error al cargar datos:', response.status, errorText);
          throw new Error('Error al cargar los datos del usuario: ' + response.status + ' ' + errorText);
        }
        const datos = await response.json();
        this.nombreUsuario = `${datos.nombre} ${datos.apellidos}`;

        // Medallas
        const medallasRes = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/medallas`, {
          headers: {
            'Authorization': authHeader,
            'Content-Type': 'application/json'
          },
          credentials: 'include'
        });
        //console.log('[MEDALLAS] status:', medallasRes.status);
        if (!medallasRes.ok) {
          const errorText = await medallasRes.text();
          console.error('[MEDALLAS] Error:', medallasRes.status, errorText);
        }
        this.medallas = medallasRes.ok ? await medallasRes.json() : [];

        // Clases reservadas
        const clasesRes = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/proximas-clases`, {
          headers: { 'Authorization': authHeader }
        });
        //console.log('[CLASES] status:', clasesRes.status);
        if (clasesRes.ok) {
          const clases = await clasesRes.json();
          //console.log('[CLASES] próximas recibidas:', clases);
          this.clasesReservadas = clases.map(clase => ({
            id: clase.solicitudId,
            clase: {
              nombre: clase.nombreClase,
              horaIni: clase.horaInicio,
              horaFin: clase.horaFin
            },
            fechaSolicitud: clase.fecha
          }));
          //console.log('[CLASES] reservadas guardadas:', this.clasesReservadas);
        } else {
          const errorText = await clasesRes.text();
          console.error('[CLASES] Error al obtener próximas:', clasesRes.status, errorText);
          this.clasesReservadas = [];
        }

        // Rutinas activas
        const rutinasRes = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/rutinas`, {
          headers: { 'Authorization': authHeader }
        });
        //console.log('[RUTINAS] status:', rutinasRes.status);
        if (!rutinasRes.ok) {
          const errorText = await rutinasRes.text();
          console.error('[RUTINAS] Error:', rutinasRes.status, errorText);
        }
        this.rutinasActivas = rutinasRes.ok ? await rutinasRes.json() : [];

        // Número de accesos
        const accesosRes = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/num-accesos`, {
          headers: { 'Authorization': authHeader }
        });
        //console.log('[ACCESOS] status:', accesosRes.status);
        if (!accesosRes.ok) {
          const errorText = await accesosRes.text();
          console.error('[ACCESOS] Error:', accesosRes.status, errorText);
        }
        this.numAccesos = accesosRes.ok ? Math.floor((await accesosRes.json())) : 0;

      } catch (error) {
        console.error('Error al cargar datos del usuario:', error);
        this.nombreUsuario = 'Usuario';
      }
    },
    async cargarAccesosMesActual() {
      try {
        const email = localStorage.getItem('email');
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        
        // Configurar fechas del mes actual
        const ahora = new Date();
        const inicioMes = new Date(ahora.getFullYear(), ahora.getMonth(), 1);
        const finMes = new Date(ahora.getFullYear(), ahora.getMonth() + 1, 0);
        
        this.currentMonth = inicioMes;
        
        // Cargar accesos sin filtro de fechas para que el calendario funcione correctamente
        const url = `http://localhost:8080/gestiongimnasios/usuarios/${email}/accesos`;
        const response = await fetch(url, {
          headers: { 'Authorization': authHeader },
          credentials: 'include'
        });
        
        if (response.ok) {
          const data = await response.json();
          this.accesos = data.map(acceso => ({
            id: acceso.id,
            fecha: acceso.horaEntrada,
            horaEntrada: acceso.horaEntrada,
            horaSalida: acceso.horaSalida,
            nombreGimnasio: acceso.nombreGimnasio
          }));
          
          // Contar accesos únicos por día en el mes actual
          const diasConAcceso = new Set();
          this.accesos.forEach(acceso => {
            const fechaAcceso = new Date(acceso.fecha);
            if (fechaAcceso >= inicioMes && fechaAcceso <= finMes) {
              diasConAcceso.add(fechaAcceso.toDateString());
            }
          });
          this.accesosMesActual = diasConAcceso.size;
        }
      } catch (error) {
        console.error('Error al cargar accesos del mes:', error);
        this.accesos = [];
        this.accesosMesActual = 0;
      }
    },
    formatearFecha(fecha) {
      return new Date(fecha).toLocaleString('es-ES', {
        weekday: 'long',
        day: 'numeric',
        month: 'long'
      })
    },
    formatearHora,
    async cancelarReserva(id) {
      try {
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/solicitudes/${id}`, {
          method: 'DELETE',
          headers: {
            'Authorization': authHeader,
            'Content-Type': 'application/json'
          }
        });
        if (response.ok) {
          // Actualizar la lista de clases reservadas
          this.clasesReservadas = this.clasesReservadas.filter(clase => clase.id !== id);
        } else {
          throw new Error('Error al cancelar la reserva');
        }
      } catch (error) {
        console.error('Error al cancelar la reserva:', error);
      }
    }
  },
  async mounted() {
    await this.cargarDatosUsuario();
    await this.cargarAccesosMesActual();
    // Aquí irían las demás llamadas a la API para obtener los datos del usuario
  }
}
</script>

<style scoped>
.welcome-text {
  font-size: clamp(1.5rem, 4vw, 2.5rem);
  font-weight: 700;
  color: #2D232E;
  margin-bottom: 0.5rem;
  line-height: 1.2;
  text-align: center;
}

.saludo-text {
  color: #534B52;
  font-size: 1.1rem;
  font-weight: 500;
  text-align: center;
}


.dashboard-card {
  transition: all 0.3s ease;
  border-radius: 12px;
  background: #fff;
  border: none;
  box-shadow: 0 2px 8px rgba(45, 35, 46, 0.1);
}

.dashboard-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(45, 35, 46, 0.15);
}

.dashboard-card:focus-within {
  outline: 2px solid #474448;
  outline-offset: 2px;
}

.card-title {
  color: #2D232E;
  font-weight: 600;
  font-size: 1.25rem;
  margin-bottom: 1.5rem;
}


.status-card {
  min-height: 64px;
  box-shadow: 0 2px 8px rgba(45, 35, 46, 0.07);
  border: none;
}

.icon-badge {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 1.7rem;
  box-shadow: 0 2px 8px rgba(45, 35, 46, 0.10);
}

.activity-item {
  background-color: #F1F0EA;
  transition: all 0.3s ease;
  border: 1px solid #E0DDCF;
  border-radius: 8px;
}

.activity-item:hover {
  background-color: #E0DDCF;
  border-color: #534B52;
}

/* Botones estilizados */
.btn {
  padding: 0.75rem 1.5rem;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  justify-content: center;
}

.btn i {
  font-size: 1.1rem;
}

.btn-success {
  background-color: #474448;
  border-color: #474448;
  color: #F1F0EA;
}

.btn-success:hover {
  background-color: #534B52;
  border-color: #534B52;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(45, 35, 46, 0.2);
}

.btn-danger {
  background-color: #2D232E;
  border-color: #2D232E;
  color: #F1F0EA;
}

.btn-danger:hover {
  background-color: #111;
  border-color: #111;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(45, 35, 46, 0.2);
}

/* Botones de acceso rápido */
.btn-outline-primary,
.btn-outline-success,
.btn-outline-warning,
.btn-outline-info,
.btn-outline-danger {
  border-width: 2px;
  font-weight: 500;
  padding: 1rem;
  transition: all 0.3s ease;
  color: #2D232E;
  border-color: #534B52;
  background-color: #F1F0EA;
}

.btn-outline-primary:hover,
.btn-outline-success:hover,
.btn-outline-warning:hover,
.btn-outline-info:hover {
  background-color: #474448;
  border-color: #474448;
  color: #F1F0EA;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(45, 35, 46, 0.2);
}

.btn-outline-danger:hover {
  background-color: #2D232E;
  border-color: #2D232E;
  color: #F1F0EA;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(45, 35, 46, 0.2);
}

/* Barra de progreso */
.progress {
  border-radius: 10px;
  height: 10px;
  background-color: #E0DDCF;
  overflow: hidden;
  border: 1px solid rgba(45, 35, 46, 0.1);
}

.progress-bar {
  background-color: #474448;
  transition: width 0.6s ease;
}

/* Mejoras de accesibilidad */
.text-muted {
  color: #534B52 !important;
}

/* Estilos para el calendario en el dashboard */
.calendario-container {
  max-height: 320px;
  overflow: hidden;
}

.calendario-container :deep(.contenedor-calendario) {
  padding: 0;
  overflow: visible;
  white-space: normal;
}

.calendario-container :deep(.envoltorio-calendario) {
  display: block;
  overflow: visible;
  white-space: normal;
}

.calendario-container :deep(.mes-calendario) {
  margin: 0;
  width: 100%;
  display: block;
  white-space: normal;
}

.calendario-container :deep(.titulo-mes) {
  display: none; /* Ocultamos el título porque ya lo tenemos en el card */
}

.calendario-container :deep(.grilla-calendario) {
  gap: 0.15rem;
  font-size: 0.75rem;
  grid-template-columns: repeat(7, 1fr);
  display: grid;
  grid-auto-rows: minmax(28px, auto);
}

.calendario-container :deep(.encabezado-dia) {
  font-size: 0.7rem;
  padding: 0.3rem;
  font-weight: 600;
  text-align: center;
  background-color: #f8f9fa;
  border-radius: 4px;
  color: #6c757d;
}

.calendario-container :deep(.dia-calendario) {
  min-height: 28px;
  font-size: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  background: #f8f9fa;
  color: #495057;
  aspect-ratio: 1;
}

.calendario-container :deep(.tiene-acceso) {
  background-color: #42b983 !important;
  color: white !important;
  font-weight: 500;
  box-shadow: 0 1px 2px rgba(66, 185, 131, 0.2);
}

.calendario-container :deep(.hoy) {
  border: 2px solid #42b983 !important;
  font-weight: bold;
}

.calendario-container :deep(.fuera-mes) {
  background-color: #e9ecef !important;
  color: #adb5bd !important;
}

.calendario-container :deep(.fuera-mes span) {
  display: none;
}

.badge {
  font-size: 0.85rem;
  padding: 0.4em 0.6em;
}

</style>
/* No custom styles needed; all styles are now global or handled by global.css */