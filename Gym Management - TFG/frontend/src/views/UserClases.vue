<template>
  <div class="background-container">
    <!-- Imagen de fondo -->
    <div class="background-image bg-fondo4"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="container py-4 position-relative" style="z-index: 10;">
    <AlertMessage />
    
    <!-- Modal para detalles -->
    <div v-if="modalVisible" class="modal-overlay" @click="cerrarModal">
      <div class="modal-custom" @click.stop>
        <div class="modal-header">
          <h5 class="modal-title" style="color: #111">Detalles de la Clase</h5>
          <!-- Cruz de cerrar eliminada -->
        </div>
        <div class="modal-body">
          <div v-if="claseSeleccionada" class="p-3">
              <div class="mb-3">
                <i class="fas fa-dumbbell me-2"></i>
                <strong>Clase:</strong> {{ claseSeleccionada.nombreTipoClase }}
              </div>
              <div class="mb-3" v-if="descripcionTipoClase">
                <i class="fas fa-align-left me-2"></i>
                <strong>Descripción:</strong> {{ descripcionTipoClase }}
              </div>
              <div class="mb-3">
                <i class="fas fa-calendar me-2"></i>
                <strong>Día:</strong> {{ formatearDiaSemana(claseSeleccionada.diaSemana) }}
              </div>
              <div class="mb-3">
                <i class="fas fa-clock me-2"></i>
                <strong>Horario:</strong> {{ formatearHora(claseSeleccionada.horaIni) }} - {{ formatearHora(claseSeleccionada.horaFin) }}
              </div>
              <div class="mb-3">
                <i class="fas fa-users me-2"></i>
                <strong>Capacidad:</strong> {{ claseSeleccionada.plazasOcupadas }}/{{ claseSeleccionada.maxPlazas }}
              </div>
              <div class="mb-3">
                <i class="fas fa-user-tie me-2"></i>
                <strong>Entrenador:</strong> {{ claseSeleccionada.emailEntrenador || 'Sin asignar' }}
              </div>
              <div class="mt-4 d-grid gap-2">
                <button 
                  class="btn btn-primary" 
                  @click="reservarClase(claseSeleccionada.id)"
                  :disabled="!puedeReservar(claseSeleccionada)">
                  <i class="fas fa-check-circle me-2"></i>
                  {{ puedeReservar(claseSeleccionada) ? 'Solicitar Plaza' : 'No hay plazas disponibles' }}
                </button>
                <button class="btn btn-secondary" @click="cerrarModal">Cerrar</button>
              </div>
            </div>
            </div>
      </div>
    </div>

    <div class="row">
      <div class="col-12">
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-body p-2">
            <div class="d-flex justify-content-between align-items-center mb-2 calendario-toggle" @click="toggleCalendario" style="cursor: pointer;">
              <h5 class="card-title m-0 px-1">
                <i class="fas fa-calendar-alt me-2"></i>Horario
              </h5>
              <div class="d-flex align-items-center">
                <small class="text-muted me-2">{{ calendarioVisible ? 'Ocultar calendario' : 'Click para ver calendario' }}</small>
                <i :class="['fas', calendarioVisible ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
              </div>
            </div>

            <!-- Vista móvil -->
            <div v-if="isMobile && calendarioVisible" class="mobile-calendar mt-3">
              <div v-for="dia in diasSemana" :key="dia" class="mobile-day-section mb-3">
                <div class="mobile-day-header" @click="toggleDia(dia)">
                  <h6 class="m-0" style="color: #111">{{ dia }}</h6>
                  <i :class="['fas', diaExpandido === dia ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
                </div>
                <div v-if="diaExpandido === dia" class="mobile-day-classes">
                  <div v-for="hora in horasCalendario" :key="hora" class="mobile-time-slot">
                    <div v-if="clasesEnCelda(dia, hora).length > 0" class="time-slot-content">
                      <div class="time-label">{{ hora }}</div>
                      <div v-for="clase in clasesEnCelda(dia, hora)" 
                           :key="clase.id" 
                           class="mobile-class-item"
                           :style="obtenerEstiloClase(clase.nombreTipoClase)"
                           @click="mostrarDetalles(clase)">
                        {{ clase.nombreTipoClase }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Vista desktop -->
            <div v-if="!isMobile && calendarioVisible" class="horario-container">
              <div class="horario-grid">
                <!-- Cabecera con días de la semana -->
                <div class="horario-header">
                  <div class="hora-column-header">Hora</div>
                  <div v-for="(dia, idx) in diasSemana" :key="dia" class="dia-header" :class="{ 'last-dia': idx === diasSemana.length - 1 }">
                    <div class="dia-nombre">{{ dia }}</div>
                    <div class="dia-abrev">{{ dia.substring(0, 1) }}</div>
                  </div>
                </div>
                
                <!-- Filas de horarios -->
                <div class="horario-body">
                  <template v-for="hora in horasCalendario" :key="hora">
                    <div class="hora-label">{{ hora }}</div>
                    <div 
                      v-for="dia in diasSemana" 
                      :key="`${dia}-${hora}`" 
                      class="celda-horario"
                    >
                      <div 
                        v-for="clase in clasesEnCelda(dia, hora)" 
                        :key="clase.id"
                        class="clase-card"
                        :style="obtenerEstiloClase(clase.nombreTipoClase)"
                        @click="mostrarDetalles(clase)"
                      >
                        <div class="clase-tipo">{{ clase.nombreTipoClase }}</div>
                        
                      </div>
                    </div>
                  </template>
                </div>
              </div>
            </div>
            <div v-if="calendarioVisible" class="text-muted mt-2" style="font-size:0.85em;">
              Haz clic en una clase para ver detalles y solicitar plaza.
            </div>
          </div>
        </div>

      

        <!-- Solicitudes de Clase -->
        <div class="mt-5">
          <h3 class="mb-4">Mis Solicitudes</h3>
          <div v-if="solicitudesUsuario.length === 0" class="card shadow-sm border-0 mb-4">
            <div class="card-body text-center py-4">
              <i class="fas fa-calendar-check fs-1 text-muted mb-3"></i>
              <h5>No tienes solicitudes</h5>
              <p class="text-muted">Solicita una clase para comenzar.</p>
            </div>
          </div>
          <div v-else class="table-responsive">
            <table class="table table-bordered align-middle">
              <thead>
                <tr>
                  <th>Clase</th>
                  <th>Horario</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="solicitud in solicitudesUsuario" :key="solicitud.id">
                  <td>
                    <div class="d-flex align-items-center">
                      <span class="fw-medium text-dark">
                        {{ clasesSolicitudes[solicitud.id]?.nombreTipoClase || 'Cargando...' }}
                      </span>
                    </div>
                  </td>
                  <td>
                    <span v-if="clasesSolicitudes[solicitud.id]">
                      {{ formatearDiaSemana(clasesSolicitudes[solicitud.id].diaSemana) }}
                      {{ formatearHora(clasesSolicitudes[solicitud.id].horaIni) }} - {{ formatearHora(clasesSolicitudes[solicitud.id].horaFin) }}
                    </span>
                    <span v-else>Cargando...</span>
                  </td>
                  <td>
                    <span :class="solicitud.confReserva ? 'text-success' : 'text-warning'">
                      {{ solicitud.confReserva ? 'Aceptada' : 'Pendiente' }}
                    </span>
                  </td>
                  <td>
                    <button class="btn btn-danger btn-sm" @click="eliminarSolicitud(solicitud.id)">
                      <i class="fas fa-trash"></i> Eliminar
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script>
import { formatearHora, formatearDiaSemana } from '../utils/dateUtils';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';

export default {
  name: 'UserClases',
  components: {
    AlertMessage
  },
  setup() {
    const { mostrarMensaje } = useAlertMessage();
    return { mostrarMensaje };
  },
  data() {
    return {
      clases: [],
      clasesAprobadas: [],
      solicitudesUsuario: [],
      clasesSolicitudes: {}, // Mapea idSolicitud -> datos de clase
      tiposClase: [],
      filtroTipo: '',
      filtroFecha: '',
      filtroEstado: '',
      idGimnasio: null,
      claseSeleccionada: null,
      modalVisible: false,
      descripcionTipoClase: '',
      calendarioVisible: false,
      diaExpandido: null,
      isMobile: window.innerWidth <= 768,
      diasSemana: ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'],
      horasCalendario: [], // Se rellenará dinámicamente según el horario del gimnasio
    }
  },
  computed: {
    clasesEnCelda() {
      return (dia, hora) => {
        return this.clases.filter(clase => {
          const [horaClase] = clase.horaIni.split(':');
          const diaIndex = this.diasSemana.indexOf(dia);
          const diasEnum = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
          return clase.diaSemana === diasEnum[diaIndex] && horaClase === hora.split(':')[0];
        });
      };
    },
    clasesFiltradas() {
      return this.clases.filter(clase => {
        const cumpleFiltroTipo = !this.filtroTipo || clase.nombreTipoClase === this.filtroTipo;
        const cumpleFiltroEstado = !this.filtroEstado || clase.estado === this.filtroEstado;
        return cumpleFiltroTipo && cumpleFiltroEstado;
      });
    }
  },
  methods: {
    obtenerColorClase(tipoClase) {
      // Generar un hash del nombre para obtener un color consistente
      const hash = this.generarHashString(tipoClase);
      return `clase-dinamica-${hash}`;
    },
    
    generarHashString(str) {
      let hash = 0;
      for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convertir a 32bit
      }
      return Math.abs(hash) % 20; // Limitar a 20 colores diferentes
    },
    
    obtenerEstiloClase(tipoClase) {
      const coloresPastel = [
        { bg: '#FFE5E5', border: '#FF9999', color: '#8B0000' }, // Rosa suave
        { bg: '#E5F3FF', border: '#99D6FF', color: '#003D6B' }, // Azul suave
        { bg: '#E5FFE5', border: '#99FF99', color: '#006600' }, // Verde suave
        { bg: '#FFF0E5', border: '#FFD199', color: '#B8860B' }, // Naranja suave
        { bg: '#F0E5FF', border: '#D199FF', color: '#4B0082' }, // Morado suave
        { bg: '#E5FFF0', border: '#99FFD1', color: '#2E8B57' }, // Menta suave
        { bg: '#FFE5F0', border: '#FF99D1', color: '#C71585' }, // Magenta suave
        { bg: '#F0F0FF', border: '#D1D1FF', color: '#483D8B' }, // Índigo suave
        { bg: '#FFF5E5', border: '#FFE599', color: '#DAA520' }, // Amarillo suave
        { bg: '#E5FFFF', border: '#99FFFF', color: '#008B8B' }, // Cian suave
        { bg: '#F5E5FF', border: '#E599FF', color: '#9932CC' }, // Violeta suave
        { bg: '#E5F5FF', border: '#99E5FF', color: '#1E90FF' }, // Azul cielo suave
        { bg: '#FFE5FA', border: '#FF99E5', color: '#FF1493' }, // Rosa fucsia suave
        { bg: '#F0FFE5', border: '#D1FF99', color: '#32CD32' }, // Lima suave
        { bg: '#FFE5E0', border: '#FF9980', color: '#FF4500' }, // Coral suave
        { bg: '#E5E5FF', border: '#9999FF', color: '#6A5ACD' }, // Pizarra suave
        { bg: '#FFFAE5', border: '#FFF299', color: '#FFD700' }, // Oro suave
        { bg: '#E5FFF5', border: '#99FFD9', color: '#20B2AA' }, // Turquesa suave
        { bg: '#F8E5FF', border: '#F099FF', color: '#BA55D3' }, // Orquídea suave
        { bg: '#E5F8FF', border: '#99F0FF', color: '#00CED1' }  // Turquesa claro suave
      ];
      
      const hash = this.generarHashString(tipoClase);
      const colorData = coloresPastel[hash % coloresPastel.length];
      
      return {
        backgroundColor: colorData.bg,
        borderLeftColor: colorData.border,
        color: colorData.color,
        boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15), 0 1px 3px rgba(0, 0, 0, 0.1)',
        border: `1px solid ${colorData.border}`,
        borderLeft: `4px solid ${colorData.border}`
      };
    },
    getClaseColorStyle(tipoClase) {
      // Colores vivos para cada tipo de clase
      const colores = {
        'Yoga':   { bg: '#FFD600', color: '#222' },
        'Pilates':{ bg: '#00E676', color: '#111' },
        'Spinning':{ bg: '#2979FF', color: '#fff' },
        'Crossfit':{ bg: '#FF1744', color: '#fff' },
        'Zumba':  { bg: '#F500A3', color: '#fff' },
        'Boxeo':  { bg: '#FF9100', color: '#222' },
        'HIIT':   { bg: '#00B8D4', color: '#fff' },
        'Funcional': { bg: '#C51162', color: '#fff' },
        'TRX':    { bg: '#AEEA00', color: '#222' },
        'default':{ bg: '#7C4DFF', color: '#fff' }
      };
      const c = colores[tipoClase] || colores['default'];
      return {
        backgroundColor: c.bg,
        color: c.color,
        border: '1.5px solid #fff',
        fontWeight: 600,
        boxShadow: '0 1px 4px rgba(0,0,0,0.10)',
        cursor: 'pointer',
        transition: 'background 0.2s, color 0.2s',
        marginBottom: '1px',
        borderRadius: '4px',
        fontSize: '0.8rem',
        padding: '2px 4px',
        textShadow: '0 1px 2px rgba(0,0,0,0.07)'
      };
    },

    formatearHora,
    formatearDiaSemana,
    checkMobile() {
      this.isMobile = window.innerWidth <= 768;
    },
    toggleCalendario() {
      this.calendarioVisible = !this.calendarioVisible;
      if (!this.calendarioVisible) {
        this.diaExpandido = null;
      }
    },
    toggleDia(dia) {
      this.diaExpandido = this.diaExpandido === dia ? null : dia;
    },
    puedeReservar(clase) {
      return clase.plazasOcupadas < clase.maxPlazas;
    },
    async reservarClase(id) {
      try {
        if (!this.idGimnasio) {
          this.mostrarMensaje('No se ha encontrado el ID del gimnasio', 'error')
          return
        }
        const email = localStorage.getItem('email')
        if (!email) {
          this.mostrarMensaje('No se ha encontrado el email del usuario', 'error')
          return
        }
        const token = localStorage.getItem('token');
        if (!token) {
          this.mostrarMensaje('No se ha encontrado el token de autenticación', 'error')
          return
        }
        
        console.log('Reservando clase:', { id, email, gimnasioId: this.idGimnasio });
        
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${this.idGimnasio}/clasesColectivas/${id}/nuevasolicitud`, {
          method: 'POST',
          headers: {
            'Content-Type': 'text/plain',
            'Authorization': authHeader
          },
          body: email
        })
        
        console.log('Respuesta del servidor:', response.status, response.statusText);
        
        if (response.ok) {
          this.mostrarMensaje('Solicitud enviada correctamente', 'success')
          await this.cargarClases()
        } else {
          let errorMsg = 'Error al enviar la solicitud';
          const contentType = response.headers.get('content-type');
          if (contentType && contentType.includes('application/json')) {
            try {
              const errorJson = await response.json();
              console.error('Error JSON del servidor:', errorJson);
              errorMsg = errorJson.mensaje || errorJson.error || errorJson.message || JSON.stringify(errorJson);
            } catch (e) {
              console.error('Error al parsear JSON de error:', e);
              errorMsg = 'Error inesperado al procesar la respuesta del servidor';
            }
          } else {
            try {
              errorMsg = await response.text();
              console.error('Error texto del servidor:', errorMsg);
            } catch (e) {
              console.error('Error al obtener texto de error:', e);
              errorMsg = `Error ${response.status}: ${response.statusText}`;
            }
          }
          this.mostrarMensaje(errorMsg || 'Error al enviar la solicitud', 'error')
        }
      } catch (error) {
        console.error('Error al reservar clase:', error)
        this.mostrarMensaje('Error al enviar la solicitud: ' + error.message, 'error')
      }
    },
    async cargarTiposClase() {
      try {
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const responseTipos = await fetch('http://localhost:8080/gestiongimnasios/tiposclases/todos', {
          headers: {
            'Authorization': authHeader
          }
        })
        if (responseTipos.ok) {
          const tipos = await responseTipos.json()
          this.tiposClase = tipos.map(tipo => tipo.nombre)
        }
      } catch (error) {
        console.error('Error al cargar tipos de clase:', error)
      }
    },

    async cargarClases() {
      try {
        const email = localStorage.getItem('email')
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const responseGimnasio = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/gimnasio`, {
          headers: {
            'Authorization': authHeader
          }
        })
        
        if (!responseGimnasio.ok) {
          throw new Error('Error al obtener el gimnasio del usuario')
        }

        const gimnasio = await responseGimnasio.json()
        this.idGimnasio = gimnasio.id

        // Calcular el rango de horas según el horario del gimnasio
        if (gimnasio.horaApertura && gimnasio.horaCierre) {
          const apertura = parseInt(gimnasio.horaApertura.split(":")[0]);
          const cierre = parseInt(gimnasio.horaCierre.split(":")[0]);
          // Si el cierre es menor que la apertura, asumimos que cierra al día siguiente (no habitual en gimnasios)
          let horas = [];
          for (let h = apertura; h <= cierre; h++) {
            horas.push((h < 10 ? '0' : '') + h + ':00');
          }
          this.horasCalendario = horas;
        } else {
          // Fallback por si no hay datos
          this.horasCalendario = Array.from({length: 14}, (_, i) => `${i + 8}:00`);
        }

        const responseClases = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${gimnasio.id}/clasescolectivas`, {
          headers: {
            'Authorization': authHeader
          }
        })
        if (responseClases.ok) {
          const clasesData = await responseClases.json()
          const tiposClasePromises = clasesData.map(clase => 
            fetch(`http://localhost:8080/gestiongimnasios/tiposclases/${clase.nombreTipoClase}`, {
              headers: { 'Authorization': authHeader }
            }).then(res => res.json())
          );
          const tiposClase = await Promise.all(tiposClasePromises);
          const tiposClaseMap = new Map(tiposClase.map(tipo => [tipo.nombre, tipo]));
          this.clases = clasesData.map(clase => {
            const tipoClase = tiposClaseMap.get(clase.nombreTipoClase);
            const [year, month, day] = this.obtenerFechaDiaSemana(clase.diaSemana)
            const [hours, minutes] = clase.horaIni.split(':')
            const fecha = new Date(year, month, day, hours, minutes)
            return {
              ...clase,
              fecha: fecha.toISOString(),
              tipo: clase.nombreTipoClase,
              nombre: clase.nombreTipoClase,
              maxPlazas: tipoClase.maxPlazas,
              estado: clase.plazasOcupadas >= tipoClase.maxPlazas ? 'completa' : 'disponible',
              inscritos: clase.plazasOcupadas,
              instructor: clase.emailEntrenador || 'Sin asignar'
            }
          })
        }

        const responseSolicitudes = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/solicitudes`, {
          headers: {
            'Authorization': authHeader
          }
        })
        if (responseSolicitudes.ok) {
          const solicitudes = await responseSolicitudes.json();
          this.solicitudesUsuario = solicitudes;
          // Por cada solicitud, obtener la clase asociada usando el endpoint y añadir idClase
          this.clasesSolicitudes = {};
          await Promise.all(solicitudes.map(async solicitud => {
            try {
              const respClase = await fetch(`http://localhost:8080/gestiongimnasios/solicitudes/${solicitud.id}/clase`, {
                headers: { 'Authorization': authHeader }
              });
              let clase = null;
              if (respClase.ok) {
                clase = await respClase.json();
                solicitud.idClase = clase.id;
              }
              this.clasesSolicitudes = { ...this.clasesSolicitudes, [solicitud.id]: clase };
            } catch (e) {
              this.clasesSolicitudes = { ...this.clasesSolicitudes, [solicitud.id]: null };
            }
          }));
        }
      } catch (error) {
        console.error('Error al cargar clases:', error)
      }
    },
    obtenerDiaSemana(fecha) {
      return new Date(fecha).toLocaleString('es-ES', {
        weekday: 'long'
      })
    },
    obtenerHorario(fecha) {
      return new Date(fecha).toLocaleString('es-ES', {
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    obtenerFechaDiaSemana(diaSemana) {
      const hoy = new Date()
      const diasSemana = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY']
      const diaObjetivo = diasSemana.indexOf(diaSemana)
      const diaActual = hoy.getDay()
      
      // Calcular días hasta el próximo día de la semana objetivo
      let diasHasta = diaObjetivo - diaActual
      if (diasHasta <= 0) { // Si el día ya pasó esta semana, ir a la próxima
        diasHasta += 7
      }
      
      const fecha = new Date(hoy)
      fecha.setDate(hoy.getDate() + diasHasta)
      
      return [fecha.getFullYear(), fecha.getMonth(), fecha.getDate()]
    },
    compararFechas(fecha1, fecha2) {
      if (!fecha1 || !fecha2) return false
      if (Array.isArray(fecha1)) {
        return fecha1[0] === fecha2[0] && 
               fecha1[1] === fecha2[1] && 
               fecha1[2] === fecha2[2]
      }
      return new Date(fecha1).toDateString() === new Date(fecha2).toDateString()
    },
    async mostrarDetalles(clase) {
      this.claseSeleccionada = clase;
      this.modalVisible = true;
      this.descripcionTipoClase = '';
      if (clase && clase.nombreTipoClase) {
        try {
          const token = localStorage.getItem('token');
          const authHeader = 'Bearer ' + token;
          const resp = await fetch(`http://localhost:8080/gestiongimnasios/tiposclases/${clase.nombreTipoClase}`, {
            headers: { 'Authorization': authHeader }
          });
          if (resp.ok) {
            const tipo = await resp.json();
            this.descripcionTipoClase = tipo.descripcion || '';
          }
        } catch (e) {
          this.descripcionTipoClase = '';
        }
      }
    },
    cerrarModal() {
      this.modalVisible = false;
      this.claseSeleccionada = null;
    },
    async eliminarSolicitud(idSolicitud) {
      console.log('[eliminarSolicitud] Click en eliminar para id:', idSolicitud);
      try {
        const solicitud = this.solicitudesUsuario.find(s => s.id === idSolicitud);
        console.log('[eliminarSolicitud] Solicitud encontrada:', solicitud);
        if (!solicitud || !solicitud.idClase) {
          this.mostrarMensaje('No se pudo determinar la clase de la solicitud', 'error');
          return;
        }
        const email = localStorage.getItem('email');
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        console.log('[eliminarSolicitud] Llamando a endpoint:', `http://localhost:8080/gestiongimnasios/clasescolectivas/${solicitud.idClase}/solicitudes/${idSolicitud}`);
        const response = await fetch(`http://localhost:8080/gestiongimnasios/clasescolectivas/${solicitud.idClase}/solicitudes/${idSolicitud}`, {
          method: 'DELETE',
          headers: {
            'Authorization': authHeader
          }
        });
        console.log('[eliminarSolicitud] Respuesta del backend:', response);
        if (response.ok) {
          this.mostrarMensaje('Solicitud eliminada correctamente', 'success');
          await this.cargarClases();
        } else {
          this.mostrarMensaje('Error al eliminar la solicitud', 'error');
        }
      } catch (error) {
        console.log('[eliminarSolicitud] Error:', error);
        this.mostrarMensaje('Error al eliminar la solicitud', 'error');
      }
    }
  },
  mounted() {
    this.cargarTiposClase();
    this.cargarClases();
    window.addEventListener('resize', this.checkMobile);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkMobile);
  }
}
</script>

<style>
/* Estilos del calendario inspirados en EntrenadorHorario.vue */

.horario-container {
  max-width: 100%;
  overflow-x: auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.horario-grid {
  min-width: 800px;
}

.horario-header {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  gap: 1px;
  background: #e9ecef;
  border-radius: 8px 8px 0 0;
}

.hora-column-header {
  background: #495057;
  color: white;
  padding: 12px 8px;
  text-align: center;
  font-weight: bold;
  border-radius: 8px 0 0 0;
}

.dia-header {
  background: #6c757d;
  color: white;
  padding: 12px 8px;
  text-align: center;
  font-weight: bold;
}

.dia-header.last-dia {
  border-radius: 0 8px 0 0;
}

.dia-nombre {
  font-size: 0.9rem;
  margin-bottom: 2px;
}

.dia-abrev {
  font-size: 0.7rem;
  opacity: 0.8;
}

.horario-body {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  gap: 1px;
  background: #e9ecef;
  border: 1px solid #e9ecef;
  border-top: none;
  border-radius: 0 0 8px 8px;
}

.hora-label {
  background: #f8f9fa;
  padding: 20px 8px;
  text-align: center;
  font-weight: bold;
  color: #495057;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.celda-horario {
  background: white;
  padding: 4px;
  min-height: 62px;
  position: relative;
  border-bottom: 1px solid #e9ecef;
}

.clase-card {
  border-radius: 6px;
  padding: 8px;
  font-size: 0.8rem;
  line-height: 1.2;
  position: absolute;
  top: 6px;
  left: 6px;
  right: 6px;
  bottom: 6px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  font-weight: 600;
}

.clase-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.25) !important;
}

.clase-tipo {
  font-weight: bold;
  margin-bottom: 2px;
}

.clase-horario {
  color: #424242;
  margin-bottom: 2px;
  font-size: 0.7rem;
}

.clase-ocupacion {
  color: #666;
  font-size: 0.7rem;
}

/* Los colores ahora se aplican dinámicamente mediante JavaScript */

/* Estilos para vista móvil */
.mobile-day-section {
  border: 1px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
}

.calendario-toggle {
  padding: 0.5rem;
  border-radius: 0.5rem;
  transition: background-color 0.2s;
}

.calendario-toggle:hover {
  background-color: #f8f9fa;
}

.mobile-day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background-color: #f8f9fa;
  cursor: pointer;
  color: #000;
  font-weight: 500;
}

.mobile-day-classes {
  padding: 0.5rem;
}

.mobile-time-slot {
  margin-bottom: 0.5rem;
}

.time-slot-content {
  border-left: 3px solid #007bff;
  padding-left: 0.5rem;
  margin-bottom: 0.5rem;
}

.time-label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #495057;
  margin-bottom: 0.25rem;
}

.mobile-class-item {
  padding: 0.5rem;
  border-radius: 4px;
  margin-bottom: 0.25rem;
  font-size: 0.85rem;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.2s, box-shadow 0.2s;
}

.mobile-class-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 12px rgba(0,0,0,0.2) !important;
}

@media (max-width: 768px) {
  .horario-container {
    padding: 8px;
  }
  
  .hora-column-header,
  .dia-header {
    padding: 8px 4px;
    font-size: 0.8rem;
  }
  
  .dia-nombre {
    display: none;
  }
  
  .hora-label {
    padding: 12px 4px;
    font-size: 0.8rem;
  }
  
  .clase-card {
    padding: 6px;
    font-size: 0.7rem;
  }
}
</style>
