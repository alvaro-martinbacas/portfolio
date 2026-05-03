<template>
    <div class="container py-4">
        <h1 class="mb-2" style="color:#111;font-weight:bold;">Panel Administrador - Entrenadores</h1>
        <h2 class="mb-4">Gestión de Entrenadores</h2>

        <!-- Formulario de registro de entrenador -->
        <form @submit.prevent="registrarEntrenador" class="mb-4 bg-light p-3 rounded">
            <div class="row g-2 mt-2">
                <div class="col">
                    <input 
                        v-model="nuevoEntrenador.nombre" 
                        class="form-control" 
                        placeholder="Nombre y Apellidos" 
                        required 
                    />
                </div>
                <div class="col">
                    <input 
                        v-model="nuevoEntrenador.tlf" 
                        class="form-control" 
                        placeholder="Teléfono" 
                        required 
                    />
                </div>
                <div class="col">
                    <input 
                        v-model="nuevoEntrenador.clave" 
                        class="form-control" 
                        placeholder="Contraseña" 
                        type="password" 
                        autocomplete="new-password"
                        required 
                    />
                </div>
                <div class="col">
                    <select v-model="nuevoEntrenador.idGimnasio" class="form-select" required>
                        <option value="" disabled>Gimnasio</option>
                        <option v-for="g in gimnasios" :key="g.id" :value="g.id">{{ g.nombre }}</option>
                    </select>
                </div>
                <div class="col">
                    <button class="btn btn-primary w-100" type="submit">Registrar</button>
                </div>
            </div>
            <div class="row g-1 mt-2">
                <div class="col-6">
                    <div class="input-group">
                        <input 
                            v-model="nuevoEntrenador.username" 
                            class="form-control" 
                            placeholder="Usuario (email)" 
                            type="text"
                            pattern="[a-zA-Z0-9._-]+" 
                            title="Solo letras, números, puntos, guiones y guiones bajos"
                            required 
                        />
                        <span class="mt-1 ms-2">@entrenador.com</span>
                    </div>
                </div>
            </div>
        </form>

        <!-- Tabla de entrenadores -->
        <table class="table table-hover bg-white rounded shadow-sm">
            <thead>
                <tr>
                    <th @click="sortBy('nombre')" style="cursor:pointer">
                        Nombre
                        <span v-if="sortKey === 'nombre'">{{ sortAsc ? '▲' : '▼' }}</span>
                    </th>
                    <th @click="sortBy('email')" style="cursor:pointer">
                        Email
                        <span v-if="sortKey === 'email'">{{ sortAsc ? '▲' : '▼' }}</span>
                    </th>
                    <th @click="sortBy('tlf')" style="cursor:pointer">
                        Teléfono
                        <span v-if="sortKey === 'tlf'">{{ sortAsc ? '▲' : '▼' }}</span>
                    </th>
                    <th @click="sortBy('gimnasio')" style="cursor:pointer">
                        Gimnasio
                        <span v-if="sortKey === 'gimnasio'">{{ sortAsc ? '▲' : '▼' }}</span>
                    </th>
                    <th @click="sortBy('activo')" style="cursor:pointer">
                        Activo
                        <span v-if="sortKey === 'activo'">{{ sortAsc ? '▲' : '▼' }}</span>
                    </th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="e in sortedEntrenadores" :key="e.email">
                    <td>{{ e.nombre }}</td>
                    <td>{{ e.email }}</td>
                    <td>{{ e.tlf }}</td>
                    <td>{{ gimnasioNombre(e.email) }}</td>
                    <td>
                        <span v-if="e.activo === true || e.activo === 'true'" class="badge bg-success">Sí</span>
                        <span v-else class="badge bg-danger">No</span>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-warning me-2" @click="editarEntrenador(e)">Editar</button>
                        <button v-if="e.activo === true || e.activo === 'true'" class="btn btn-sm btn-secondary me-2" @click="desactivarEntrenador(e.email)">Desactivar</button>
                        <button v-else class="btn btn-sm btn-success me-2" @click="activarEntrenador(e.email)">Activar</button>
                    </td>
                </tr>
            </tbody>
        </table>

        <!-- Modal de edición -->
        <div v-if="entrenadorEditando" class="modal-backdrop">
            <div class="modal-content" style="max-width:400px;margin:auto;">
                <h4 style="color: #111;">Editar Entrenador</h4>
                <form @submit.prevent="guardarEdicion">
                    <input v-model="entrenadorEditando.nombre" class="form-control mb-2" placeholder="Nombre" required />
                    <input v-model="entrenadorEditando.tlf" class="form-control mb-2" placeholder="Teléfono" required />
                    <input 
                        v-model="entrenadorEditando.clave" 
                        class="form-control mb-2" 
                        placeholder="Contraseña" 
                        type="password"
                        autocomplete="new-password"
                    />
                    <button class="btn btn-success me-2" type="submit">Guardar</button>
                    <button class="btn btn-secondary" @click="entrenadorEditando = null" type="button">Cancelar</button>
                </form>
            </div>
        </div>
    </div>
    <AlertMessage />
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';

const { mostrarMensaje } = useAlertMessage();

const entrenadores = ref([]);
const sortKey = ref('nombre');
const sortAsc = ref(true);
const sortedEntrenadores = computed(() => {
    return [...entrenadores.value].sort((a, b) => {
        let valA, valB;
        if (sortKey.value === 'gimnasio') {
            valA = gimnasioNombre(a.email);
            valB = gimnasioNombre(b.email);
        } else if (sortKey.value === 'activo') {
            valA = String(a.activo).toLowerCase() === 'true' ? 1 : 0;
            valB = String(b.activo).toLowerCase() === 'true' ? 1 : 0;
        } else {
            valA = a[sortKey.value];
            valB = b[sortKey.value];
        }
        if (valA < valB) return sortAsc.value ? -1 : 1;
        if (valA > valB) return sortAsc.value ? 1 : -1;
        return 0;
    });
});

function sortBy(key) {
    if (sortKey.value === key) {
        sortAsc.value = !sortAsc.value;
    } else {
        sortKey.value = key;
        sortAsc.value = true;
    }
}
const gimnasios = ref([]);
const nuevoEntrenador = ref({
    nombre: '', username: '', tlf: '', clave: '', idGimnasio: ''
});

// Función para generar sugerencia de email
const generarSugerenciaEmail = (nombreCompleto) => {
    if (!nombreCompleto) return '';
    const partes = nombreCompleto.trim().toLowerCase().split(' ');
    if (partes.length < 2) return '';
    const inicial = partes[0].charAt(0);
    let apellido = partes[partes.length - 1];
    if (partes.length > 3) apellido = partes[2]
    else apellido = partes[1];

    return (inicial + apellido).replace(/[áéíóúñ]/g, c => {
        const conversiones = { 'á': 'a', 'é': 'e', 'í': 'i', 'ó': 'o', 'ú': 'u', 'ñ': 'n' };
        return conversiones[c] || c;
    });
};

// Watch para actualizar el email cuando cambie el nombre
watch(() => nuevoEntrenador.value.nombre, (newNombre) => {
    nuevoEntrenador.value.username = generarSugerenciaEmail(newNombre);
});

const entrenadorEditando = ref(null);
const entrenadorGimnasios = ref({}); // email -> gimnasio

onMounted(() => {
    cargarGimnasios();
    cargarEntrenadores();
});

async function cargarGimnasios() {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
      headers: { 'Authorization': authHeader }
    });
    gimnasios.value = await res.json();
}

async function cargarEntrenadores() {
    entrenadores.value = [];
    entrenadorGimnasios.value = {};
    // Para cada gimnasio, carga sus entrenadores
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const resGim = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
      headers: { 'Authorization': authHeader }
    });
    const gimList = await resGim.json();
    for (const g of gimList) {
        const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${g.id}/entrenadores`, {
          headers: { 'Authorization': authHeader }
        });
        if (res.ok) {
            const data = await res.json();
            for (const e of data) {
                entrenadores.value.push(e);
                entrenadorGimnasios.value[e.email] = g.nombre;
            }
        }
    }
}

function gimnasioNombre(email) {
    return entrenadorGimnasios.value[email] || '-';
}

async function registrarEntrenador() {
    if (!nuevoEntrenador.value.idGimnasio) {
        mostrarMensaje('Selecciona un gimnasio', 'danger');
        return;
    }

    // Validar el nombre de usuario
    if (!/^[a-zA-Z0-9._-]+$/.test(nuevoEntrenador.value.username)) {
        mostrarMensaje('El nombre de usuario solo puede contener letras, números, puntos, guiones y guiones bajos', 'danger');
        return;
    }

    const email = `${nuevoEntrenador.value.username}@entrenador.com`;

    const dEntrenador = {
        nombre: nuevoEntrenador.value.nombre,
        email: email,
        clave: nuevoEntrenador.value.clave,
        tlf: nuevoEntrenador.value.tlf,
        activo: true
    };
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${nuevoEntrenador.value.idGimnasio}/nuevoentrenador`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
        body: JSON.stringify(dEntrenador)
    });
    if (!res.ok) {
        mostrarMensaje('Error al registrar entrenador: ' + await res.text(), 'danger');
        return;
    }
    mostrarMensaje('Entrenador registrado correctamente', 'success');
    nuevoEntrenador.value = { nombre: '', username: '', tlf: '', clave: '', idGimnasio: '' };
    cargarEntrenadores();
}

function editarEntrenador(e) {
    entrenadorEditando.value = { ...e };
}

async function guardarEdicion() {
    const email = entrenadorEditando.value.email;
    const dEntrenador = {
        nombre: entrenadorEditando.value.nombre,
        email: entrenadorEditando.value.email,
        clave: entrenadorEditando.value.clave,
        tlf: entrenadorEditando.value.tlf,
        activo: entrenadorEditando.value.activo
    };
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${email}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
        body: JSON.stringify(dEntrenador)
    });
    if (!res.ok) {
        mostrarMensaje('Error al editar entrenador: ' + await res.text(), 'danger');
        return;
    }
    mostrarMensaje('Entrenador editado correctamente', 'success');
    entrenadorEditando.value = null;
    cargarEntrenadores();
}

async function activarEntrenador(email) {
    const nombreGim = entrenadorGimnasios.value[email];
    if (!nombreGim) {
        mostrarMensaje('No se pudo determinar el gimnasio del entrenador', 'danger');
        return;
    }
    const gim = gimnasios.value.find(g => g.nombre === nombreGim);
    if (!gim) {
        mostrarMensaje('No se pudo determinar el gimnasio del entrenador', 'danger');
        return;
    }
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${gim.id}/entrenadores/${email}/activar`, {
        method: 'PUT',
        headers: { 'Authorization': authHeader }
    });
    if (!res.ok) {
        mostrarMensaje('Error al activar entrenador: ' + await res.text(), 'danger');
        return;
    }
    mostrarMensaje('Entrenador activado correctamente', 'success');
    cargarEntrenadores();
}

async function eliminarEntrenador(email) {
    const nombreGim = entrenadorGimnasios.value[email];
    if (!nombreGim) {
        alert('No se pudo determinar el gimnasio del entrenador');
        return;
    }
    if (!confirm('¿Seguro que quieres eliminar este entrenador?')) return;
    const gim = gimnasios.value.find(g => g.nombre === nombreGim);
    if (!gim) {
        alert('No se pudo determinar el gimnasio del entrenador');
        return;
    }
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${gim.id}/entrenadores/${email}`, {
        method: 'DELETE',
        headers: { 'Authorization': authHeader }
    });
    if (!res.ok) {
        alert('Error al eliminar entrenador: ' + await res.text());
        return;
    }
    cargarEntrenadores();
}

async function desactivarEntrenador(email) {
    const nombreGim = entrenadorGimnasios.value[email];
    if (!nombreGim) {
        mostrarMensaje('No se pudo determinar el gimnasio del entrenador', 'danger');
        return;
    }
    const gim = gimnasios.value.find(g => g.nombre === nombreGim);
    if (!gim) {
        mostrarMensaje('No se pudo determinar el gimnasio del entrenador', 'danger');
        return;
    }
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${gim.id}/entrenadores/${email}/desactivar`, {
        method: 'PUT',
        headers: { 'Authorization': authHeader }
    });
    if (!res.ok) {
        mostrarMensaje('Error al desactivar entrenador: ' + await res.text(), 'danger');
        return;
    }
    mostrarMensaje('Entrenador desactivado correctamente', 'success');
    cargarEntrenadores();
}

</script>

<style>
/* No quedan estilos específicos, todo está en global.css */
</style>