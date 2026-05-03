<template>
    <div class="container py-4">
        <AlertMessage />
        <h1 class="mb-2" style="color:#111;font-weight:bold;">Panel Administrador - Ejercicios</h1>
        <h2 class="mb-4">Gestión de Ejercicios</h2>

        <!-- Formulario de registro de ejercicio -->
        <form @submit.prevent="registrarEjercicio" class="mb-4 bg-light p-3 rounded">
            <div class="row g-2 align-items-stretch">
                <div class="col">
                    <input v-model="nuevoEjercicio.nombre" class="form-control h-100" placeholder="Nombre" required />
                </div>
                <div class="col">
                    <select v-model="nuevoEjercicio.grupoMuscular" class="form-select h-100" required>
                        <option value="" disabled>Grupo Muscular</option>
                        <option v-for="grupo in GRUPOS_MUSCULARES" :key="grupo" :value="grupo">{{ grupo }}</option>
                    </select>
                </div>
                <div class="col">
                    <select v-model="nuevoEjercicio.equipo" class="form-select h-100" required>
                        <option value="" disabled>Equipo</option>
                        <option v-for="equipo in EQUIPOS" :key="equipo" :value="equipo">{{ equipo }}</option>
                    </select>
                </div>
                <div class="col">
                    <input v-model="nuevoEjercicio.video" class="form-control h-100" placeholder="URL Video (opcional)" />
                </div>
                <div class="col d-grid">
                    <button class="button btn-primary h-100" type="submit">Registrar</button>
                </div>
            </div>
        </form>

        <!-- Filtros -->
        <form class="mb-3 bg-light p-3 rounded">
            <div class="row g-2 align-items-stretch">
                <div class="col">
                    <select v-model="filtroGrupoMuscular" class="form-select h-100">
                        <option value="">Todos los grupos musculares</option>
                        <option v-for="grupo in GRUPOS_MUSCULARES" :key="grupo" :value="grupo">{{ grupo }}</option>
                    </select>
                </div>
                <div class="col">
                    <select v-model="filtroEquipo" class="form-select h-100">
                        <option value="">Todos los equipos</option>
                        <option v-for="equipo in EQUIPOS" :key="equipo" :value="equipo">{{ equipo }}</option>
                    </select>
                </div>
                <div class="col">
                    <input v-model="filtroNombre" class="form-control h-100" placeholder="Buscar por nombre..." />
                </div>
            </div>
        </form>

        <!-- Tabla de ejercicios -->
        <table class="table table-hover bg-white rounded shadow-sm">
            <thead>
                <tr>
                    <th @click="sortBy('nombre')" style="cursor:pointer">
                        Nombre
                        <span v-if="sortKey === 'nombre'">{{ sortAsc ? '▲' : '▼' }}</span>
                    </th>
                    <th @click="sortBy('grupoMuscular')" style="cursor:pointer">
                        Grupo Muscular
                        <span v-if="sortKey === 'grupoMuscular'">{{ sortAsc ? '▲' : '▼' }}</span>
                    </th>
                    <th @click="sortBy('equipo')" style="cursor:pointer">
                        Equipo
                        <span v-if="sortKey === 'equipo'">{{ sortAsc ? '▲' : '▼' }}</span>
                    </th>
                    <th>Video</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="e in ejerciciosFiltrados" :key="e.id">
                    <td>{{ e.nombre }}</td>
                    <td>{{ e.grupoMuscular }}</td>
                    <td>{{ e.equipo }}</td>
                    <td>
                        <a v-if="e.video" href="#" @click.prevent="abrirVideo(e.video)" class="video-link">Ver video</a>
                        <span v-else>-</span>

                    </td>
                    <td>
                        <button class="btn btn-sm btn-warning me-2" @click="editarEjercicio(e)">Editar</button>
                        <button class="btn btn-sm btn-danger" @click="eliminarEjercicio(e.id)">Eliminar</button>
                    </td>
                </tr>
            </tbody>
        </table>

        <!-- Modal de edición -->
        <div v-if="ejercicioEditando" class="modal-backdrop">
            <div class="modal-content" style="max-width:400px;margin-top:200px;">
                <h4 style="color: #111">Editar Ejercicio</h4>
                <form @submit.prevent="guardarEdicion">
                    <input v-model="ejercicioEditando.nombre" class="form-control mb-2" placeholder="Nombre" required />
                    <select v-model="ejercicioEditando.grupoMuscular" class="form-select mb-2" required>
                        <option value="" disabled>Grupo Muscular</option>
                        <option v-for="grupo in GRUPOS_MUSCULARES" :key="grupo" :value="grupo">{{ grupo }}</option>
                    </select>
                    <select v-model="ejercicioEditando.equipo" class="form-select mb-2" required>
                        <option value="" disabled>Equipo</option>
                        <option v-for="equipo in EQUIPOS" :key="equipo" :value="equipo">{{ equipo }}</option>
                    </select>
                    <input v-model="ejercicioEditando.video" class="form-control mb-2" placeholder="URL Video (opcional)" />
                    <button class="btn btn-success me-2" type="submit">Guardar</button>
                    <button class="btn btn-secondary" @click="ejercicioEditando = null" type="button">Cancelar</button>
                </form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';
const { mostrarMensaje } = useAlertMessage();

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

const ejercicios = ref([]);
const nuevoEjercicio = ref({
    nombre: '', grupoMuscular: '', equipo: '', video: ''
});
const ejercicioEditando = ref(null);

// Variables para filtros y ordenamiento
const filtroGrupoMuscular = ref('');
const filtroEquipo = ref('');
const filtroNombre = ref('');
const sortKey = ref('nombre');
const sortAsc = ref(true);

// Computed para ejercicios filtrados y ordenados
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
    
    // Aplicar ordenamiento
    return filtrados.sort((a, b) => {
        let valA = a[sortKey.value];
        let valB = b[sortKey.value];
        
        if (valA < valB) return sortAsc.value ? -1 : 1;
        if (valA > valB) return sortAsc.value ? 1 : -1;
        return 0;
    });
});

onMounted(() => {
    cargarEjercicios();
});

function sortBy(key) {
    if (sortKey.value === key) {
        sortAsc.value = !sortAsc.value;
    } else {
        sortKey.value = key;
        sortAsc.value = true;
    }
}

async function cargarEjercicios() {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch('http://localhost:8080/gestiongimnasios/ejercicios/todos', {
      headers: { 'Authorization': authHeader }
    });
    let data = await res.json();
    if (!Array.isArray(data)) data = [];
    ejercicios.value = data;
}

async function registrarEjercicio() {
    const dEjercicio = {
        id: 0,
        nombre: nuevoEjercicio.value.nombre,
        grupoMuscular: nuevoEjercicio.value.grupoMuscular,
        equipo: nuevoEjercicio.value.equipo,
        video: nuevoEjercicio.value.video
    };
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch('http://localhost:8080/gestiongimnasios/ejercicios', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
        body: JSON.stringify(dEjercicio)
    });
    if (!res.ok) {
        mostrarMensaje('Error al registrar ejercicio: ' + await res.text(), 'error');
        return;
    }
    mostrarMensaje('Ejercicio registrado con éxito', 'success');
    nuevoEjercicio.value = { nombre: '', grupoMuscular: '', equipo: '', video: '' };
    cargarEjercicios();
}

function editarEjercicio(e) {
    ejercicioEditando.value = { ...e };
}

async function guardarEdicion() {
    const dEjercicio = {
        id: ejercicioEditando.value.id,
        nombre: ejercicioEditando.value.nombre,
        grupoMuscular: ejercicioEditando.value.grupoMuscular,
        equipo: ejercicioEditando.value.equipo,
        video: ejercicioEditando.value.video
    };
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/ejercicios/${dEjercicio.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
        body: JSON.stringify(dEjercicio)
    });
    if (!res.ok) {
        mostrarMensaje('Error al editar ejercicio: ' + await res.text(), 'error');
        return;
    }
    mostrarMensaje('Ejercicio editado con éxito', 'success');
    ejercicioEditando.value = null;
    cargarEjercicios();
}

async function eliminarEjercicio(id) {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/ejercicios/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': authHeader }
    });
    if (!res.ok) {
        const msg = await res.text();
        if (res.status === 409) {
            mostrarMensaje('No se puede eliminar el ejercicio porque está siendo utilizado en alguna rutina. Elimina primero las referencias en las rutinas.', 'error');
        } else {
            mostrarMensaje('Error al eliminar ejercicio: ' + msg, 'error');
        }
        return;
    }
    mostrarMensaje('Ejercicio eliminado con éxito', 'success');
    cargarEjercicios();
}

function abrirVideo(url) {
    if (!url) return;
    window.open(url, '_blank', 'noopener');
}

</script>

<style>
/* Estilos específicos de AdminEjercicio.vue */
.video-link {
    color: #0d6efd;
    text-decoration: underline;
    cursor: pointer;
}
.video-link:hover {
    color: #0a58ca;
    text-decoration: underline;
}
</style>