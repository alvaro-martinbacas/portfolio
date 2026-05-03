<template>
  <div class="container py-4">
    <AlertMessage />
    <h1 class="mb-2" style="color:#111;font-weight:bold;">Panel Administrador - Accesos</h1>
    <h2 class="mb-4">Gestión de Accesos</h2>

    <!-- Filtros -->
    <div class="mb-4 bg-light p-3 rounded">
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">Gimnasio *</label>
          <select v-model="filtroGimnasio" class="form-select" @change="async () => { await cargarUsuarios(); cargarAccesos(); }">
            <option value="" disabled>Selecciona un gimnasio para ver datos generales</option>
            <option v-for="g in gimnasios" :key="g.id" :value="g.id">{{ g.nombre }}</option>
          </select>
        </div>
        <div class="col-md-6">
          <label class="form-label">Usuario (opcional - para estadísticas específicas)</label>
          <select v-model="filtroUsuario" class="form-select" @change="cargarAccesos" :disabled="!filtroGimnasio">
            <option value="">Todos los usuarios del gimnasio</option>
            <option v-for="u in usuarios" :key="u.email" :value="u.email">
              {{ u.nombre }} {{ u.apellidos }}
            </option>
          </select>
        </div>
      </div>
    </div>
<!--
    <div v-if="filtroGimnasio" class="mb-3 d-flex justify-content-end">
      <button class="btn btn-outline-secondary btn-sm" @click="mostrarModalDatosPrueba = true">
        <i class="fas fa-flask me-1"></i>
        Generar Datos de Prueba
      </button>
    </div>

    <div v-if="mostrarModalDatosPrueba" class="modal show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Generar Datos de Prueba</h5>
            <button type="button" class="btn-close" @click="mostrarModalDatosPrueba = false"></button>
          </div>
          <div class="modal-body">
            <p class="text-muted">Se generarán accesos aleatorios para cada usuario del gimnasio seleccionado en los últimos 30 días.</p>
            <div class="mb-3">
              <label class="form-label">Accesos por usuario:</label>
              <select v-model="cantidadAccesosPorUsuario" class="form-select">
                <option value="5">5 accesos por usuario</option>
                <option value="10">10 accesos por usuario</option>
                <option value="15">15 accesos por usuario</option>
                <option value="20">20 accesos por usuario</option>
              </select>
            </div>
            <div class="alert alert-warning">
              <i class="fas fa-exclamation-triangle me-2"></i>
              <strong>Atención:</strong> Esta acción generará datos ficticios. Úsala solo en entornos de prueba.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="mostrarModalDatosPrueba = false">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="generarDatosPrueba">
              <i class="fas fa-flask me-1"></i>
              Generar Datos
            </button>
          </div>
        </div>
      </div>
    </div>
    -->
    <!-- Estadísticas -->
    <div v-if="!filtroGimnasio" class="alert alert-info">
      <strong>¡Selecciona un gimnasio!</strong> Verás estadísticas generales del gimnasio y podrás filtrar por usuario específico para obtener sus estadísticas individuales.
    </div>
    <div v-else class="row mb-4">
      <template v-if="!filtroUsuario">
        <!-- Estadísticas del gimnasio -->
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">Media Diaria</h5>
              <p class="card-text display-6">{{ stats.mediaDiaria.toFixed(1) }}</p>
              <p class="card-text text-muted">personas/día</p>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">Máximo Diario</h5>
              <p class="card-text display-6">{{ stats.maximoDiario }}</p>
              <p class="card-text text-muted">{{ stats.maximoDiarioFecha }}</p>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">Hora Pico</h5>
              <p class="card-text display-6">{{ stats.horaPico }}</p>
              <p class="card-text text-muted">{{ stats.personasHoraPico }} personas</p>
            </div>
          </div>
        </div>
      </template>
      <template v-else>
        <!-- Estadísticas del usuario -->
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">Número de Accesos</h5>
              <p class="card-text display-6">{{ stats.totalAccesos }}</p>
              <p class="card-text text-muted">accesos totales</p>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">Duración Media</h5>
              <p class="card-text display-6">{{ stats.duracionMedia }}</p>
              <p class="card-text text-muted">por acceso</p>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">Hora Frecuente</h5>
              <p class="card-text display-6">{{ stats.horaPico }}</p>
              <p class="card-text text-muted">hora habitual</p>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- Gráfico de ocupación y accesos -->
    <TablaAccesosPorHora 
      v-if="!filtroUsuario"
      :accesos="accesos"
      @filtro-change="({ fechaInicio, fechaFin }) => {
        filtroFechaInicio = fechaInicio;
        filtroFechaFin = fechaFin;
        cargarAccesos();
      }" />
    
    <!-- Consulta de aforo específico -->
    <div v-if="!filtroUsuario" class="card shadow-sm border-0 mb-4">
      <div class="card-body">
        <h5 class="card-title mb-1">Consulta de Aforo Histórico</h5>
        <p class="text-muted small mb-3">{{ gimnasioSeleccionado?.nombre }}</p>
        <div class="row g-3 align-items-end">
          <div class="col-md-4">
            <label class="form-label">Fecha</label>
            <input type="date" v-model="consultaAforo.fecha" class="form-control" />
          </div>
          <div class="col-md-4">
            <label class="form-label">Hora</label>
            <input type="time" v-model="consultaAforo.hora" class="form-control" />
          </div>
          <div class="col-md-4">
            <button class="btn btn-primary w-100 mb-3" @click="consultarAforo" :disabled="!puedeConsultarAforo">
              Consultar Aforo
            </button>
          </div>
        </div>
        <div v-if="consultaAforo.resultado !== null" class="mt-4">
          <div class="alert" :class="getColorAlertaAforo">
            <div class="d-flex align-items-center">
              <div class="flex-grow-1">
                <strong>Ocupación registrada el {{ formatFechaHoraConsulta }}</strong>
                <div class="mt-2 h4 mb-0">{{ consultaAforo.resultado }} personas</div>
              </div>
            </div>
            
          </div>
        </div>
      </div>
    </div>

    <!-- Calendario de accesos (solo para usuario) -->
    <div v-if="filtroUsuario" class="card mb-4">
      <div class="card-body p-3">
        <h6 class="card-title mb-3">Calendario de Accesos</h6>
        <Calendario
          :accesos="accesos"
          :current-month="currentMonth"
          :filtro-fecha-inicio="filtroFechaInicio"
          :filtro-fecha-fin="filtroFechaFin"
        />
      </div>
    </div>

    <!-- Tabla de accesos -->
    <div class="table-responsive">
      <table class="table table-hover bg-white rounded shadow-sm">
        <thead>
          <tr>
            <th @click="sortBy('usuario')" style="cursor:pointer">
              Usuario
              <span v-if="sortKey === 'usuario'">{{ sortAsc ? '▲' : '▼' }}</span>
            </th>
            <th @click="sortBy('gimnasio')" style="cursor:pointer">
              Gimnasio
              <span v-if="sortKey === 'gimnasio'">{{ sortAsc ? '▲' : '▼' }}</span>
            </th>
            <th @click="sortBy('horaEntrada')" style="cursor:pointer">
              Hora Entrada
              <span v-if="sortKey === 'horaEntrada'">{{ sortAsc ? '▲' : '▼' }}</span>
            </th>
            <th @click="sortBy('horaSalida')" style="cursor:pointer">
              Hora Salida
              <span v-if="sortKey === 'horaSalida'">{{ sortAsc ? '▲' : '▼' }}</span>
            </th>
            <th>Duración</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="acceso in sortedAccesos" :key="acceso.id">
            <td>{{ acceso.nombreUsuario }} {{ acceso.apellidosUsuario }}</td>
            <td>{{ acceso.nombreGimnasio }}</td>
            <td>{{ formatDateTime(acceso.horaEntrada) }}</td>
            <td>{{ acceso.horaSalida ? formatDateTime(acceso.horaSalida) : 'En gimnasio' }}</td>
            <td>{{ calcularDuracion(acceso.horaEntrada, acceso.horaSalida) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Paginación -->
    <nav v-if="totalPages > 1" class="mt-4">
      <ul class="pagination justify-content-center">
        <!-- Primera página -->
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" href="#" @click.prevent="changePage(1)">Primera</a>
        </li>
        <!-- Anterior -->
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)">Anterior</a>
        </li>
        <!-- Número de página actual -->
        <li class="page-item disabled">
          <span class="page-link">
            Página {{ currentPage }} de {{ totalPages }}
          </span>
        </li>
        <!-- Siguiente -->
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">Siguiente</a>
        </li>
        <!-- Última página -->
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" href="#" @click.prevent="changePage(totalPages)">Última</a>
        </li>
      </ul>
    </nav>
  </div>
</template>


<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';
import TablaAccesosPorHora from '../components/TablaAccesosPorHora.vue';
import Calendario from '../components/Calendario.vue';

const { mostrarMensaje } = useAlertMessage();

// Referencias y estados
const gimnasios = ref([]);
const usuarios = ref([]);
const accesos = ref([]);
const filtroGimnasio = ref('');
const filtroUsuario = ref('');
const filtroFechaInicio = ref('');
const filtroFechaFin = ref('');
const sortKey = ref('horaEntrada');
const sortAsc = ref(false);
const currentPage = ref(1);
const itemsPerPage = ref(10);
const currentMonth = ref(null); // Mes actual para la navegación del calendario

// Estados para el modal de datos de prueba
const mostrarModalDatosPrueba = ref(false);
const cantidadAccesosPorUsuario = ref(10);

// Estado para la consulta de aforo
const consultaAforo = ref({
  fecha: new Date().toISOString().split('T')[0],
  hora: new Date().getHours().toString().padStart(2, '0') + ':00',
  resultado: null
});

const gimnasioSeleccionado = computed(() => {
  return gimnasios.value.find(g => g.id === parseInt(filtroGimnasio.value));
});

const puedeConsultarAforo = computed(() => {
  return filtroGimnasio.value && consultaAforo.value.fecha && consultaAforo.value.hora;
});

const formatFechaHoraConsulta = computed(() => {
  if (!consultaAforo.value.fecha || !consultaAforo.value.hora) return '';
  const fecha = new Date(consultaAforo.value.fecha + 'T' + consultaAforo.value.hora);
  return new Intl.DateTimeFormat('es-ES', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(fecha);
});

const getPorcentajeOcupacion = computed(() => {
  if (consultaAforo.value.resultado === null || !gimnasioSeleccionado.value?.aforoMaximo) return 0;
  return Math.round((consultaAforo.value.resultado / gimnasioSeleccionado.value.aforoMaximo) * 100);
});

const getColorAlertaAforo = computed(() => {
  const porcentaje = getPorcentajeOcupacion.value;
  if (porcentaje < 50) return 'alert-success';
  if (porcentaje < 80) return 'alert-warning';
  return 'alert-danger';
});

const getColorProgressBar = computed(() => {
  const porcentaje = getPorcentajeOcupacion.value;
  if (porcentaje < 50) return 'bg-success';
  if (porcentaje < 80) return 'bg-warning';
  return 'bg-danger';
});

// Estadísticas
const stats = ref({
  mediaDiaria: 0,
  maximoDiario: 0,
  maximoDiarioFecha: '',
  maximoSemanal: 0,
  maximoSemanalFecha: '',
  horaPico: '',
  personasHoraPico: 0
});

// Mounted hook
onMounted(async () => {
  await Promise.all([
    cargarGimnasios(),
    cargarUsuarios()
  ]);
  await cargarAccesos();
});

// Computed properties
const totalPages = computed(() => Math.ceil(accesos.value.length / itemsPerPage.value));
const sortedAccesos = computed(() => {
  let sorted = [...accesos.value].sort((a, b) => {
    let valA, valB;
    
    switch(sortKey.value) {
      case 'usuario':
        valA = (a.nombreUsuario + ' ' + a.apellidosUsuario) || '';
        valB = (b.nombreUsuario + ' ' + b.apellidosUsuario) || '';
        break;
      case 'gimnasio':
        valA = a.nombreGimnasio || '';
        valB = b.nombreGimnasio || '';
        break;
      case 'horaEntrada':
        valA = new Date(a.horaEntrada);
        valB = new Date(b.horaEntrada);
        break;
      case 'horaSalida':
        valA = a.horaSalida ? new Date(a.horaSalida) : new Date(0);
        valB = b.horaSalida ? new Date(b.horaSalida) : new Date(0);
        break;
      default:
        valA = a[sortKey.value];
        valB = b[sortKey.value];
    }
    
    if (valA instanceof Date && valB instanceof Date) {
      return sortAsc.value ? valA - valB : valB - valA;
    }
    
    return sortAsc.value
      ? String(valA).localeCompare(String(valB))
      : String(valB).localeCompare(String(valA));
  });

  // Aplicar paginación
  const startIndex = (currentPage.value - 1) * itemsPerPage.value;
  return sorted.slice(startIndex, startIndex + itemsPerPage.value);
});

const calendarioSemanas = computed(() => {
  if (!filtroUsuario.value) return [];

  // Obtener el primer día del mes actual
  const inicio = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth(), 1);
  // Retroceder hasta el lunes
  inicio.setDate(inicio.getDate() - inicio.getDay() + (inicio.getDay() === 0 ? -6 : 1));

  // Obtener el último día del mes actual
  const fin = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() + 1, 0);
  // Avanzar hasta el domingo
  fin.setDate(fin.getDate() + (7 - fin.getDay()));

  // Crear mapa de accesos por día
  const accesosPorDia = {};
  accesos.value.forEach(acceso => {
    const fecha = acceso.horaEntrada.split('T')[0];
    accesosPorDia[fecha] = (accesosPorDia[fecha] || 0) + 1;
  });

  const semanas = [];
  let currentDate = new Date(inicio);
  let weekNumber = 1;

  while (currentDate <= fin) {
    const week = {
      weekNumber,
      days: []
    };

    // Para cada día de la semana (Lun-Dom)
    for (let i = 0; i < 7; i++) {
      const date = new Date(currentDate);
      const dateStr = date.toISOString().split('T')[0];
      
      const isCurrentMonth = currentMonth.value.getMonth() === date.getMonth() &&
                           currentMonth.value.getFullYear() === date.getFullYear();
      
      // Añadir un día de margen al rango
      const minDateWithMargin = fechaMinima.value ? new Date(fechaMinima.value) : null;
      const maxDateWithMargin = fechaMaxima.value ? new Date(new Date(fechaMaxima.value).setDate(new Date(fechaMaxima.value).getDate() + 1)) : null;
      
      const isInDateRange = (!minDateWithMargin || date >= minDateWithMargin) && 
                           (!maxDateWithMargin || date < maxDateWithMargin);
      
      week.days.push({
        date: dateStr,
        hasAccess: !!accesosPorDia[dateStr],
        accessCount: accesosPorDia[dateStr] || 0,
        isCurrentMonth: isCurrentMonth,
        isToday: new Date().toISOString().split('T')[0] === dateStr,
        'different-month': !isCurrentMonth,
        isOutOfRange: !isInDateRange
      });

      currentDate.setDate(currentDate.getDate() + 1);
    }

    semanas.push(week);
    weekNumber++;
  }

  return semanas;
});

// Funciones
async function cargarGimnasios() {
  try {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
      headers: { 'Authorization': authHeader }
    });
    if (!res.ok) throw new Error('Error al cargar gimnasios');
    gimnasios.value = await res.json();
  } catch (error) {
    mostrarMensaje('Error al cargar gimnasios: ' + error.message, 'danger');
  }
}

async function cargarUsuarios() {
  try {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    let res;
    if (filtroGimnasio.value) {
      // Cargar usuarios de un gimnasio específico
      res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${filtroGimnasio.value}/usuarios`, {
        headers: { 'Authorization': authHeader }
      });
    } else {
      // Cargar todos los usuarios del sistema
      res = await fetch('http://localhost:8080/gestiongimnasios/usuarios/todos', {
        headers: { 'Authorization': authHeader }
      });
    }
    if (!res.ok) throw new Error('Error al cargar usuarios');
    usuarios.value = await res.json();
  } catch (error) {
    mostrarMensaje('Error al cargar usuarios: ' + error.message, 'danger');
  }
}

async function cargarAccesos() {
  if (!filtroGimnasio.value) {
    accesos.value = [];
    calcularEstadisticas();
    return;
  }

  try {
    let url = 'http://localhost:8080/gestiongimnasios/accesos';
    const params = new URLSearchParams();
    
    params.append('gimnasioId', filtroGimnasio.value);
    if (filtroUsuario.value) params.append('usuarioEmail', filtroUsuario.value);
    if (filtroFechaInicio.value) params.append('fechaInicio', filtroFechaInicio.value);
    if (filtroFechaFin.value) params.append('fechaFin', filtroFechaFin.value);
    
    if (params.toString()) url += '?' + params.toString();

    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(url, {
      headers: { 'Authorization': authHeader }
    });
    if (!res.ok) throw new Error('Error al cargar accesos');
    const data = await res.json();
    accesos.value = data;
    
    // Actualizar el mes actual si es necesario
    if (!currentMonth.value) {
      if (filtroFechaFin.value) {
        // Si hay filtro de fecha fin, mostrar ese mes
        currentMonth.value = new Date(new Date(filtroFechaFin.value).getFullYear(), new Date(filtroFechaFin.value).getMonth(), 1);
      } else if (data.length > 0) {
        // Si hay accesos, mostrar el mes del último acceso
        const ultimoAcceso = new Date(Math.max(...data.map(a => new Date(a.horaEntrada))));
        currentMonth.value = new Date(ultimoAcceso.getFullYear(), ultimoAcceso.getMonth(), 1);
      } else {
        // Si no hay accesos ni filtro, mostrar el mes actual
        currentMonth.value = new Date(new Date().getFullYear(), new Date().getMonth(), 1);
      }
    }
    
    calcularEstadisticas();
  } catch (error) {
    mostrarMensaje('Error al cargar accesos: ' + error.message, 'danger');
  }
}

function calcularEstadisticas() {
  if (!filtroGimnasio.value) {
    stats.value = {
      mediaDiaria: 0,
      maximoDiario: 0,
      maximoDiarioFecha: '',
      horaPico: '00:00',
      personasHoraPico: 0,
      totalAccesos: 0,
      duracionMedia: '0h 0m'
    };
    return;
  }

  const onFiltroChange = ({ fechaInicio, fechaFin }) => {
    filtroFechaInicio.value = fechaInicio;
    filtroFechaFin.value = fechaFin;
    cargarAccesos();
  };

  // Calcular el número total de días en el intervalo
  let totalDias = 0;
  if (filtroFechaInicio.value && filtroFechaFin.value) {
    const inicio = new Date(filtroFechaInicio.value);
    const fin = new Date(filtroFechaFin.value);
    totalDias = Math.ceil((fin - inicio) / (1000 * 60 * 60 * 24)) + 1;
  } else {
    // Si no hay fechas seleccionadas, usar el rango natural de los datos
    const fechas = accesos.value.map(a => new Date(a.horaEntrada.split('T')[0]));
    if (fechas.length > 0) {
      const inicio = new Date(Math.min(...fechas));
      const fin = new Date(Math.max(...fechas));
      totalDias = Math.ceil((fin - inicio) / (1000 * 60 * 60 * 24)) + 1;
    } else {
      totalDias = 1; // Para evitar división por cero
    }
  }

  // Agrupar accesos por día y hora
  const accesosPorDia = {};
  const accesosPorHora = Array(24).fill(0);
  
  accesos.value.forEach(acceso => {
    const fecha = acceso.horaEntrada.split('T')[0];
    const hora = new Date(acceso.horaEntrada).getHours();
    
    accesosPorDia[fecha] = (accesosPorDia[fecha] || 0) + 1;
    accesosPorHora[hora]++;
  });

  const horaPico = accesosPorHora.indexOf(Math.max(...accesosPorHora));

  if (filtroUsuario.value) {
    // Calcular duración media para el usuario
    const accesosConSalida = accesos.value.filter(a => a.horaSalida);
    let duracionMedia = '0h 0m';
    
    if (accesosConSalida.length > 0) {
      const duracionTotal = accesosConSalida.reduce((total, acceso) => {
        const entrada = new Date(acceso.horaEntrada);
        const salida = new Date(acceso.horaSalida);
        return total + (salida - entrada);
      }, 0);
      
      const duracionPromedio = duracionTotal / accesosConSalida.length;
      const hours = Math.floor(duracionPromedio / 3600000);
      const minutes = Math.floor((duracionPromedio % 3600000) / 60000);
      duracionMedia = `${hours}h ${minutes}m`;
    }

    // Estadísticas para un usuario específico
    stats.value = {
      totalAccesos: accesos.value.length,
      duracionMedia: duracionMedia,
      horaPico: `${horaPico}:00`,
      personasHoraPico: accesosPorHora[horaPico]
    };
  } else {
    // Estadísticas para el gimnasio
    const maximoDiario = Math.max(...Object.values(accesosPorDia));
    stats.value = {
      mediaDiaria: totalDias ? accesos.value.length / totalDias : 0,
      maximoDiario,
      maximoDiarioFecha: Object.entries(accesosPorDia).find(([, val]) => val === maximoDiario)?.[0] || '',
      horaPico: `${horaPico}:00`,
      personasHoraPico: accesosPorHora[horaPico]
    };
  }
}

function sortBy(key) {
  if (sortKey.value === key) {
    sortAsc.value = !sortAsc.value;
  } else {
    sortKey.value = key;
    sortAsc.value = true;
  }
}

function changePage(page) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
}

function formatDateTime(dateStr) {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  return new Intl.DateTimeFormat('es-ES', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
}

function calcularDuracion(entrada, salida) {
  if (!entrada || !salida) return '-';
  
  const start = new Date(entrada);
  const end = new Date(salida);
  const diff = end - start;
  
  const hours = Math.floor(diff / 3600000);
  const minutes = Math.floor((diff % 3600000) / 60000);
  
  return `${hours}h ${minutes}m`;
}

async function generarDatosPrueba() {
  if (!filtroGimnasio.value) {
    mostrarMensaje('Selecciona un gimnasio primero', 'warning');
    return;
  }
  
  try {
    mostrarModalDatosPrueba.value = false; // Cerrar el modal
    
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/accesos/test/${filtroGimnasio.value}?cantidadPorUsuario=${cantidadAccesosPorUsuario.value}`, {
      method: 'POST',
      headers: { 'Authorization': authHeader }
    });
    
    if (!res.ok) throw new Error('Error al generar datos de prueba');
    
    mostrarMensaje(`Datos de prueba generados correctamente: ${cantidadAccesosPorUsuario.value} accesos por usuario`, 'success');
    await cargarAccesos();
  } catch (error) {
    mostrarMensaje('Error al generar datos de prueba: ' + error.message, 'danger');
  }
}

// Funciones del calendario
function previousMonth() {
  currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() - 1, 1);
}

function nextMonth() {
  currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() + 1, 1);
}

function formatMonth(date) {
  return new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' }).format(date);
}

// Consulta de aforo
async function consultarAforo() {
  if (!puedeConsultarAforo.value) return;

  try {
    const fechaHora = `${consultaAforo.value.fecha}T${consultaAforo.value.hora}:00`;
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const response = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${filtroGimnasio.value}/aforo?fechaHora=${fechaHora}`, {
      headers: { 'Authorization': authHeader }
    });
    
    if (!response.ok) throw new Error('Error al consultar el aforo');
    
    const data = await response.json();
    consultaAforo.value.resultado = data.aforo;
  } catch (error) {
    mostrarMensaje('Error al consultar el aforo: ' + error.message, 'danger');
  }
}
</script>

<style scoped>
.table {
  border-radius: 12px;
  overflow: hidden;
}
.card {
  border-radius: 12px;
  transition: transform 0.2s;
}
.card:hover {
  transform: translateY(-5px);
}
.display-6 {
  font-size: 2.5rem;
  font-weight: 300;
  line-height: 1.2;
}
th {
  cursor: pointer;
}
th:hover {
  background-color: rgba(0,0,0,0.05);
}
.chart-container {
  margin-top: 1rem;
}


/* Estilos específicos para la consulta de aforo */
.alert {
  border-radius: 8px;
}

.progress {
  height: 10px;
  border-radius: 5px;
}

.progress-bar {
  transition: width 0.4s ease;
}
</style>
