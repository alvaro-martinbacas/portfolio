<template>
  <div class="background-container">
    <!-- Imagen de fondo -->
    <div class="background-image bg-fondo7"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="container py-4 px-3 position-relative" style="z-index: 10;">
      <div class="row">
        <div class="col-xxl-10 col-lg-12 mx-auto">
          <h1 class="mb-4 text-center" style="color:#111;font-weight:bold;">Tu Horario Semanal</h1>
          
          <!-- Horario semanal -->
          <div class="horario-container bg-white rounded shadow-lg p-4">
            <div v-if="cargando" class="text-center py-5">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando horario...</span>
              </div>
              <p class="mt-3 text-muted">Cargando tu horario...</p>
            </div>
            
            <div v-else-if="error" class="alert alert-danger text-center">
              <i class="fas fa-exclamation-triangle me-2"></i>
              {{ error }}
            </div>
            
            <!-- Vista Desktop -->
            <div v-if="!isMobile" class="horario-grid">
              <!-- Cabecera con días de la semana -->
              <div class="horario-header">
                <div class="hora-column-header">Hora</div>
                <div v-for="dia in diasSemana" :key="dia.value" class="dia-header">
                  <div class="dia-nombre">{{ dia.nombre }}</div>
                  <div class="dia-abrev">{{ dia.abrev }}</div>
                </div>
              </div>
              
              <!-- Filas de horarios -->
              <div class="horario-body">
                <template v-for="hora in horasDelDia" :key="hora">
                  <div class="hora-label">{{ formatearHora(hora) }}</div>
                  <div 
                    v-for="dia in diasSemana" 
                    :key="`${dia.value}-${hora}`" 
                    class="celda-horario"
                  >
                    <div 
                      v-for="clase in obtenerClaseEnHorario(dia.value, hora)" 
                      :key="clase.id"
                      class="clase-card"
                      :style="{ 
                        ...obtenerEstiloClase(clase.nombreTipoClase),
                        height: `${calcularAlturaClase(clase)}px`,
                        zIndex: 10,
                        cursor: 'pointer',
                        transition: 'transform 0.2s, box-shadow 0.2s'
                      }"
                      @mouseover="(e) => e.target.style.transform = 'translateY(-2px)'"
                      @mouseout="(e) => e.target.style.transform = 'translateY(0px)'"
                    >
                      <div class="clase-tipo">{{ clase.nombreTipoClase }}</div>
                      <div class="clase-horario">
                        {{ formatearHoraCorta(clase.horaIni) }} - {{ formatearHoraCorta(clase.horaFin) }}
                      </div>
                    </div>
                  </div>
                </template>
              </div>
            </div>

            <!-- Vista Mobile -->
            <div v-else class="mobile-view">
              <!-- Botón para mostrar/ocultar calendario -->
              <div class="mobile-header" @click="toggleCalendario">
                <h5 class="mb-0">
                  <i class="fas fa-calendar-alt me-2"></i>
                  Horario Semanal
                </h5>
                <i :class="['fas', calendarioVisible ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
              </div>

              <!-- Lista de días colapsable -->
              <div v-show="calendarioVisible" class="mobile-days">
                <div 
                  v-for="dia in diasSemana" 
                  :key="dia.value" 
                  class="mobile-day"
                >
                  <!-- Header del día -->
                  <div 
                    class="mobile-day-header" 
                    @click="toggleDia(dia.value)"
                    :class="{ 'expanded': diaExpandido === dia.value }"
                  >
                    <div class="day-info">
                      <span class="day-name">{{ dia.nombre }}</span>
                      <span class="day-count">{{ obtenerClasesDelDia(dia.value).length }} clases</span>
                    </div>
                    <i :class="['fas', diaExpandido === dia.value ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
                  </div>

                  <!-- Contenido del día -->
                  <div v-show="diaExpandido === dia.value" class="mobile-day-content">
                    <div v-if="obtenerClasesDelDia(dia.value).length === 0" class="no-classes">
                      <i class="fas fa-calendar-times text-muted"></i>
                      <span>No hay clases programadas</span>
                    </div>
                    <div v-else class="classes-list">
                      <div 
                        v-for="clase in obtenerClasesDelDia(dia.value)" 
                        :key="clase.id"
                        class="mobile-class-card"
                        :style="obtenerEstiloClase(clase.nombreTipoClase)"
                      >
                        <div class="class-main-info">
                          <div class="class-title">{{ clase.nombreTipoClase }}</div>
                          <div class="class-time">
                            <i class="fas fa-clock me-1"></i>
                            {{ formatearHoraCorta(clase.horaIni) }} - {{ formatearHoraCorta(clase.horaFin) }}
                          </div>
                        </div>
                        <div class="class-details">
                          <div class="detail-item">
                            <i class="fas fa-users me-1"></i>
                            {{ clase.plazasOcupadas }}/{{ clase.plazasMax }} participantes
                          </div>
                          <div class="detail-item">
                            <i class="fas fa-dumbbell me-1"></i>
                            Duración: {{ calcularDuracionClaseMinutos(clase) }} min
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Resumen de clases -->
            <div v-if="!cargando && !error && clases.length > 0" class="mt-5">
              <h4 class="mb-3" style="color: #111;">Resumen de tus clases</h4>
              <div class="row">
                <div class="col-md-6">
                  <div class="stats-card">
                    <i class="fas fa-calendar-check text-success"></i>
                    <div>
                      <div class="stats-number">{{ clases.length }}</div>
                      <div class="stats-label">Clases semanales</div>
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="stats-card">
                    <i class="fas fa-users text-info"></i>
                    <div>
                      <div class="stats-number">{{ totalPersonas }}</div>
                      <div class="stats-label">Total de participantes</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-else-if="!cargando && !error && clases.length === 0" class="text-center py-5">
              <i class="fas fa-calendar-times text-muted" style="font-size: 3rem;"></i>
              <h4 class="mt-3 text-muted">No tienes clases programadas</h4>
              <p class="text-muted">Contacta con el administrador para programar tus clases.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue';

const clases = ref([]);
const cargando = ref(true);
const error = ref('');
const calendarioVisible = ref(true);
const diaExpandido = ref(null);
const isMobile = ref(window.innerWidth <= 768);

const diasSemana = [
  { value: 'MONDAY', nombre: 'Lunes', abrev: 'L' },
  { value: 'TUESDAY', nombre: 'Martes', abrev: 'M' },
  { value: 'WEDNESDAY', nombre: 'Miércoles', abrev: 'X' },
  { value: 'THURSDAY', nombre: 'Jueves', abrev: 'J' },
  { value: 'FRIDAY', nombre: 'Viernes', abrev: 'V' },
  { value: 'SATURDAY', nombre: 'Sábado', abrev: 'S' },
  { value: 'SUNDAY', nombre: 'Domingo', abrev: 'D' }
];

// Horarios del gimnasio (de 6:00 a 23:00)
const horasDelDia = Array.from({ length: 17 }, (_, i) => i + 6);

const totalPersonas = computed(() => {
  return clases.value.reduce((total, clase) => total + clase.plazasOcupadas, 0);
});

// Funciones para responsividad
function checkMobile() {
  isMobile.value = window.innerWidth <= 768;
}

function toggleCalendario() {
  calendarioVisible.value = !calendarioVisible.value;
  if (!calendarioVisible.value) {
    diaExpandido.value = null;
  }
}

function toggleDia(dia) {
  diaExpandido.value = diaExpandido.value === dia ? null : dia;
}

onMounted(async () => {
  await cargarHorario();
  window.addEventListener('resize', checkMobile);
});

// Cleanup del event listener
onBeforeUnmount(() => {
  window.removeEventListener('resize', checkMobile);
});

async function cargarHorario() {
  try {
    cargando.value = true;
    error.value = '';
    
    const email = localStorage.getItem('email');
    const token = localStorage.getItem('token');
    
    const response = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${email}/clases`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (!response.ok) {
      throw new Error('Error al cargar el horario');
    }
    
    clases.value = await response.json();
  } catch (err) {
    error.value = 'Error al cargar tu horario. Inténtalo de nuevo más tarde.';
    console.error('Error al cargar horario:', err);
  } finally {
    cargando.value = false;
  }
}

function obtenerClaseEnHorario(diaSemana, hora) {
  return clases.value.filter(clase => {
    if (clase.diaSemana !== diaSemana) return false;
    
    const horaInicio = parseInt(clase.horaIni.split(':')[0]);
    
    // Solo mostrar la clase en su hora de inicio para evitar duplicados
    return hora === horaInicio;
  });
}

function calcularDuracionClase(clase) {
  const horaInicio = parseInt(clase.horaIni.split(':')[0]);
  const horaFin = parseInt(clase.horaFin.split(':')[0]);
  const minutoInicio = parseInt(clase.horaIni.split(':')[1]);
  const minutoFin = parseInt(clase.horaFin.split(':')[1]);
  
  const duracionHoras = horaFin - horaInicio;
  const duracionMinutos = minutoFin - minutoInicio;
  
  // Si hay minutos adicionales, redondear hacia arriba
  return duracionMinutos > 0 ? duracionHoras + 1 : duracionHoras;
}

function calcularAlturaClase(clase) {
  const duracion = calcularDuracionClase(clase);
  // Cada celda tiene 80px de altura + 1px de gap = 81px total
  // Para 1 hora: 80px - 8px (padding) = 72px
  // Para múltiples horas: (duracion * 81) - 8px (padding) - 1px (gap final)
  if (duracion === 1) {
    return 72; // Altura de una celda menos el padding
  } else {
    return (duracion * 81) - 9; // Múltiples celdas menos padding y gap final
  }
}

function formatearHora(hora) {
  return `${hora.toString().padStart(2, '0')}:00`;
}

function formatearHoraCorta(horaString) {
  return horaString.substring(0, 5); // "HH:mm"
}

// Funciones adicionales para vista mobile
function obtenerClasesDelDia(diaSemana) {
  return clases.value.filter(clase => clase.diaSemana === diaSemana);
}

function calcularDuracionClaseMinutos(clase) {
  const [horaIni, minIni] = clase.horaIni.split(':').map(Number);
  const [horaFin, minFin] = clase.horaFin.split(':').map(Number);
  
  const inicio = horaIni * 60 + minIni;
  const fin = horaFin * 60 + minFin;
  
  return fin - inicio;
}

function obtenerColorClase(tipoClase) {
  // Generar un hash del nombre para obtener un color consistente
  const hash = generarHashString(tipoClase);
  return `clase-dinamica-${hash}`;
}

function generarHashString(str) {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i);
    hash = ((hash << 5) - hash) + char;
    hash = hash & hash; // Convertir a 32bit
  }
  return Math.abs(hash) % 20; // Limitar a 20 colores diferentes
}

function obtenerEstiloClase(tipoClase) {
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
  
  const hash = generarHashString(tipoClase);
  const colorData = coloresPastel[hash % coloresPastel.length];
  
  return {
    backgroundColor: colorData.bg,
    borderLeftColor: colorData.border,
    color: colorData.color,
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15), 0 1px 3px rgba(0, 0, 0, 0.1)',
    border: `1px solid ${colorData.border}`,
    borderLeft: `4px solid ${colorData.border}`
  };
}
</script>

<style scoped>
/* Estilos de fondo */
.background-container {
  position: relative;
  min-height: 100vh;
  background-color: #F1F0EA;
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
  z-index: 2;
}

/* Estilos para los contenedores de contenido */
.horario-container,
.alert,
.card,
.stats-card {
  position: relative;
  z-index: 15;
  background-color: rgba(241, 240, 234, 0.95) !important;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(224, 221, 207, 0.3);
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.horario-container {
  max-width: 100%;
  overflow-x: auto;
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

.dia-header:last-child {
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

.fila-hora {
  display: contents;
}

.hora-label {
  background: #f8f9fa;
  padding: 29px 8px;
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
  min-height: 80px;
  position: relative;
  border-bottom: 1px solid #e9ecef;
}

.clase-card {
  border-radius: 6px;
  padding: 8px;
  font-size: 0.8rem;
  line-height: 1.2;
  position: absolute;
  top: 4px;
  left: 4px;
  right: 4px;
  min-height: calc(100% - 8px);
  font-weight: 600;
}

.clase-tipo {
  font-weight: bold;
  margin-bottom: 2px;
}

.clase-horario {
  margin-bottom: 2px;
  opacity: 0.8;
}

.clase-ocupacion {
  font-size: 0.7rem;
  opacity: 0.7;
}

/* Los colores ahora se aplican dinámicamente mediante JavaScript */

.stats-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.stats-card i {
  font-size: 2rem;
}

.stats-number {
  font-size: 1.8rem;
  font-weight: bold;
  color: #2c3e50;
}

.stats-label {
  color: #6c757d;
  font-size: 0.9rem;
}

/* Estilos para vista mobile */
.mobile-view {
  display: block;
}

.mobile-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  color: #495057;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  transition: all 0.3s ease;
}

.mobile-header:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  transform: translateY(-1px);
}

.mobile-days {
  margin-top: 12px;
}

.mobile-day {
  margin-bottom: 8px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}

.mobile-day-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 12px 16px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.2s ease;
  border-bottom: 1px solid #dee2e6;
}

.mobile-day-header:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
}

.mobile-day-header.expanded {
  background: linear-gradient(135deg, #d1ecf1 0%, #bee5eb 100%);
  border-bottom-color: #b6d7dc;
}

.day-info {
  display: flex;
  flex-direction: column;
}

.day-name {
  font-weight: bold;
  font-size: 1rem;
  color: #495057;
}

.day-count {
  font-size: 0.8rem;
  color: #6c757d;
  margin-top: 2px;
}

.mobile-day-content {
  background: #fff;
  border-top: 1px solid #dee2e6;
}

.no-classes {
  padding: 24px 16px;
  text-align: center;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.no-classes i {
  font-size: 1.2rem;
}

.classes-list {
  padding: 8px;
}

.mobile-class-card {
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.mobile-class-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.mobile-class-card:last-child {
  margin-bottom: 0;
}

.class-main-info {
  margin-bottom: 8px;
}

.class-title {
  font-weight: bold;
  font-size: 1rem;
  margin-bottom: 4px;
  color: inherit;
}

.class-time {
  font-size: 0.9rem;
  opacity: 0.9;
  display: flex;
  align-items: center;
}

.class-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 0.8rem;
  opacity: 0.8;
}

.detail-item {
  display: flex;
  align-items: center;
}

.detail-item i {
  width: 16px;
  text-align: center;
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
