<template>
  <div class="background-container">
    <!-- Imagen de fondo -->
    <div class="background-image bg-fondo7"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="container py-4 px-3 position-relative" style="z-index: 10;">
      <div class="row">
        <!-- Ejercicios de Rutina -->
        <div class="col-xxl-8 col-lg-10 mx-auto" style="max-width: 100%;">
          <h1 class="mb-2 text-center" style="color:#111;font-weight:bold;">Panel Entrenador - Rutinas</h1>
        <h3 class="mb-3 mt-5" style="color: #111">Ver / Editar rutinas</h3>
        <!-- Menú de visualización de rutinas -->
        <div class="mb-3 bg-light p-3 rounded">
          <div class="row">
            <div class="col-12">
              <label class="form-label mb-1">Ver rutinas de usuario</label>
              <select v-model="visualUsuario" class="form-select">
                <option value="todos">Todos los usuarios</option>
                <option v-for="u in usuarios" :key="u.email" :value="u.email">{{ u.nombre }} {{ u.apellidos }} ({{ u.email }})</option>
              </select>
            </div>
          </div>
        </div>

        <div class="mb-2">
          <div v-if="visualUsuario && visualUsuario !== 'todos'">
            <div class="alert alert-info p-2 mb-2">
              Viendo rutinas de <b>{{ usuarios.find(u => u.email === visualUsuario)?.nombre || visualUsuario }}</b>
            </div>
            <div class="text-center mb-3">
              <button @click="abrirModalCrear" class="btn btn-primary" :class="{ 'btn-lg': isMobile }">
                <i class="fas fa-plus me-2"></i>Nueva Rutina
              </button>
            </div>
          </div>
          <div v-else-if="visualUsuario === 'todos'" class="alert alert-info p-2 mb-0">
            Viendo todas las rutinas
          </div>
        </div>
        <!-- Vista Desktop -->
        <table v-if="visualRutinas.length > 0 && !isMobile" class="table table-hover bg-white rounded shadow-sm align-middle w-100">
          <thead>
            <tr>
              <th class="text-center" style="width: 10%;">Nombre</th>
              <th class="text-center" style="width: 35%;">Descripción</th>
              <th class="text-center" style="width: 25%;">Usuario</th>
              <th class="text-center" style="width: 20%;">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in visualRutinas" :key="r.id">
              <td class="text-center">{{ r.nombre }}</td>
              <td class="text-center">{{ r.descripcion }}</td>
              <td class="text-center">
                <span v-if="r.emailUsuario">
                  {{ r.emailUsuario }}
                </span>
                <span v-else class="text-muted">Sin asignar</span>
              </td>
              <td class="text-center">
                <div class="d-flex gap-1 justify-content-center">
                  <button class="btn btn-sm btn-success" @click.stop="abrirPopupEjercicios(r)">Modificar ejercicios</button>
                  <button class="btn btn-sm btn-warning" @click.stop="editarRutina(r)">Editar</button>
                  <button class="btn btn-sm btn-danger" @click.stop="eliminarRutina(r)">Eliminar</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Vista Mobile -->
        <div v-if="visualRutinas.length > 0 && isMobile" class="mobile-rutinas">
          <div 
            v-for="r in visualRutinas" 
            :key="r.id" 
            class="mobile-rutina-card"
          >
            <!-- Header de la rutina -->
            <div 
              class="mobile-rutina-header" 
              @click="toggleRutina(r.id)"
              :class="{ 'expanded': rutinaExpandida === r.id }"
            >
              <div class="rutina-info">
                <div class="rutina-nombre">{{ r.nombre }}</div>
                <div class="rutina-usuario">
                  <i class="fas fa-user me-1"></i>
                  <span v-if="r.emailUsuario">{{ r.emailUsuario }}</span>
                  <span v-else class="text-muted">Sin asignar</span>
                </div>
              </div>
              <i :class="['fas', rutinaExpandida === r.id ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
            </div>

            <!-- Contenido expandible de la rutina -->
            <div v-show="rutinaExpandida === r.id" class="mobile-rutina-content">
              <div class="rutina-descripcion">
                <strong>Descripción:</strong>
                <p class="mt-1 mb-3">{{ r.descripcion }}</p>
              </div>
              
              <div class="rutina-acciones">
                <button 
                  class="btn btn-success btn-sm mb-2" 
                  @click.stop="abrirPopupEjercicios(r)"
                >
                  <i class="fas fa-dumbbell me-2"></i>
                  Modificar ejercicios
                </button>
                <button 
                  class="btn btn-warning btn-sm mb-2 ms-2" 
                  @click.stop="editarRutina(r)"
                >
                  <i class="fas fa-edit me-2"></i>
                  Editar
                </button>
                <button 
                  class="btn btn-danger btn-sm mb-2 ms-2" 
                  @click.stop="eliminarRutina(r)"
                >
                  <i class="fas fa-trash me-2"></i>
                  Eliminar
                </button>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="alert alert-warning mt-2">No hay rutinas para este usuario o entrenador.</div>

        <!-- Popup para modificar ejercicios de la rutina -->
        <div v-if="popupEjerciciosVisible" class="modal-overlay-top mt-4">
          <div class="modal-content" style="max-width: 1200px; width: 98%; margin: 2vh auto; max-height: 95vh; overflow-y: auto;">
            <h4 style="color: #111;">Ejercicios de la rutina: {{ rutinaEjercicios?.nombre }}</h4>
            <!-- Vista Desktop de ejercicios -->
            <div v-if="rutinaEjercicios && rutinaEjercicios.ejerciciosEnRutina && rutinaEjercicios.ejerciciosEnRutina.length && !isMobile">
              <table class="table table-bordered table-responsive">
                <thead>
                  <tr>
                    <th style="width: 20%;">Ejercicio</th>
                    <th style="width: 12%;">Grupo Muscular</th>
                    <th style="width: 12%;">Equipo</th>
                    <th style="width: 8%;">Series</th>
                    <th style="width: 8%;">Repeticiones</th>
                    <th style="width: 10%;">Descanso (seg)</th>
                    <th style="width: 15%;">Indicaciones</th>
                    <th style="width: 15%;">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="ej in rutinaEjercicios.ejerciciosEnRutina" :key="ej.id">
                    <td>{{ ej.nombreEjercicio }}</td>
                    <td>
                      <span>{{ obtenerGrupoMuscular(ej.nombreEjercicio) }}</span>
                    </td>
                    <td>
                      <span>{{ obtenerEquipo(ej.nombreEjercicio) }}</span>
                    </td>
                    <td>{{ ej.series }}</td>
                    <td>{{ ej.repeticiones }}</td>
                    <td>{{ ej.descanso }}</td>
                    <td>{{ ej.indicaciones }}</td>
                    <td>
                      <button class="btn btn-sm btn-warning me-1" @click="editarEjercicioRutina(ej)">Editar</button>
                      <button class="btn btn-sm btn-danger" @click="eliminarEjercicioRutina(ej)">Eliminar</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Vista Mobile de ejercicios -->
            <div v-if="rutinaEjercicios && rutinaEjercicios.ejerciciosEnRutina && rutinaEjercicios.ejerciciosEnRutina.length && isMobile" class="mobile-ejercicios">
              <div 
                v-for="ej in rutinaEjercicios.ejerciciosEnRutina" 
                :key="ej.id"
                class="mobile-ejercicio-card"
              >
                <div class="ejercicio-header">
                  <h6 class="ejercicio-nombre">{{ ej.nombreEjercicio }}</h6>
                  <div class="ejercicio-acciones">
                    <button class="btn btn-sm btn-warning me-1" @click="editarEjercicioRutina(ej)">
                      <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" @click="eliminarEjercicioRutina(ej)">
                      <i class="fas fa-trash"></i>
                    </button>
                  </div>
                </div>
                
                <div class="ejercicio-detalles">
                  <div class="detalle-row">
                    <span class="detalle-label">Grupo:</span>
                    <span class="detalle-valor">{{ obtenerGrupoMuscular(ej.nombreEjercicio) }}</span>
                  </div>
                  <div class="detalle-row">
                    <span class="detalle-label">Equipo:</span>
                    <span class="detalle-valor">{{ obtenerEquipo(ej.nombreEjercicio) }}</span>
                  </div>
                  <div class="detalle-row">
                    <span class="detalle-label">Series:</span>
                    <span class="detalle-valor">{{ ej.series }}</span>
                  </div>
                  <div class="detalle-row">
                    <span class="detalle-label">Repeticiones:</span>
                    <span class="detalle-valor">{{ ej.repeticiones }}</span>
                  </div>
                  <div class="detalle-row">
                    <span class="detalle-label">Descanso:</span>
                    <span class="detalle-valor">{{ ej.descanso }} seg</span>
                  </div>
                  <div v-if="ej.indicaciones" class="detalle-row">
                    <span class="detalle-label">Indicaciones:</span>
                    <span class="detalle-valor">{{ ej.indicaciones }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="alert alert-info">No hay ejercicios en esta rutina.</div>
            <!-- Formulario edición ejercicioRutina -->
            <div v-if="ejercicioRutinaEditando" class="mt-3">
              <h5 style="color:#222;">Editar ejercicio</h5>
              <form class="row g-2 align-items-end" @submit.prevent="guardarEdicionEjercicioRutina">
                <div class="col-12 col-md-4">
                  <label class="form-label mb-1" style="font-weight:500; color:#222;">Series</label>
                  <input v-model.number="ejercicioRutinaEditando.series" type="number" min="1" class="form-control" required />
                </div>
                <div class="col-4 col-md-2">
                  <label class="form-label mb-1" style="font-weight:500; color:#222;">Reps</label>
                  <input v-model.number="ejercicioRutinaEditando.repeticiones" type="number" min="1" class="form-control" required />
                </div>
                <div class="col-4 col-md-2">
                  <label class="form-label mb-1" style="font-weight:500; color:#222;">Descanso (seg)</label>
                  <input v-model.number="ejercicioRutinaEditando.descanso" type="number" min="0" class="form-control" required />
                </div>
                <div class="col-12 col-md-8 mt-2 mt-md-0">
                  <label class="form-label mb-1" style="font-weight:500; color:#222;">Indicaciones</label>
                  <input v-model="ejercicioRutinaEditando.indicaciones" class="form-control" />
                </div>
                <div class="col-12 col-md-4 mt-2 mt-md-0 d-flex align-items-start">
                  <button class="btn mb-3 btn-success w-100" type="submit">Guardar</button>
                  <button class="btn btn-secondary ms-2 w-100" type="button" @click="ejercicioRutinaEditando = null">Cancelar</button>
                </div>
              </form>
            </div>
            <!-- Formulario añadir ejercicio -->
            <div v-else class="mt-3">
              <h5 style="color:#222;">Añadir ejercicio</h5>
              
              <!-- Filtros para ejercicios -->
                <div class="row g-2">
                  <div class="col-md-4">
                    <input v-model="filtroNombre" class="form-control form-control-sm" placeholder="Buscar por nombre..." />
                  </div>
                  <div class="col-md-4">
                    <select v-model="filtroGrupoMuscular" class="form-select form-select-sm">
                      <option value="">Todos los grupos musculares</option>
                      <option v-for="grupo in GRUPOS_MUSCULARES" :key="grupo" :value="grupo">{{ grupo }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <select v-model="filtroEquipo" class="form-select form-select-sm">
                      <option value="">Todos los equipos</option>
                      <option v-for="equipo in EQUIPOS" :key="equipo" :value="equipo">{{ equipo }}</option>
                    </select>
                  </div>
                </div>
              
              
              <form class="row g-2 align-items-end" @submit.prevent="crearEjercicioRutina">
                <div class="col-12 col-md-4">
                  <label class="form-label mb-1" style="font-weight:500; color:#222;">Ejercicio</label>
                  <select v-model="nuevoEjercicioRutina.idEjercicio" class="form-select" required>
                    <option value="" disabled>Selecciona ejercicio ({{ ejerciciosFiltrados.length }} disponibles)</option>
                    <option v-for="e in ejerciciosFiltrados" :key="e.id" :value="e.id">
                      {{ e.nombre }} - {{ e.grupoMuscular }} ({{ e.equipo }})
                    </option>
                  </select>
                </div>
                <div class="col-4 col-md-2">
                  <label class="form-label mb-1" style="font-weight:500; color:#222;">Series</label>
                  <input v-model.number="nuevoEjercicioRutina.series" type="number" min="1" class="form-control" required />
                </div>
                <div class="col-4 col-md-2">
                  <label class="form-label mb-1" style="font-weight:500; color:#222;">Reps</label>
                  <input v-model.number="nuevoEjercicioRutina.repeticiones" type="number" min="1" class="form-control" required />
                </div>
                <div class="col-4 col-md-2">
                  <label class="form-label mb-1" style="font-weight:500; color:#222;">Descanso (seg)</label>
                  <input v-model.number="nuevoEjercicioRutina.descanso" type="number" min="0" class="form-control" required />
                </div>
                <div class="col-12 col-md-6 mt-2">
                  <label class="form-label mb-1" style="font-weight:500; color:#222;">Indicaciones</label>
                  <input v-model="nuevoEjercicioRutina.indicaciones" class="form-control" />
                </div>
                <div class="col-11 col-md-6 mt-2">
                  <button class="btn btn-success mb-3 ms-md-5" type="submit" style=" font-weight:500; height:38px; min-width:100px;">Añadir</button>
                </div>
              </form>
            </div>
            <div class="mt-3 text-end">
              <button class="btn btn-secondary ms-2" @click="cerrarPopupEjercicios">Cerrar</button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Modal para crear rutina -->
      <div v-if="mostrarModalCrear" class="modal-backdrop modal-overlay-top" style="z-index: 2147483647; position: fixed !important;">
        <div class="modal-content" style="z-index: 2147483647; position: relative !important;">
          <h4 style="color: #111;">Nueva Rutina</h4>
          <form @submit.prevent="crearRutina">
            <div class="mb-3">
              <label class="form-label">Nombre</label>
              <input v-model="nuevaRutina.nombre" type="text" required class="form-control">
            </div>
            <div class="mb-3">
              <label class="form-label">Descripción</label>
              <textarea v-model="nuevaRutina.descripcion" class="form-control" rows="3" required></textarea>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" @click="cancelarCreacion">Cancelar</button>
              <button type="submit" class="btn btn-primary">
                <i class="fas fa-plus me-2"></i>Crear Rutina
              </button>
            </div>
          </form>
        </div>
      </div>

        <div v-if="rutinaEditando" class="modal-backdrop modal-overlay-top" style="z-index: 2147483647; position: fixed !important;">
          <div class="modal-content" style="z-index: 2147483647; position: relative !important;">
            <h4 style="color: #111;">Editar Rutina</h4>
            <form @submit.prevent="guardarEdicionRutina">
              <input v-model="rutinaEditando.nombre" class="form-control mb-2" placeholder="Nombre" required />
              <input v-model="rutinaEditando.descripcion" class="form-control mb-2" placeholder="Descripción" required />
              <button class="btn btn-success me-2" type="submit">Guardar</button>
              <button class="btn btn-secondary" @click="rutinaEditando = null" type="button">Cancelar</button>
            </form>
          </div>
        </div>
      </div> <!-- cierre col-md-7 -->
      <AlertMessage />
    </div> <!-- cierre container -->
  </div> <!-- cierre background-container -->
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';
// --- Visualización independiente de rutinas ---
const visualUsuario = ref("todos");
const visualRutinas = ref([]);
const token = localStorage.getItem('token');
const isMobile = ref(window.innerWidth <= 768);
const rutinaExpandida = ref(null);

const { mostrarMensaje } = useAlertMessage();

// Watch para la visualización de rutinas
watch(visualUsuario, async (nuevo) => {
  if (!nuevo) {
    visualRutinas.value = [];
    return;
  }

  try {
    let res;
    if (nuevo === 'todos') {
      const email = localStorage.getItem('email');
      res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${email}/rutinas`, {
        credentials: 'include',
        headers: {
        'Authorization': `Bearer ${token}`
      }
      });
    } else {
      res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${nuevo}/rutinas`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
    }

    if (res.ok) {
      visualRutinas.value = await res.json();
    } else {
      visualRutinas.value = [];
      mostrarMensaje('Error al cargar las rutinas', 'error');
    }
  } catch (e) {
    visualRutinas.value = [];
    mostrarMensaje('Error al cargar las rutinas', 'error');
    console.error(e);
  }
});

const rutinas = ref([]);
const rutinaSeleccionada = ref(null);
const rutinaEditando = ref(null);
const mostrarModalCrear = ref(false);
const nuevaRutina = ref({ nombre: '', descripcion: '' });

const ejercicios = ref([]);
const usuarios = ref([]);

const nuevoEjercicioRutina = ref({ idEjercicio: '', series: 1, repeticiones: 1, descanso: 0, indicaciones: '' });
const ejercicioRutinaEditando = ref(null);

// Constantes para filtros
const GRUPOS_MUSCULARES = [
    'Pectoral', 'Espalda', 'Bíceps', 'Tríceps', 'Hombros', 
    'Abdominales', 'Glúteos', 'Cuádriceps', 'Isquiotibiales',
    'Gemelos', 'Antebrazos', 'Trapecio', 'Aductores', 'Core',
    'Cardio', 'Fullbody', 'Otros'
];

const EQUIPOS = [
    'Sin material', 'Mancuernas', 'Barra', 'Discos', 'Polea',
    'Máquina', 'Máquina Smith', 'Elíptica', 'Bicicleta estática',
    'Cinta de correr', 'Steps', 'TRX', 'Fitball', 'Bandas elásticas',
    'Kettlebell', 'Comba', 'Rueda abdominal', 'Balón medicinal', 'Otros'
];

// Variables para filtros de ejercicios en el popup
const filtroGrupoMuscular = ref('');
const filtroEquipo = ref('');
const filtroNombre = ref('');

const emailEntrenador = localStorage.getItem('email') || '';
const idGimnasioEntrenador = ref(null);

// Computed para ejercicios filtrados
const ejerciciosFiltrados = computed(() => {
    let filtrados = [...ejercicios.value];
    
    // Aplicar filtros
    if (filtroGrupoMuscular.value) {
        filtrados = filtrados.filter(e => e.grupoMuscular === filtroGrupoMuscular.value);
    }
    
    if (filtroEquipo.value) {
        filtrados = filtrados.filter(e => e.equipo === filtroEquipo.value);
    }
    
    if (filtroNombre.value.trim()) {
        const nombreBusqueda = filtroNombre.value.toLowerCase().trim();
        filtrados = filtrados.filter(e => 
            e.nombre.toLowerCase().includes(nombreBusqueda)
        );
    }
    
    // Ordenar por nombre
    return filtrados.sort((a, b) => a.nombre.localeCompare(b.nombre));
});

async function obtenerGimnasioEntrenador() {
  try {
    const token = localStorage.getItem('token');
    const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${emailEntrenador}/gimnasio`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    if (res.ok) {
      const gimnasio = await res.json();
      idGimnasioEntrenador.value = gimnasio.id;
    }
  } catch (error) {
    console.error('Error al obtener el gimnasio del entrenador:', error);
  }
}

// Funciones para responsividad
function checkMobile() {
  isMobile.value = window.innerWidth <= 768;
}

function toggleRutina(rutinaId) {
  rutinaExpandida.value = rutinaExpandida.value === rutinaId ? null : rutinaId;
}

onMounted(async () => {
  try {
    window.addEventListener('resize', checkMobile);
    await obtenerGimnasioEntrenador();
    await cargarUsuarios();
    await cargarEjercicios();
    
    // Cargar todas las rutinas del entrenador al inicio
    const email = localStorage.getItem('email');
    const token = localStorage.getItem('token');
    const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${email}/rutinas`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (res.ok) {
      visualRutinas.value = await res.json();
    } else {
      mostrarMensaje('Error al cargar las rutinas', 'error');
    }
  } catch (error) {
    mostrarMensaje('Error de conexión al cargar los datos iniciales', 'error');
    console.error('Error:', error);
  }
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', checkMobile);
});

async function cargarGimnasios() {
  const token = localStorage.getItem('token');
  const authHeader = 'Bearer ' + token;
  const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
    headers: { 'Authorization': authHeader }
  });
  gimnasios.value = await res.json();
}

async function cargarEjercicios() {
  try {
    const token = localStorage.getItem('token');
    const res = await fetch('http://localhost:8080/gestiongimnasios/ejercicios/todos', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    if (!res.ok) {
      console.error('Error al cargar ejercicios:', res.status, await res.text());
      ejercicios.value = [];
      return;
    }
    const data = await res.json();
    ejercicios.value = Array.isArray(data) ? data : [];
  } catch (err) {
    console.error('Error de red al cargar ejercicios:', err);
    ejercicios.value = [];
  }
}

async function cargarEjerciciosRutina(rutinaId) {
    if (!rutinaId) return [];
    try {
        const token = localStorage.getItem('token');
        const res = await fetch(`http://localhost:8080/gestiongimnasios/rutinas/${rutinaId}/ejerciciosRutina`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (res.ok) {
            const data = await res.json();
            return Array.isArray(data) ? data : [];
        }
        return [];
    } catch (err) {
        console.error('Error al cargar ejercicios de la rutina:', err);
        return [];
    }
}

async function cargarUsuarios() {
  try {
    if (!idGimnasioEntrenador.value) {
      console.error('No se ha podido obtener el ID del gimnasio del entrenador');
      return;
    }
    const token = localStorage.getItem('token');
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasioEntrenador.value}/usuarios`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    if (res.ok) {
      usuarios.value = await res.json();
    } else {
      console.error('Error al cargar usuarios:', res.status);
      usuarios.value = [];
    }
  } catch (e) {
    console.error('Error al cargar usuarios:', e);
    usuarios.value = [];
  }
}

async function cargarRutinas() {
  // Mostrar rutinas según usuario o entrenador seleccionado
  let res;
  if (nuevaRutina.value.emailUsuario) {
    res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${nuevaRutina.value.emailUsuario}/rutinas`);
  } else if (nuevaRutina.value.emailEntrenador) {
    res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${nuevaRutina.value.emailEntrenador}/rutinas`);
  } else {
    rutinas.value = [];
    return;
  }
  if (res.ok) {
    rutinas.value = await res.json();
  } else {
    rutinas.value = [];
  }
}

async function cargarEntrenadores(idGimnasio) {
  if (!idGimnasio) { entrenadores.value = []; return; }
  const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/entrenadores`);
  if (res.ok) {
    // Filtrar solo activos si es necesario
    const lista = await res.json();
    entrenadores.value = lista.filter(e => e.activo);
  } else {
    entrenadores.value = [];
  }
}

function seleccionarRutina(r) {
  rutinaSeleccionada.value = r;
}

function abrirModalCrear() {
  nuevaRutina.value = { nombre: '', descripcion: '' };
  mostrarModalCrear.value = true;
}

function cancelarCreacion() {
  mostrarModalCrear.value = false;
  nuevaRutina.value = { nombre: '', descripcion: '' };
}

async function crearRutina() {
  if (!visualUsuario.value || visualUsuario.value === 'todos') {
    mostrarMensaje('Selecciona un usuario primero', 'error');
    return;
  }

  if (!idGimnasioEntrenador.value) {
    mostrarMensaje('Error: No se pudo obtener el gimnasio del entrenador', 'error');
    return;
  }

  const emailEntrenador = localStorage.getItem('email');
  const dRutina = {
    nombre: nuevaRutina.value.nombre,
    descripcion: nuevaRutina.value.descripcion,
    emailEntrenador: emailEntrenador,
    emailUsuario: visualUsuario.value
  };

  try {
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasioEntrenador.value}/usuarios/${visualUsuario.value}/rutinas`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(dRutina)
    });

    if (!res.ok) {
      mostrarMensaje('Error al crear rutina: ' + await res.text(), 'error');
      return;
    }

    const rutinaCreada = await res.json();
    mostrarMensaje('Rutina creada con éxito', 'success');
    mostrarModalCrear.value = false;
    nuevaRutina.value = { nombre: '', descripcion: '' };

    // Recargar las rutinas del usuario actual
    const resRutinas = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${visualUsuario.value}/rutinas`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    if (resRutinas.ok) {
      visualRutinas.value = await resRutinas.json();
    }
  } catch (error) {
    mostrarMensaje('Error de conexión al crear la rutina', 'error');
    console.error('Error:', error);
  }
}

function editarRutina(r) {
  rutinaEditando.value = { ...r };
}

async function guardarEdicionRutina() {
  const r = rutinaEditando.value;
  // Incluye el emailEntrenador en el body
  const body = {
    id: r.id,
    nombre: r.nombre,
    descripcion: r.descripcion,
    emailEntrenador: r.emailEntrenador // <-- Añadido
  };
  const token = localStorage.getItem('token');
  const res = await fetch(`http://localhost:8080/gestiongimnasios/rutinas/${r.id}`, {
    method: 'PUT',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(body)
  });
  if (!res.ok) {
    mostrarMensaje('Error al editar rutina: ' + await res.text(), 'error');
    return;
  }
  mostrarMensaje('Rutina editada con éxito', 'success');
  rutinaEditando.value = null;
  
  // Recargar la lista de rutinas manteniendo el filtro actual
  try {
    let res;
    if (visualUsuario.value === 'todos') {
      res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${emailEntrenador}/rutinas`, {
        credentials: 'include',
        headers: {
        'Authorization': `Bearer ${token}`
      }
      });
    } else {
      res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${visualUsuario.value}/rutinas`, {
        credentials: 'include',
        headers: {
        'Authorization': `Bearer ${token}`
      }
      });
    }

    if (res.ok) {
      visualRutinas.value = await res.json();
    } else {
      mostrarMensaje('Error al recargar las rutinas', 'error');
    }
  } catch (error) {
    mostrarMensaje('Error de conexión al recargar las rutinas', 'error');
    console.error(error);
  }
}

async function eliminarRutina(r) {
  // Eliminar rutina usando el nuevo endpoint para entrenador
  // Se requiere el id de la rutina y el email del entrenador
  try {
    const token = localStorage.getItem('token');
    const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${emailEntrenador}/rutinas/${r.id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    if (!res.ok) {
      mostrarMensaje('Error al eliminar rutina: ' + await res.text(), 'error');
      return;
    }
    mostrarMensaje('Rutina eliminada con éxito', 'success');
    rutinaSeleccionada.value = null;
    // Recargar la lista de rutinas según el filtro actual
    if (visualUsuario.value === 'todos') {
      const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${emailEntrenador}/rutinas`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (res.ok) visualRutinas.value = await res.json();
    } else if (visualUsuario.value) {
      const res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${visualUsuario.value}/rutinas`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (res.ok) visualRutinas.value = await res.json();
    } else {
      visualRutinas.value = [];
    }
  } catch (e) {
    mostrarMensaje('Error de conexión al eliminar rutina', 'error');
  }
}

// Popup ejercicios
const popupEjerciciosVisible = ref(false);
const rutinaEjercicios = ref(null);

async function abrirPopupEjercicios(rutina) {
  await cargarEjercicios();
  try {
    // Cargar los ejercicios de la rutina usando el nuevo endpoint
    const ejerciciosEnRutina = await cargarEjerciciosRutina(rutina.id);
    //console.log('Ejercicios recibidos para la rutina:', ejerciciosEnRutina);
    if (Array.isArray(ejerciciosEnRutina) && ejerciciosEnRutina.length > 0) {
      console.log('Primer ejercicio:', ejerciciosEnRutina[0]);
    }
    rutinaEjercicios.value = { ...rutina, ejerciciosEnRutina };
  } catch (err) {
    console.error('Error al abrir popup de ejercicios:', err);
    rutinaEjercicios.value = { ...rutina, ejerciciosEnRutina: [] };
  }
  popupEjerciciosVisible.value = true;
}
function cerrarPopupEjercicios() {
  popupEjerciciosVisible.value = false;
  rutinaEjercicios.value = null;
}



async function crearEjercicioRutina() {
  try {
    const rutinaActual = rutinaEjercicios.value || rutinaSeleccionada.value;
    if (!rutinaActual) {
      console.error('No hay rutina seleccionada');
      return;
    }
    const dEjercicioRutina = {
      ejercicioRutina: {
        series: nuevoEjercicioRutina.value.series,
        repeticiones: nuevoEjercicioRutina.value.repeticiones,
        descanso: nuevoEjercicioRutina.value.descanso,
        indicaciones: nuevoEjercicioRutina.value.indicaciones
      },
      idEjercicio: nuevoEjercicioRutina.value.idEjercicio
    };
    const token = localStorage.getItem('token');
    const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${emailEntrenador}/rutinas/${rutinaActual.id}/ejerciciosRutina`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(dEjercicioRutina)
    });
    const textoRespuesta = await res.text();
    if (!res.ok) {
      mostrarMensaje('Error al añadir ejercicio: ' + textoRespuesta, 'error');
      return;
    }
    mostrarMensaje('Ejercicio añadido con éxito', 'success');
    // Recargar ejercicios de la rutina en el popup y mantenerlo abierto
    if (popupEjerciciosVisible.value) {
      const ejerciciosEnRutina = await cargarEjerciciosRutina(rutinaActual.id);
      rutinaEjercicios.value = { ...rutinaActual, ejerciciosEnRutina };
    }
    cargarRutinas();
  } catch (error) {
    mostrarMensaje('Error inesperado al crear ejercicio', 'error');
    console.error('Error en crearEjercicioRutina:', error);
  }
  nuevoEjercicioRutina.value = { idEjercicio: '', series: 1, repeticiones: 1, descanso: 0, indicaciones: '' };
}

function editarEjercicioRutina(ej) {
  console.log('Editando ejercicioRutina:', ej);
  ejercicioRutinaEditando.value = { ...ej };
}

async function guardarEdicionEjercicioRutina() {
  const rutina = rutinaEjercicios.value || rutinaSeleccionada.value;
  if (!rutina || !ejercicioRutinaEditando.value) return;
  const ej = ejercicioRutinaEditando.value;
  // Usar el email del entrenador de la rutina, si existe
  const email = rutina.emailEntrenador || emailEntrenador;
  const token = localStorage.getItem('token');
  const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${email}/rutinas/${rutina.id}/ejerciciosRutina/${ej.id}`, {
    method: 'PUT',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(ej)
  });
  if (!res.ok) {
    mostrarMensaje('Error al editar ejercicio: ' + await res.text(), 'error');
    return;
  }
  mostrarMensaje('Ejercicio editado con éxito', 'success');
  ejercicioRutinaEditando.value = null;
  if (popupEjerciciosVisible.value) {
    // Recargar ejercicios de la rutina en el popup
    const ejerciciosEnRutina = await cargarEjerciciosRutina(rutina.id);
    rutinaEjercicios.value = { ...rutina, ejerciciosEnRutina };
  }
  cargarRutinas();
}

async function eliminarEjercicioRutina(ej) {
  try {
    const rutina = rutinaEjercicios.value || rutinaSeleccionada.value;
    console.log('Eliminando ejercicio:', ej);
    console.log('De la rutina:', rutina);
    console.log('Email del entrenador:', emailEntrenador);
    
    if (!rutina) {
      console.error('No hay rutina seleccionada');
      return;
    }

    const url = `http://localhost:8080/gestiongimnasios/entrenadores/${emailEntrenador}/rutinas/${rutina.id}/ejerciciosRutina/${ej.id}`;
    console.log('URL de eliminación:', url);

    const res = await fetch(url, {
      method: 'DELETE',
      credentials: 'include'
    });

    console.log('Status de la respuesta:', res.status);
    const textoRespuesta = await res.text();
    console.log('Respuesta del servidor:', textoRespuesta);

    if (!res.ok) {
      mostrarMensaje('Error al eliminar ejercicio: ' + textoRespuesta, 'error');
      return;
    }

    // Recargar ejercicios de la rutina en el popup sin cerrarlo
    mostrarMensaje('Ejercicio eliminado con éxito', 'success');
    if (popupEjerciciosVisible.value) {
      try {
        const ejerciciosEnRutina = await cargarEjerciciosRutina(rutina.id);
        rutinaEjercicios.value = { ...rutinaEjercicios.value, ejerciciosEnRutina };
      } catch (err) {
        console.error('Error al recargar ejercicios tras eliminar:', err);
      }
    }
    await cargarRutinas();
  } catch (error) {
    console.error('Error al eliminar ejercicio:', error);
    mostrarMensaje('Error inesperado al eliminar el ejercicio', 'error');
  }
}

// Cargar usuarios y entrenadores al seleccionar gimnasio
watch(() => nuevaRutina.value.idGimnasio, (nuevo) => {
  cargarUsuarios(nuevo);
  cargarEntrenadores(nuevo);
  // Limpiar selects dependientes
  nuevaRutina.value.emailUsuario = '';
  nuevaRutina.value.emailEntrenador = '';
  rutinas.value = [];
  rutinaSeleccionada.value = null;
});

// Función para obtener el nombre del usuario por email
function obtenerNombreUsuario(email) {
  const usuario = usuarios.value.find(u => u.email === email);
  return usuario ? `${usuario.nombre} ${usuario.apellidos}` : email;
}

// Cargar rutinas al cambiar usuario o entrenador
watch([
  () => nuevaRutina.value.emailUsuario,
  () => nuevaRutina.value.emailEntrenador
], ([nuevoUsuario, nuevoEntrenador]) => {
  // Si ambos están vacíos, limpiar rutinas
  if (!nuevoUsuario && !nuevoEntrenador) {
    rutinas.value = [];
    rutinaSeleccionada.value = null;
    return;
  }
  cargarRutinas();
  rutinaSeleccionada.value = null;
});

// Funciones auxiliares para obtener información de ejercicios
function obtenerGrupoMuscular(nombreEjercicio) {
  const ejercicio = ejercicios.value.find(e => e.nombre === nombreEjercicio);
  return ejercicio ? ejercicio.grupoMuscular : 'N/A';
}

function obtenerEquipo(nombreEjercicio) {
  const ejercicio = ejercicios.value.find(e => e.nombre === nombreEjercicio);
  return ejercicio ? ejercicio.equipo : 'N/A';
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

.bg-fondo7 {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('../assets/fondo5.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.15;
  z-index: 0;
  filter: grayscale(20%) blur(1px);
}

/* Imagen de fondo */
.background-image {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('../assets/fondo4.jpg');
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
.modal-content,
.bg-light,
.alert,
.table,
.card,
.stats-card {
  position: relative;
  z-index: 15;
  background-color: rgba(241, 240, 234, 0.95) !important;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(224, 221, 207, 0.3);
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

/* Estilos específicos de AdminRutina.vue */
.table {
  margin-bottom: 0;
}

@media (max-width: 767px) {
  .row {
    flex-direction: column;
  }
  .col-md-6 {
    border: none !important;
    padding: 0 !important;
  }
}



.modal-content {
  background: white !important;
  border-radius: 12px;
  padding: 24px;
  max-width: 1200px !important;
  width: 98% !important;
  max-height: 95vh !important;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  z-index: 2147483647 !important;
  position: relative !important;
}

/* Estilos para vista mobile */
.mobile-rutinas {
  display: block;
}

.mobile-rutina-card {
  margin-bottom: 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  background: rgba(255, 255, 255, 0.95);
}

.mobile-rutina-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 16px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.2s ease;
  border-bottom: 1px solid #dee2e6;
}

.mobile-rutina-header:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
}

.mobile-rutina-header.expanded {
  background: linear-gradient(135deg, #d1ecf1 0%, #bee5eb 100%);
  border-bottom-color: #b6d7dc;
}

.rutina-info {
  flex-grow: 1;
}

.rutina-nombre {
  font-weight: bold;
  font-size: 1.1rem;
  color: #495057;
  margin-bottom: 4px;
}

.rutina-usuario {
  font-size: 0.9rem;
  color: #6c757d;
  display: flex;
  align-items: center;
}

.mobile-rutina-content {
  padding: 16px;
  background: white;
  border-top: 1px solid #dee2e6;
}

.rutina-descripcion {
  margin-bottom: 16px;
}

.rutina-descripcion p {
  margin-bottom: 0;
  color: #495057;
  line-height: 1.5;
}

.rutina-acciones {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rutina-acciones .btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Estilos para ejercicios en mobile */
.mobile-ejercicios {
  display: block;
}

.mobile-ejercicio-card {
  margin-bottom: 12px;
  padding: 12px;
  border-radius: 8px;
  background: rgba(248, 249, 250, 0.8);
  border: 1px solid #dee2e6;
}

.ejercicio-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.ejercicio-nombre {
  font-weight: bold;
  margin: 0;
  color: #495057;
  flex-grow: 1;
}

.ejercicio-acciones {
  display: flex;
  gap: 4px;
}

.ejercicio-detalles {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detalle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
  border-bottom: 1px solid rgba(222, 226, 230, 0.5);
}

.detalle-row:last-child {
  border-bottom: none;
}

.detalle-label {
  font-weight: 500;
  color: #6c757d;
  font-size: 0.9rem;
}

.detalle-valor {
  color: #495057;
  font-size: 0.9rem;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .container {
    padding-left: 8px !important;
    padding-right: 8px !important;
  }
  
  .modal-content {
    margin: 8px !important;
    padding: 16px !important;
    width: calc(100% - 16px) !important;
  }
  
  .form-select,
  .form-control {
    font-size: 16px; /* Evita zoom en iOS */
  }
  
  .btn-sm {
    padding: 4px 8px;
    font-size: 0.8rem;
  }
  
  .alert {
    font-size: 0.9rem;
    padding: 12px !important;
  }
}
</style>
