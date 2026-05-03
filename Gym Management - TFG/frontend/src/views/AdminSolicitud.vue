<template>
    <div class="container py-5">
        <h1 class="mb-2" style="color:#111;font-weight:bold;">Panel Administrador - Solicitudes</h1>
        <h2 class="mb-4">Gestión de Solicitudes de Reserva</h2>
        <div class="row">
            <!-- Columna izquierda: Filtros y tabla de solicitudes -->
            <div class="col-xxl-4 col-lg-5 col-md-5 pe-md-4" style="min-width: 620px; max-width: 700px; flex: 0 0 70%;">
                <h3 class="mb-3 mt-5" style="color: #111">Ver / Gestionar solicitudes</h3>
                <!-- Filtros de visualización de solicitudes -->
                <div class="mb-3 bg-light p-3 rounded">
                    <div class="mb-2">
                        <label class="form-label mb-1">Gimnasio</label>
                        <select v-model="visualGimnasio" class="form-select">
                            <option value="" disabled>Selecciona gimnasio</option>
                            <option v-for="g in gimnasios" :key="g.id" :value="g.id">{{ g.nombre }}</option>
                        </select>
                    </div>
                    <div class="row g-2 align-items-center">
                        <div class="col-md-6">
                            <label class="form-label mb-1">Filtrar por clase</label>
                            <select v-model="visualClase" class="form-select" :disabled="!visualGimnasio">
                                <option value="" disabled>Selecciona clase</option>
                                <option value="TODAS">Todas las clases</option>
                                <option v-for="c in clases" :key="c.id" :value="c.id">
                                    {{ c.nombreTipoClase }}
                                    <span v-if="c.diaSemana">
                                        | {{ diaSemanaTexto(c.diaSemana) }} | 
                                        {{ formateaHora(c.horaIni) }} - {{ formateaHora(c.horaFin) }}
                                    </span>
                                </option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label mb-1">Filtrar por usuario</label>
                            <select v-model="visualUsuario" class="form-select" :disabled="!visualGimnasio">
                                <option value="" disabled>Selecciona usuario</option>
                                <option value="TODOS">Todos los usuarios</option>
                                <option v-for="u in usuarios" :key="u.email" :value="u.email">{{ u.nombre }} ({{ u.email }})</option>
                            </select>
                        </div>
                    </div>
                </div>
                <!-- Indicador de filtro aplicado -->
                <div class="mb-2">
                    <div v-if="visualClase === 'TODAS'">
                        <div class="alert alert-info p-2 mb-0">Viendo <b>todas las solicitudes de clases</b> del gimnasio</div>
                    </div>
                    <div v-else-if="visualClase">
                        <div class="alert alert-info p-2 mb-0">Viendo solicitudes de la clase <b>{{ clases.find(c => c.id === visualClase)?.nombreTipoClase || visualClase }}</b></div>
                    </div>
                    <div v-else-if="visualUsuario === 'TODOS'">
                        <div class="alert alert-primary p-2 mb-0">Viendo <b>todas las solicitudes de usuarios</b> del gimnasio</div>
                    </div>
                    <div v-else-if="visualUsuario">
                        <div class="alert alert-primary p-2 mb-0">Viendo solicitudes del usuario <b>{{ usuarios.find(u => u.email === visualUsuario)?.nombre || visualUsuario }}</b></div>
                    </div>
                    <div v-else>
                        <div class="alert alert-info p-2 mb-0">Selecciona clase o usuario para ver sus solicitudes.</div>
                    </div>
                </div>
                <!-- Tabla de solicitudes filtradas -->
                <table v-if="visualSolicitudes.length > 0" class="table table-hover bg-white rounded shadow-sm">
                    <thead>
                        <tr>
                            <th>Fecha Solicitud</th>
                            <th>Clase</th>
                            <th>Usuario</th>
                            <th>Confirmada</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="s in visualSolicitudes" :key="s.id">
                            <td>{{ formateaFecha(s.fechaSolicitud) }}</td>
                            <td>{{ s.nombreClase }}</td>
                            <td>{{ s.emailUsuario }}</td>
                            <td>
                                <span v-if="s.confReserva" class="badge bg-success">Sí</span>
                                <span v-else class="badge bg-secondary">No</span>
                            </td>
                            <td>
                                <button v-if="!s.confReserva" class="btn btn-sm btn-success me-2" @click="confirmarSolicitud(s)">
                                    Confirmar
                                </button>
                                <button
                                    class="btn btn-sm btn-danger"
                                    @click="eliminarSolicitud(s.id)"
                                >
                                    Eliminar
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div v-else class="alert alert-warning mt-2">No hay solicitudes para este filtro.</div>
            </div>
            <!-- Columna derecha: Formulario de creación -->
            <div class="col-md-12 ms-5" style="min-width: 500px; max-width: 800px; flex: 0 0 20%; margin: 0 0 0 00px">
                <h3 class="mb-3 mt-5" style="color: #111;">Gestión de Solicitudes</h3>
                <!-- Formulario de creación de solicitud presencial -->
                <div class="mb-3 bg-light p-3 rounded">
                  <form @submit.prevent="crearSolicitud">
                    <div class="mb-2">
                      <select v-model="nuevaSolicitud.idGimnasio" class="form-select mb-2" required>
                        <option value="" disabled>Selecciona gimnasio</option>
                        <option v-for="g in gimnasios" :key="g.id" :value="g.id">{{ g.nombre }}</option>
                      </select>
                      <select v-model="nuevaSolicitud.idClase" class="form-select mb-2" :disabled="!nuevaSolicitud.idGimnasio" required>
                        <option value="" disabled>Selecciona clase</option>
                        <option v-for="c in clasesForm" :key="c.id" :value="c.id">
                          {{ c.nombreTipoClase }}
                          <span v-if="c.diaSemana">
                            | {{ diaSemanaTexto(c.diaSemana) }} | 
                            {{ formateaHora(c.horaIni) }} - {{ formateaHora(c.horaFin) }}
                          </span>
                        </option>
                      </select>
                      <select v-model="nuevaSolicitud.emailUsuario" class="form-select mb-2" :disabled="!nuevaSolicitud.idGimnasio" required>
                        <option value="" disabled>Selecciona usuario</option>
                        <option v-for="u in usuariosForm" :key="u.email" :value="u.email">{{ u.nombre }} ({{ u.email }})</option>
                      </select>
                    </div>
                    <div class="col d-grid">
                        <button class="btn btn-primary" type="submit">Registrar</button>
                    </div>
                  </form>
                </div>
            </div>
        </div>

        <!-- Modal de confirmación -->
        <div v-if="solicitudConfirmando" class="modal-backdrop">
            <div class="modal-content">
                <h4>Confirmar Solicitud</h4>
                <p>
                    ¿Seguro que quieres confirmar la solicitud con ID <b>{{ solicitudConfirmando.id }}</b>?
                </p>
                <button class="btn btn-success me-2" @click="confirmarSolicitudAPI">Confirmar</button>
                <button class="btn btn-secondary" @click="solicitudConfirmando = null">Cancelar</button>
            </div>
        </div>

        <AlertMessage />
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useGimnasioClasesUsuarios } from '../composables/useGimnasioClasesUsuarios';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';

const diasSemana = [
    { value: 'MONDAY', text: 'Lunes' },
    { value: 'TUESDAY', text: 'Martes' },
    { value: 'WEDNESDAY', text: 'Miércoles' },
    { value: 'THURSDAY', text: 'Jueves' },
    { value: 'FRIDAY', text: 'Viernes' },
    { value: 'SATURDAY', text: 'Sábado' },
    { value: 'SUNDAY', text: 'Domingo' }
];

const solicitudes = ref([]);
const solicitudConfirmando = ref(null);
const nuevaSolicitud = ref({ idGimnasio: '', idClase: '', emailUsuario: '' });
const gimnasiosForm = ref([]);
const clasesForm = ref([]);
const usuariosForm = ref([]);
const visualGimnasio = ref("");
const visualClase = ref("");
const visualUsuario = ref("");
const visualSolicitudes = ref([]);
const mensajeSolicitudCreada = ref("");
const { gimnasios, clases, usuarios, cargarGimnasios, cargarClases, cargarUsuarios } = useGimnasioClasesUsuarios();
const { mostrarMensaje } = useAlertMessage();

onMounted(() => {
    cargarGimnasios();
    cargarGimnasiosForm();
});

async function cargarGimnasiosForm() {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
        headers: { 'Authorization': authHeader }
    });
    if (res.ok) {
        gimnasiosForm.value = await res.json();
    } else {
        gimnasiosForm.value = [];
    }
}

watch(() => nuevaSolicitud.value.idGimnasio, async (nuevo, antiguo) => {
    if (nuevo === antiguo) return;
    
    nuevaSolicitud.value.idClase = '';
    nuevaSolicitud.value.emailUsuario = '';
    
    if (nuevo) {
        console.log('Cargando datos para gimnasio:', nuevo);
        await Promise.all([
            cargarClasesForm(nuevo),
            cargarUsuariosForm(nuevo)
        ]);
    } else {
        clasesForm.value = [];
        usuariosForm.value = [];
    }
});

async function cargarClasesForm(idGimnasio) {
    if (!idGimnasio) { 
        clasesForm.value = []; 
        return; 
    }
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/clasescolectivas`, {
        headers: { 'Authorization': authHeader }
    });
    if (res.ok) {
        clasesForm.value = await res.json();
        console.log('Clases cargadas para el formulario:', clasesForm.value);
    } else {
        console.error('Error al cargar clases:', await res.text());
        clasesForm.value = [];
    }
}

async function cargarUsuariosForm(idGimnasio) {
    if (!idGimnasio) { usuariosForm.value = []; return; }
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/usuarios`, {
        headers: { 'Authorization': authHeader }
    });
    if (res.ok) {
        usuariosForm.value = await res.json();
    } else {
        usuariosForm.value = [];
    }
}

watch(visualGimnasio, (nuevo) => {
    if (nuevo) {
        // Establecer filtros por defecto
        visualClase.value = "TODAS";
        visualUsuario.value = "";
        cargarClases(nuevo);
        cargarUsuarios(nuevo);
        // Cargar todas las solicitudes del gimnasio por defecto
        cargarSolicitudesGimnasio(nuevo);
    } else {
        visualClase.value = "";
        visualUsuario.value = "";
        visualSolicitudes.value = [];
    }
});

watch(visualClase, (nuevo) => {
    if (nuevo && nuevo !== "") {
        visualUsuario.value = "";
    }
});

watch(visualUsuario, (nuevo) => {
    if (nuevo && nuevo !== "") {
        visualClase.value = "";
    }
});

watch([visualClase, visualUsuario], async ([clase, usuario]) => {
    if (!visualGimnasio.value) {
        visualSolicitudes.value = [];
        return;
    }
    
    if (clase === 'TODAS') {
        // Cargar todas las solicitudes del gimnasio
        await cargarSolicitudesGimnasio(visualGimnasio.value);
        return;
    }
    
    if (clase && clase !== 'TODAS') {
        // Obtener solicitudes de la clase específica usando el endpoint específico
        try {
            const res = await fetch(`http://localhost:8080/gestiongimnasios/clasescolectivas/${clase}/solicitudes`);
            if (res.ok) {
                visualSolicitudes.value = await res.json();
            } else {
                visualSolicitudes.value = [];
            }
        } catch {
            visualSolicitudes.value = [];
        }
        return;
    }
    
    if (usuario === 'TODOS') {
        // Cargar todas las solicitudes del gimnasio
        await cargarSolicitudesGimnasio(visualGimnasio.value);
        return;
    }
    
    if (usuario && usuario !== 'TODOS') {
        // Obtener solicitudes del usuario específico usando el endpoint específico
        try {
            const res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${usuario}/solicitudes`);
            if (res.ok) {
                visualSolicitudes.value = await res.json();
            } else {
                visualSolicitudes.value = [];
            }
        } catch {
            visualSolicitudes.value = [];
        }
        return;
    }
    
    // Si no hay filtros específicos, limpiar la lista
    visualSolicitudes.value = [];
});

function diaSemanaTexto(dia) {
    const found = diasSemana.find(d => d.value === dia);
    return found ? found.text : dia;
}

function formateaHora(hora) {
    if (!hora) return '';
    hora = String(hora);
    if (/^\d{2}:\d{2}$/.test(hora)) return hora;
    if (/^\d{2}:\d{2}:\d{2}$/.test(hora)) return hora.substring(0,5);
    const match = hora.match(/(\d{2}:\d{2})/);
    return match ? match[1] : hora;
}

async function crearSolicitud() {
    const { idGimnasio, idClase, emailUsuario } = nuevaSolicitud.value;
    if (!idGimnasio || !idClase || !emailUsuario) return;
    
    try {
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/clasesColectivas/${idClase}/nuevasolicitud`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: emailUsuario
        });
        
        if (res.ok) {
            mostrarMensaje('Solicitud creada correctamente', 'success');
            nuevaSolicitud.value = { idGimnasio: '', idClase: '', emailUsuario: '' };
        } else {
            const error = await res.text();
            mostrarMensaje(`Error al crear solicitud: ${error}`, 'danger');
        }
    } catch (error) {
        mostrarMensaje(`Error de conexión: ${error.message}`, 'danger');
    }
}


function formateaFecha(fecha) {
    if (!fecha) return '';
    // Espera formato ISO yyyy-mm-dd
    return fecha.split('T')[0];
}

function confirmarSolicitud(s) {
    solicitudConfirmando.value = s;
}

async function confirmarSolicitudAPI() {
    const s = solicitudConfirmando.value;
    if (!s) return;
    // Actualiza la solicitud con confReserva = true
    const solicitudActualizada = { ...s, confReserva: true };
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/solicitudes/${s.id}`, {
        method: 'PUT',
        headers: { 
            'Content-Type': 'application/json',
            'Authorization': authHeader
        },
        body: JSON.stringify(solicitudActualizada)
    });
    if (!res.ok) {
        alert('Error al confirmar solicitud: ' + await res.text());
        return;
    }
    solicitudConfirmando.value = null;
}

async function eliminarSolicitud(id) {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    // 1. Obtener el idClase usando el endpoint
    const claseRes = await fetch(`http://localhost:8080/gestiongimnasios/solicitudes/${id}/clase`, {
        headers: { 'Authorization': authHeader }
    });
    console.log('Respuesta del endpoint clase:', claseRes);
    if (!claseRes.ok) {
        alert('No se pudo encontrar la clase asociada a la solicitud.');
        return;
    }
    const clase = await claseRes.json();
    console.log('JSON recibido de clase:', clase);
    const idClase = clase.id;
    console.log('idClase usado para eliminar:', idClase);
    // 2. Eliminar la solicitud usando el endpoint correcto
    const res = await fetch(`http://localhost:8080/gestiongimnasios/clasescolectivas/${idClase}/solicitudes/${id}`,
        {
            method: 'DELETE',
            headers: { 'Authorization': authHeader }
        });
    if (!res.ok) {
        alert('Error al eliminar solicitud: ' + await res.text());
        return;
    }
    // Refrescar la lista según el filtro activo
    if (visualUsuario.value) {
        // Si está filtrando por usuario
        const resUsuario = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${visualUsuario.value}/solicitudes`);
        if (resUsuario.ok) {
            visualSolicitudes.value = await resUsuario.json();
        } else {
            visualSolicitudes.value = [];
        }
    } else if (visualClase.value) {
        // Si está filtrando por clase
        const resClase = await fetch(`http://localhost:8080/gestiongimnasios/clasescolectivas/${visualClase.value}/solicitudes`);
        if (resClase.ok) {
            visualSolicitudes.value = await resClase.json();
        } else {
            visualSolicitudes.value = [];
        }
    } else if (visualGimnasio.value) {
        // Si está filtrando por gimnasio
        cargarSolicitudesGimnasio(visualGimnasio.value);
    }
}

async function cargarSolicitudesGimnasio(idGimnasio) {
    try {
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/solicitudes`, {
            headers: { 'Authorization': authHeader }
        });
        if (res.ok) {
            visualSolicitudes.value = await res.json();
            console.log('Solicitudes del gimnasio cargadas:', visualSolicitudes.value);
        } else {
            console.error('Error al cargar solicitudes del gimnasio:', await res.text());
            visualSolicitudes.value = [];
        }
    } catch (error) {
        console.error('Error al cargar solicitudes del gimnasio:', error);
        visualSolicitudes.value = [];
    }
}
</script>

<style>
/* Estilos específicos de AdminSolicitud.vue */
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