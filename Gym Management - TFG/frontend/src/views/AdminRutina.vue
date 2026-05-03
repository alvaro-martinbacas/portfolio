<template>
  <div class="container py-4">
    <!-- ...existing code... -->
    <h1 class="mb-2" style="color:#111;font-weight:bold;">Panel Administrador - Rutinas</h1>
    <div class="row">
      <!-- Ejercicios de Rutina -->
      <div class="col-xxl-4 col-lg-5 col-md-5 pe-md-4" style="min-width: 620px; max-width: 700px; flex: 0 0 80%;">
        <h3 class="mb-3 mt-5" style="color: #111">Ver / Editar rutinas</h3>
        <!-- Menú de visualización de rutinas (migrado a la izquierda) -->
        <div class="mb-3 bg-light p-3 rounded">
          <div class="mb-2">
            <label class="form-label mb-1">Gimnasio</label>
            <select v-model="visualGimnasio" class="form-select">
              <option value="" disabled>Selecciona gimnasio</option>
              <option v-for="g in gimnasios" :key="g.id" :value="g.id">{{ g.nombre }}</option>
            </select>
          </div>
          <div class="row g-2">
            <div class="col-md-6">
              <label class="form-label mb-1">Ver rutinas de usuario</label>
              <select v-model="visualUsuario" class="form-select" :disabled="!visualGimnasio" @change="visualEntrenador = ''">
                <option value="" disabled>Selecciona usuario</option>
                <option v-for="u in usuarios" :key="u.email" :value="u.email">{{ u.nombre }} ({{ u.email }})</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label mb-1">Ver rutinas de entrenador</label>
              <select v-model="visualEntrenador" class="form-select" :disabled="!visualGimnasio" @change="visualUsuario = ''">
                <option value="" disabled>Selecciona entrenador</option>
                <option v-for="e in entrenadores" :key="e.id" :value="e.email">{{ e.nombre }} ({{ e.email }})</option>
              </select>
            </div>
          </div>
        </div>

        <!-- Indicador de rutinas cargadas y tabla visualización (ahora solo en la izquierda) -->
        <div class="mb-2">
          <div v-if="visualUsuario">
            <div class="alert alert-info p-2 mb-0">
              Viendo rutinas de <b>{{ usuarios.find(u => u.email === visualUsuario)?.nombre || visualUsuario }}</b> (usuario)
            </div>
          </div>
          <div v-else-if="visualEntrenador">
            <div class="alert alert-primary p-2 mb-0">
              Viendo rutinas de <b>{{ entrenadores.find(e => e.email === visualEntrenador)?.nombre || visualEntrenador }}</b> (entrenador)
            </div>
          </div>
          <div v-else>
            <div class="alert alert-info p-2 mb-0">Selecciona usuario o entrenador para ver sus rutinas.</div>
          </div>
        </div>
        <table v-if="visualRutinas.length > 0" class="table table-hover bg-white rounded shadow-sm align-middle w-100">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in visualRutinas" :key="r.id" :class="{ 'table-primary': rutinaSeleccionada && rutinaSeleccionada.id === r.id }">
              <td @click="seleccionarRutina(r)" style="cursor:pointer;">{{ r.nombre }}</td>
              <td @click="seleccionarRutina(r)" style="cursor:pointer;">{{ r.descripcion }}</td>
              <td class="d-flex gap-1">
                <button class="btn btn-sm btn-success" @click.stop="abrirPopupEjercicios(r)">Modificar ejercicios</button>
                <button class="btn btn-sm btn-warning" @click.stop="editarRutina(r)">Editar</button>
                <button class="btn btn-sm btn-danger" @click.stop="eliminarRutina(r)">Eliminar</button>
              </td>
    <!-- Popup para modificar ejercicios de la rutina -->
    <div v-if="popupEjerciciosVisible" class="modal-backdrop" style="z-index: 1050; background-color: rgba(0, 0, 0, 0.75);">
      <div class="modal-content" style="max-width: 800px; width: 95%; margin: 3vh auto; max-height: 90vh; overflow-y: auto;">
        <h4 style="color: #111;">Ejercicios de la rutina: {{ rutinaEjercicios?.nombre }}</h4>
        <div v-if="rutinaEjercicios && rutinaEjercicios.ejerciciosEnRutina && rutinaEjercicios.ejerciciosEnRutina.length">
          <table class="table table-bordered">
            <thead>
              <tr>
                <th>Ejercicio</th>
                <th>Grupo Muscular</th>
                <th>Equipo</th>
                <th>Series</th>
                <th>Repeticiones</th>
                <th>Descanso</th>
                <th>Indicaciones</th>
                <th>Acciones</th>
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
          <div class="mb-3 bg-light p-2 rounded">
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
            </tr>
          </tbody>
        </table>
        <div v-else class="alert alert-warning mt-2">No hay rutinas para este usuario o entrenador.</div>
      </div>

      <!-- Rutinas -->
      <div class="col-md-12 ms-5" style="min-width: 500px; max-width: 800px; flex: 0 0 20%; margin: 0 0 0 00px">
        <h3 class="mb-3 mt-5" style="color: #111;">Gestión de Rutinas</h3>
        <!-- Formulario de creación de rutina -->
        <div class="mb-3 bg-light p-3 rounded">
          <form @submit.prevent="crearRutina">
            <div class="mb-2">
              <input v-model="nuevaRutina.nombre" class="form-control mb-2" placeholder="Nombre" required />
              <input v-model="nuevaRutina.descripcion" class="form-control mb-2" placeholder="Descripción" required />
              <select v-model="nuevaRutina.idGimnasio" class="form-select mb-2" required>
                <option value="" disabled>Selecciona gimnasio</option>
                <option v-for="g in gimnasios" :key="g.id" :value="g.id">{{ g.nombre }}</option>
              </select>
              <select v-model="nuevaRutina.emailUsuario" class="form-select mb-2" :disabled="!nuevaRutina.idGimnasio">
                <option value="" disabled>Selecciona usuario</option>
                <option v-for="u in usuarios" :key="u.email" :value="u.email">{{ u.nombre }} ({{ u.email }})</option>
              </select>
              <select v-model="nuevaRutina.emailEntrenador" class="form-select mb-2" :disabled="!nuevaRutina.idGimnasio">
                <option value="" disabled>Selecciona entrenador</option>
                <option v-for="e in entrenadores" :key="e.id" :value="e.email">{{ e.nombre }} ({{ e.email }})</option>
              </select>
            </div>
            <div class="col d-grid">
                <button class="btn btn-primary" type="submit" style="width: 30%;">Registrar</button>
            </div>
          </form>
        </div>

        <div v-if="rutinaEditando" class="modal-backdrop">
          <div class="modal-content">
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
    </div> <!-- cierre row -->
    <AlertMessage />
  </div> <!-- cierre container -->

</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';
// --- Visualización independiente de rutinas ---
const visualGimnasio = ref("");
const visualUsuario = ref("");
const visualEntrenador = ref("");
const visualRutinas = ref([]);

const { mostrarMensaje } = useAlertMessage();

// Watchers para la visualización de rutinas (usuarios/entrenadores)
watch(visualGimnasio, (nuevo) => {
  // Al cambiar gimnasio, limpiar selects y rutinas
  visualUsuario.value = "";
  visualEntrenador.value = "";
  visualRutinas.value = [];
  if (nuevo) {
    cargarUsuarios(nuevo);
    cargarEntrenadores(nuevo);
  } else {
    usuarios.value = [];
    entrenadores.value = [];
  }
});

watch([visualUsuario, visualEntrenador], async ([usuario, entrenador]) => {
  if (!visualGimnasio.value) {
    visualRutinas.value = [];
    return;
  }
  let res;
  const token = localStorage.getItem('token');
  if (usuario) {
    res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${usuario}/rutinas`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
  } else if (entrenador) {
    res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${entrenador}/rutinas`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
  } else {
    visualRutinas.value = [];
    return;
  }
  if (res.ok) {
    visualRutinas.value = await res.json();
  } else {
    visualRutinas.value = [];
  }
});

const rutinas = ref([]);
const rutinaSeleccionada = ref(null);
const rutinaEditando = ref(null);
const nuevaRutina = ref({ nombre: '', descripcion: '', idGimnasio: '', emailUsuario: '' });

const ejercicios = ref([]);
const usuarios = ref([]);
const gimnasios = ref([]);
const entrenadores = ref([]);

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

const nuevoEjercicioRutina = ref({ idEjercicio: '', series: 1, repeticiones: 1, descanso: 0, indicaciones: '' });
const ejercicioRutinaEditando = ref(null);

// Variables para filtros de ejercicios en el popup
const filtroGrupoMuscular = ref('');
const filtroEquipo = ref('');
const filtroNombre = ref('');

const emailEntrenador = localStorage.getItem('email') || '';

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

onMounted(() => {
  cargarGimnasios();
  cargarEjercicios();
  cargarEntrenadores();
});

async function cargarGimnasios() {
  const token = localStorage.getItem('token');
  const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
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
        const authHeader = 'Bearer ' + token;
        const res = await fetch(`http://localhost:8080/gestiongimnasios/rutinas/${rutinaId}/ejerciciosRutina`, { 
            headers: { 'Authorization': authHeader }
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

async function cargarUsuarios(idGimnasio) {
  if (!idGimnasio) { usuarios.value = []; return; }
  const token = localStorage.getItem('token');
  const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/usuarios`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  usuarios.value = await res.json();
}

async function cargarRutinas() {
  // Mostrar rutinas según usuario o entrenador seleccionado
  let res;
  const token = localStorage.getItem('token');
  if (nuevaRutina.value.emailUsuario) {
    res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${nuevaRutina.value.emailUsuario}/rutinas`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
  } else if (nuevaRutina.value.emailEntrenador) {
    res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${nuevaRutina.value.emailEntrenador}/rutinas`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
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
  const token = localStorage.getItem('token');
  const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/entrenadores`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
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

async function crearRutina() {
  if (!nuevaRutina.value.idGimnasio || !nuevaRutina.value.emailUsuario || !nuevaRutina.value.emailEntrenador) {
    mostrarMensaje('Selecciona gimnasio, usuario y entrenador', 'error');
    return;
  }
  const dRutina = {
    nombre: nuevaRutina.value.nombre,
    descripcion: nuevaRutina.value.descripcion,
    emailEntrenador: nuevaRutina.value.emailEntrenador
  };
  const idGimnasioActual = nuevaRutina.value.idGimnasio;
  const token = localStorage.getItem('token');
  const authHeader = 'Bearer ' + token;
  const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasioActual}/usuarios/${nuevaRutina.value.emailUsuario}/rutinas`, {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': authHeader
    },
    body: JSON.stringify(dRutina)
  });
  if (!res.ok) {
    mostrarMensaje('Error al crear rutina: ' + await res.text(), 'error');
    return;
  }
  mostrarMensaje('Rutina creada con éxito', 'success');
  // No limpiar el gimnasio, solo los demás campos
  nuevaRutina.value = { nombre: '', descripcion: '', idGimnasio: idGimnasioActual, emailUsuario: '', emailEntrenador: '' };
  // Recargar usuarios y entrenadores del gimnasio actual
  await cargarUsuarios(idGimnasioActual);
  await cargarEntrenadores(idGimnasioActual);
  cargarRutinas();
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
  const authHeader = 'Bearer ' + token;
  const res = await fetch(`http://localhost:8080/gestiongimnasios/rutinas/${r.id}`, {
    method: 'PUT',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': authHeader
    },
    body: JSON.stringify(body)
  });
  if (!res.ok) {
    mostrarMensaje('Error al editar rutina: ' + await res.text(), 'error');
    return;
  }
  mostrarMensaje('Rutina editada con éxito', 'success');
  rutinaEditando.value = null;
  // Recargar la lista de rutinas según el filtro actual
  if (visualUsuario.value) {
    const res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${visualUsuario.value}/rutinas`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    if (res.ok) {
      visualRutinas.value = await res.json();
    }
  } else if (visualEntrenador.value) {
    const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${visualEntrenador.value}/rutinas`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    if (res.ok) {
      visualRutinas.value = await res.json();
    }
  } else {
    visualRutinas.value = [];
  }
  
  // Mantener los valores de filtro actuales
  if (visualGimnasio.value) {
    await cargarUsuarios(visualGimnasio.value);
    await cargarEntrenadores(visualGimnasio.value);
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
    if (visualUsuario.value) {
      const res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${visualUsuario.value}/rutinas`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (res.ok) {
        visualRutinas.value = await res.json();
      }
    } else if (visualEntrenador.value) {
      const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${visualEntrenador.value}/rutinas`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (res.ok) {
        visualRutinas.value = await res.json();
      }
    } else {
      visualRutinas.value = [];
    }
    
    // Mantener los valores de filtro actuales
    if (visualGimnasio.value) {
      await cargarUsuarios(visualGimnasio.value);
      await cargarEntrenadores(visualGimnasio.value);
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
  // Limpiar filtros al abrir el popup
  filtroNombre.value = '';
  filtroGrupoMuscular.value = '';
  filtroEquipo.value = '';
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
  // Limpiar filtros al cerrar el popup
  filtroNombre.value = '';
  filtroGrupoMuscular.value = '';
  filtroEquipo.value = '';
}



async function crearEjercicioRutina() {
  // Permitir añadir desde el popup o desde la selección normal
  const rutina = rutinaEjercicios.value || rutinaSeleccionada.value;
  if (!rutina) return;
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
  const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${emailEntrenador}/rutinas/${rutina.id}/ejerciciosRutina`, {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(dEjercicioRutina)
  });
  if (!res.ok) {
    mostrarMensaje('Error al añadir ejercicio: ' + await res.text(), 'error');
    return;
  }
  mostrarMensaje('Ejercicio añadido con éxito', 'success');
  nuevoEjercicioRutina.value = { idEjercicio: '', series: 1, repeticiones: 1, descanso: 0, indicaciones: '' };
  // Recargar ejercicios de la rutina en el popup
  if (popupEjerciciosVisible.value) {
    // Recargar los ejercicios de la rutina usando el endpoint específico y mantener el popup abierto
    const ejerciciosEnRutina = await cargarEjerciciosRutina(rutina.id);
    rutinaEjercicios.value = { ...rutina, ejerciciosEnRutina };
  }
  cargarRutinas();
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
  const rutina = rutinaEjercicios.value || rutinaSeleccionada.value;
  if (!rutina) return;
  // Eliminar sin mostrar confirmación popup
  const token = localStorage.getItem('token');
  const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${emailEntrenador}/rutinas/${rutina.id}/ejerciciosRutina/${ej.id}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  if (!res.ok) {
    mostrarMensaje('Error al eliminar ejercicio: ' + await res.text(), 'error');
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
  cargarRutinas();
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

// Funciones auxiliares para obtener información de ejercicios
function obtenerGrupoMuscular(nombreEjercicio) {
  const ejercicio = ejercicios.value.find(e => e.nombre === nombreEjercicio);
  return ejercicio ? ejercicio.grupoMuscular : 'N/A';
}

function obtenerEquipo(nombreEjercicio) {
  const ejercicio = ejercicios.value.find(e => e.nombre === nombreEjercicio);
  return ejercicio ? ejercicio.equipo : 'N/A';
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
</script>

<style>
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
</style>
