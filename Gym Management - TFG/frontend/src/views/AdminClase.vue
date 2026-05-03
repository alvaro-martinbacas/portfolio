<template>
    <div class="container py-4">
        <AlertMessage />
        <h1 class="mb-2" style="color:#111;font-weight:bold;">Panel Administrador - Clases</h1>
        <div class="row">
            <!-- Tipos de Clase -->
        <div class="col pe-md-4 border-end">
                <h3 class="mb-3">Gestión de Tipos de Clase</h3>
                <form @submit.prevent="crearTipoClase" class="mb-3 bg-light p-3 rounded">
                    <div class="mb-2">
                        <input v-model="nuevoTipoClase.nombre" class="form-control mb-2" placeholder="Nombre" required />
                        <input v-model="nuevoTipoClase.descripcion" class="form-control mb-2" placeholder="Descripción" required />
                        <input v-model.number="nuevoTipoClase.maxPlazas" class="form-control mb-2" placeholder="Máx. plazas" min="1" required />
                    </div>
                    <button class="btn btn-primary" type="submit" style="width: 30%;">Registrar</button>
                </form>
                <table class="table table-hover bg-white rounded shadow-sm">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Descripción</th>
                            <th>Máx. Plazas</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="tipo in tiposClase" :key="tipo.nombre">
                            <td>{{ tipo.nombre }}</td>
                            <td>{{ tipo.descripcion }}</td>
                            <td>{{ tipo.maxPlazas }}</td>
                            <td class="text-nowrap">
                                <div class="d-inline-flex align-items-center" style="gap: 0.5rem;">
                                    <button class="btn btn-sm btn-warning align-middle" @click="editarTipoClase(tipo)">Editar</button>
                                    <button class="btn btn-sm btn-danger align-middle" @click="eliminarTipoClase(tipo.nombre)">Eliminar</button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <!-- Modal edición tipo clase -->
                <div v-if="tipoClaseEditando" class="modal-backdrop">
                    <div class="modal-content">
                        <h4 style="color: #111;">Editar Tipo de Clase</h4>
                        <form @submit.prevent="guardarEdicionTipoClase">
                            <input v-model="tipoClaseEditando.descripcion" class="form-control mb-2" placeholder="Descripción" required />
                            <input v-model.number="tipoClaseEditando.maxPlazas" type="number" min="1" class="form-control mb-2" placeholder="Máx. plazas" required />
                            <button class="btn btn-success me-2" type="submit">Guardar</button>
                            <button class="btn btn-secondary" @click="tipoClaseEditando = null" type="button">Cancelar</button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Clases Colectivas -->
            <div class="col-md-7 ps-md-4">
                <h3 class="mb-3">Gestión de Clases Colectivas</h3>
                <form @submit.prevent="crearClaseColectiva" class="mb-3 bg-light p-3 rounded">
                    <div class="mb-2">
                        <select v-model="nuevaClaseColectiva.idGimnasio" class="form-select mb-2" required>
                            <option value="" disabled>Selecciona gimnasio</option>
                            <option v-for="g in gimnasios" :key="g.id" :value="g.id">{{ g.nombre }}</option>
                        </select>
                        <select v-model="nuevaClaseColectiva.nombreTipoClase" class="form-select mb-2" required>
                            <option value="" disabled>Selecciona tipo de clase</option>
                            <option v-for="t in tiposClase" :key="t.nombre" :value="t.nombre">{{ t.nombre }}</option>
                        </select>
                        <select v-model="nuevaClaseColectiva.diaSemana" class="form-select mb-2" required>
                            <option value="" disabled>Día de la semana</option>
                            <option v-for="d in diasSemana" :key="d.value" :value="d.value">{{ d.text }}</option>
                        </select>
                        <input v-model="nuevaClaseColectiva.horaIni" type="time" class="form-control mb-2" placeholder="Hora inicio" required />
                        <input v-model="nuevaClaseColectiva.horaFin" type="time" class="form-control mb-2" placeholder="Hora fin" required />
                    </div>
                    <div class="col d-grid">
                        <button class="btn btn-primary" type="submit" style="width: 30%;">Registrar</button>
                    </div>
                </form>
                <table class="table table-hover bg-white rounded shadow-sm align-middle w-100">
                    <colgroup>
                        <col style="width: 15%">
                        <col style="width: 13%">
                        <col style="width: 10%">
                        <col style="width: 17%">
                        <col style="width: 13%">
                        <col style="width: 40%">
                    </colgroup>
                    <thead>
                        <tr>
                            <th>Gimnasio</th>
                            <th>Tipo</th>
                            <th>Día</th>
                            <th>Hora</th>
                            <th>Entrenador</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="clase in clasesColectivas" :key="clase.id">
                            <td>{{ gimnasioNombre(clase.idGimnasio) }}</td>
                            <td>{{ clase.nombreTipoClase }}</td>
                            <td>{{ diaSemanaTexto(clase.diaSemana) }}</td>
                            <td class="text-nowrap">{{ formateaHora(clase.horaIni) }} - {{ formateaHora(clase.horaFin) }}</td>

                            <td>{{ clase.nombreEntrenador ? clase.nombreEntrenador : '-' }}</td>
                            <td class="text-nowrap">
                                <div class="d-inline-flex align-items-center" style="gap: 0.5rem;">
                                    <button class="btn btn-sm btn-info align-middle" style="min-width: 110px;" @click="abrirModalAsignarEntrenador(clase)">Entrenador</button>
                                    <button class="btn btn-sm btn-warning align-middle" @click="editarClaseColectiva(clase)">Editar</button>
                                    <button class="btn btn-sm btn-danger align-middle" @click="eliminarClaseColectiva(clase.id, clase.idGimnasio)">Eliminar</button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <!-- Modal edición clase colectiva -->
                <div v-if="claseColectivaEditando" class="modal-backdrop">
                    <div class="modal-content">
                        <h4 style="color: #111;">Editar Clase Colectiva</h4>
                        <form @submit.prevent="guardarEdicionClaseColectiva">
                            <select v-model="claseColectivaEditando.diaSemana" class="form-select mb-2" required>
                                <option v-for="d in diasSemana" :key="d.value" :value="d.value">{{ d.text }}</option>
                            </select>
                            <input v-model="claseColectivaEditando.horaIni" type="time" class="form-control mb-2" required />
                            <input v-model="claseColectivaEditando.horaFin" type="time" class="form-control mb-2" required />
                            <button class="btn btn-success me-2" type="submit">Guardar</button>
                            <button class="btn btn-secondary" @click="claseColectivaEditando = null" type="button">Cancelar</button>
                        </form>
                    </div>
                </div>

                <!-- Modal asignar/cambiar entrenador -->
                <div v-if="modalAsignarEntrenador" class="modal-backdrop">
                    <div class="modal-content">
                        <h4 style="color: #111;">Asignar/Cambiar Entrenador</h4>
                        <div v-if="claseAsignarEntrenador && claseAsignarEntrenador.nombreEntrenador" class="alert alert-info p-2 mb-2">
                            <strong>Entrenador actual:</strong> {{ claseAsignarEntrenador.nombreEntrenador }}
                        </div>
                        <form @submit.prevent="asignarEntrenadorClase">
                            <select v-model="entrenadorSeleccionado" class="form-select mb-3" required>
                                <option value="" disabled>Selecciona entrenador</option>
                                <option v-for="e in entrenadoresDisponibles" :key="e.email" :value="e.email">{{ e.nombre }} ({{ e.email }})</option>
                            </select>
                            <button class="btn btn-success me-2" type="submit">Asignar</button>
                            <button class="btn btn-secondary" @click="cerrarModalAsignarEntrenador" type="button">Cancelar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>


<script setup>
import { ref, onMounted } from 'vue';
import AlertMessage from '../components/AlertMessage.vue';
import { useAlertMessage } from '../composables/useAlertMessage';

const { mostrarMensaje } = useAlertMessage();

const tiposClase = ref([]);
const tipoClaseEditando = ref(null);
const nuevoTipoClase = ref({ nombre: '', descripcion: '', maxPlazas: null });

const gimnasios = ref([]);
const clasesColectivas = ref([]);
const claseColectivaEditando = ref(null);
const nuevaClaseColectiva = ref({
    idGimnasio: '',
    nombreTipoClase: '',
    diaSemana: '',
    horaIni: '',
    horaFin: ''
});

// --- Asignar entrenador ---
const modalAsignarEntrenador = ref(false);
const claseAsignarEntrenador = ref(null);
const entrenadoresDisponibles = ref([]);
const entrenadorSeleccionado = ref("");

const diasSemana = [
    { value: 'MONDAY', text: 'Lunes' },
    { value: 'TUESDAY', text: 'Martes' },
    { value: 'WEDNESDAY', text: 'Miércoles' },
    { value: 'THURSDAY', text: 'Jueves' },
    { value: 'FRIDAY', text: 'Viernes' },
    { value: 'SATURDAY', text: 'Sábado' },
    { value: 'SUNDAY', text: 'Domingo' }
];

function diaSemanaTexto(dia) {
    const found = diasSemana.find(d => d.value === dia);
    return found ? found.text : dia;
}

function gimnasioNombre(idGimnasio) {
    const g = gimnasios.value.find(g => g.id === idGimnasio);
    return g ? g.nombre : '-';
}

onMounted(() => {
    cargarTiposClase();
    cargarGimnasios();
    cargarClasesColectivas();
});

// --- Tipos de Clase ---
async function cargarTiposClase() {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch('http://localhost:8080/gestiongimnasios/tiposclases/todos', {
      headers: { 'Authorization': authHeader }
    });
    if (res.ok) {
        tiposClase.value = await res.json();
    } else {
        tiposClase.value = [];
    }
}

async function crearTipoClase() {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch('http://localhost:8080/gestiongimnasios/tiposclases', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
        body: JSON.stringify(nuevoTipoClase.value)
    });
    if (!res.ok) {
        mostrarMensaje('Error al crear tipo de clase: ' + await res.text(), 'danger');
        return;
    }
    mostrarMensaje('Tipo de clase creado con éxito', 'success');
    nuevoTipoClase.value = { nombre: '', descripcion: '', maxPlazas: null };
    cargarTiposClase();
}

function editarTipoClase(tipo) {
    tipoClaseEditando.value = { ...tipo };
}

async function guardarEdicionTipoClase() {
    const tipo = tipoClaseEditando.value;
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/tiposclases/${tipo.nombre}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
        body: JSON.stringify(tipo)
    });
    if (!res.ok) {
        mostrarMensaje('Error al editar tipo de clase: ' + await res.text(), 'danger');
        return;
    }
    mostrarMensaje('Tipo de clase editado con éxito', 'success');
    tipoClaseEditando.value = null;
    cargarTiposClase();
}

async function eliminarTipoClase(nombre) {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/tiposclases/${nombre}`, {
        method: 'DELETE',
        headers: { 'Authorization': authHeader }
    });
    if (!res.ok) {
        mostrarMensaje('Error al eliminar tipo de clase: ' + await res.text(), 'danger');
        return;
    }
    mostrarMensaje('Tipo de clase eliminado con éxito', 'success');
    cargarTiposClase();
}

// --- Gimnasios y Clases Colectivas ---
async function cargarGimnasios() {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
      headers: { 'Authorization': authHeader }
    });
    gimnasios.value = await res.json();
}

async function cargarClasesColectivas() {
    clasesColectivas.value = [];
    // Para cada gimnasio, carga sus clases colectivas
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const resGim = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
      headers: { 'Authorization': authHeader }
    });
    const gimList = await resGim.json();
    for (const g of gimList) {
        const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${g.id}/clasescolectivas`, {
          headers: { 'Authorization': authHeader }
        });
        if (res.ok) {
            const data = await res.json();
            for (const c of data) {
                clasesColectivas.value.push({ ...c, idGimnasio: g.id });
            }
        }
    }
}

async function crearClaseColectiva() {
    const dClase = {
        id: 0,
        diaSemana: nuevaClaseColectiva.value.diaSemana,
        horaIni: nuevaClaseColectiva.value.horaIni,
        horaFin: nuevaClaseColectiva.value.horaFin,
        nombreTipoClase: nuevaClaseColectiva.value.nombreTipoClase
    };

    try {
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${nuevaClaseColectiva.value.idGimnasio}/clasescolectivas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
            body: JSON.stringify(dClase)
        });

        const responseData = await res.json();
        
        if (responseData.error) {
            mostrarMensaje('Error al crear clase colectiva: ' + responseData.error, 'danger');
            return;
        }

        mostrarMensaje('Clase colectiva creada con éxito', 'success');
        nuevaClaseColectiva.value = { idGimnasio: '', nombreTipoClase: '', diaSemana: '', horaIni: '', horaFin: '' };
        cargarClasesColectivas();
    } catch (error) {
        mostrarMensaje('Error de conexión al servidor', 'danger');
    }
}

function editarClaseColectiva(clase) {
    claseColectivaEditando.value = { ...clase, emailEntrenador: clase.entrenador && clase.entrenador.email ? clase.entrenador.email : '' };
    cargarEntrenadoresParaClase(clase.idGimnasio);
}

async function guardarEdicionClaseColectiva() {
    const clase = claseColectivaEditando.value;
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/clasescolectivas/${clase.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
        body: JSON.stringify(clase)
    });
    if (!res.ok) {
        mostrarMensaje('Error al editar clase colectiva: ' + await res.text(), 'error');
        return;
    }
    mostrarMensaje('Clase colectiva editada con éxito', 'success');
    claseColectivaEditando.value = null;
    cargarClasesColectivas();
}

async function eliminarClaseColectiva(id, idGimnasio) {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/clasescolectivas/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': authHeader }
    });
    if (!res.ok) {
        mostrarMensaje('Error al eliminar clase colectiva: ' + await res.text(), 'error');
        return;
    }
    mostrarMensaje('Clase colectiva eliminada con éxito', 'success');
    cargarClasesColectivas();
}

// --- Asignar/Cambiar Entrenador ---
function abrirModalAsignarEntrenador(clase) {
    claseAsignarEntrenador.value = clase;
    entrenadorSeleccionado.value = clase.entrenador && clase.entrenador.email ? clase.entrenador.email : '';
    cargarEntrenadoresParaClase(clase.idGimnasio);
    modalAsignarEntrenador.value = true;
}

function cerrarModalAsignarEntrenador() {
    modalAsignarEntrenador.value = false;
    claseAsignarEntrenador.value = null;
    entrenadorSeleccionado.value = "";
    entrenadoresDisponibles.value = [];
}

function formateaHora(hora) {
    if (!hora) return '';
    // Si está en formato HH:mm
    if (/^\d{2}:\d{2}$/.test(hora)) return hora;
    // Si HH:mm:ss
    if (/^\d{2}:\d{2}:\d{2}$/.test(hora)) return hora.substring(0,5);
    // Si viene como string con fecha y hora
    const match = hora.match(/(\d{2}:\d{2})/);
    return match ? match[1] : hora;
}

async function cargarEntrenadoresParaClase(idGimnasio) {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/entrenadores`, {
      headers: { 'Authorization': authHeader }
    });
    if (res.ok) {
        const lista = await res.json();
        entrenadoresDisponibles.value = lista.filter(e => e.activo);
    } else {
        entrenadoresDisponibles.value = [];
    }
}

async function asignarEntrenadorClase() {
    if (!entrenadorSeleccionado.value) {
        mostrarMensaje('Selecciona un entrenador', 'error');
        return;
    }
    const idClase = claseAsignarEntrenador.value.id;
    // primero es necesario quitar el entrenador si hay uno
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    if (claseAsignarEntrenador.value.nombreEntrenador) {
        const resQuitar = await fetch(`http://localhost:8080/gestiongimnasios/clasescolectivas/${idClase}/quitar`, {
            method: 'PUT',
            headers: { 'Authorization': authHeader }
        });
        if (!resQuitar.ok) {
            mostrarMensaje('Error al quitar entrenador actual: ' + await resQuitar.text(), 'error');
            return;
        }
    }
    const res = await fetch(`http://localhost:8080/gestiongimnasios/clasescolectivas/${idClase}/asignar`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
        body: entrenadorSeleccionado.value
    });
    if (!res.ok) {
        mostrarMensaje('Error al asignar entrenador: ' + await res.text(), 'error');
        return;
    }
    mostrarMensaje('Entrenador asignado con éxito', 'success');
    cerrarModalAsignarEntrenador();
    cargarClasesColectivas();
}
</script>

<style>
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