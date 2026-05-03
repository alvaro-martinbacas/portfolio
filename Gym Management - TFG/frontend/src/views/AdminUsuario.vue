<template>
  <div class="container py-4">
    <h1 class="mb-2" style="color:#111;font-weight:bold;">Panel Administrador - Usuarios</h1>
    <h2 class="mb-4">Gestión de Usuarios</h2>

    <!-- Formulario de registro de usuario -->
    <form @submit.prevent="registrarUsuario" class="mb-4 bg-light p-3 rounded">
      <div class="row g-2 mt-2">
        <div class="col">
          <input v-model="nuevoUsuario.nombre" class="form-control" placeholder="Nombre" required />
        </div>
        <div class="col">
          <input v-model="nuevoUsuario.apellidos" class="form-control" placeholder="Apellidos" required />
        </div>
        <div class="col">
          <input v-model="nuevoUsuario.email" class="form-control" placeholder="Email" type="email" required />
        </div>
        <div class="col">
          <input v-model="nuevoUsuario.clave" class="form-control" placeholder="Contraseña" type="password" required />
        </div>
        <div class="col">
          <input v-model="nuevoUsuario.tlf" class="form-control" placeholder="Teléfono" required />
        </div>
        <div class="col">
          <select v-model="nuevoUsuario.idGimnasio" class="form-select" required>
            <option value="" disabled>Gimnasio</option>
            <option v-for="g in gimnasios" :key="g.id" :value="g.id">{{ g.nombre }}</option>
          </select>
        </div>
        <div class="col">
          <button class="btn btn-primary w-100" type="submit">Registrar</button>
        </div>
      </div>
    </form>
    <!-- 

    Descomentar para pruebas!!

    <div class="col-auto ms-auto mb-4">
      <button type="button" class="btn btn-info" @click="generarUsuariosPrueba" :disabled="!nuevoUsuario.idGimnasio">
        Generar 10 Usuarios de Prueba (Selecciona gimnasio antes en el form de arriba)
      </button>
    </div>
     -->
    <!-- Tabla de usuarios -->
    <table class="table table-hover bg-white rounded shadow-sm">
      <thead>
        <tr>
          <th @click="sortBy('nombre')" style="cursor:pointer">
            Nombre
            <span v-if="sortKey === 'nombre'">{{ sortAsc ? '▲' : '▼' }}</span>
          </th>
          <th @click="sortBy('apellidos')" style="cursor:pointer">
            Apellidos
            <span v-if="sortKey === 'apellidos'">{{ sortAsc ? '▲' : '▼' }}</span>
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
          <th @click="sortBy('cuotaPagada')" style="cursor:pointer">
            Cuota pagada
            <span v-if="sortKey === 'cuotaPagada'">{{ sortAsc ? '▲' : '▼' }}</span>
          </th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="u in sortedUsuarios" :key="u.email">
          <td>{{ u.nombre }}</td>
          <td>{{ u.apellidos }}</td>
          <td>{{ u.email }}</td>
          <td>{{ u.tlf }}</td>
          <td>{{ gimnasioNombre(u.email) }}</td>
          <td>
            <span v-if="u.cuotaPagada === true || u.cuotaPagada === 'true' " class="badge bg-success">Sí</span>
            <span v-else class="badge bg-danger">No</span>
          </td>
          <td>
            <button class="btn btn-sm btn-success me-2" @click="marcarCuotaPagada(u.email)">Marcar cuota pagada</button>
            <button class="btn btn-sm btn-warning me-2" @click="editarUsuario(u)">Editar</button>
            <button class="btn btn-sm btn-danger" @click="eliminarUsuario(u.email)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Modal de edición -->
    <div v-if="usuarioEditando" class="modal-backdrop">
      <div class="modal-content" style="max-width:400px;margin:auto;">
        <h4 style="color: black">Editar Usuario</h4>
        <form @submit.prevent="guardarEdicion">
          <input v-model="usuarioEditando.nombre" class="form-control mb-2" placeholder="Nombre" required />
          <input v-model="usuarioEditando.apellidos" class="form-control mb-2" placeholder="Apellidos" required />
          <input v-model="usuarioEditando.tlf" class="form-control mb-2" placeholder="Teléfono" required />
          <input v-model="usuarioEditando.clave" class="form-control mb-2" placeholder="Contraseña" type="password" />
          <button class="btn btn-success me-2" type="submit">Guardar</button>
          <button class="btn btn-secondary" @click="usuarioEditando = null" type="button">Cancelar</button>
        </form>
      </div>
    </div>
    <AlertMessage />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';

const { mostrarMensaje } = useAlertMessage();

const usuarios = ref([]);
const gimnasios = ref([]);
const nuevoUsuario = ref({
  nombre: '', apellidos: '', email: '', tlf: '', clave: '', idGimnasio: ''
});
const usuarioEditando = ref(null);
const usuarioGimnasios = ref({}); // email -> gimnasio
const sortKey = ref('nombre');
const sortAsc = ref(true);
const sortedUsuarios = computed(() => {
  return [...usuarios.value].sort((a, b) => {
    let valA, valB;
    if (sortKey.value === 'gimnasio') {
      valA = gimnasioNombre(a.email);
      valB = gimnasioNombre(b.email);
    } else if (sortKey.value === 'cuotaPagada') {
      valA = String(a.cuotaPagada).toLowerCase() === 'true' ? 1 : 0;
      valB = String(b.cuotaPagada).toLowerCase() === 'true' ? 1 : 0;
    } else {
      valA = a[sortKey.value];
      valB = b[sortKey.value];
    }
    if (valA < valB) return sortAsc.value ? -1 : 1;
    if (valA > valB) return sortAsc.value ? 1 : -1;
    return 0;
  });
});

// Cargar gimnasios y usuarios al montar
onMounted(() => {
  cargarGimnasios();
  cargarUsuarios();
});

async function cargarGimnasios() {
  const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos');
  gimnasios.value = await res.json();
}

async function cargarUsuarios() {
  try {
    // Cargar todos los usuarios
    const res = await fetch('http://localhost:8080/gestiongimnasios/usuarios/todos');
    if (!res.ok) {
      console.error('Error al cargar usuarios:', await res.text());
      usuarios.value = [];
      return;
    }
    let data = await res.json();
    usuarios.value = Array.isArray(data) ? data : [];

    // Cargar el gimnasio de cada usuario
    for (const usuario of usuarios.value) {
      try {
        const resGim = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${usuario.email}/gimnasio`);
        if (resGim.ok) {
          const gimnasio = await resGim.json();
          usuarioGimnasios.value[usuario.email] = gimnasio.nombre;
        } else {
          console.error(`Error al cargar gimnasio para usuario ${usuario.email}:`, await resGim.text());
          usuarioGimnasios.value[usuario.email] = 'No asignado';
        }
      } catch (error) {
        console.error(`Error al procesar gimnasio para usuario ${usuario.email}:`, error);
        usuarioGimnasios.value[usuario.email] = 'Error';
      }
    }
  } catch (error) {
    console.error('Error al cargar usuarios:', error);
    usuarios.value = [];
    mostrarMensaje('Error al cargar usuarios', 'danger');
  }
}

function gimnasioNombre(email) {
  return usuarioGimnasios.value[email] || '-';
}

async function registrarUsuario() {
  if (!nuevoUsuario.value.idGimnasio) {
    mostrarMensaje('Selecciona un gimnasio', 'danger');
    return;
  }
  const dUsuario = {
    nombre: nuevoUsuario.value.nombre,
    apellidos: nuevoUsuario.value.apellidos,
    tlf: nuevoUsuario.value.tlf,
    email: nuevoUsuario.value.email,
    clave: nuevoUsuario.value.clave
  };
  const token = localStorage.getItem('token');
  const authHeader = 'Bearer ' + token;
  const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${nuevoUsuario.value.idGimnasio}/nuevousuario`, {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': authHeader
    },
    body: JSON.stringify(dUsuario)
  });
  if (!res.ok) {
    mostrarMensaje('Error al registrar usuario: ' + await res.text(), 'danger');
    return;
  }
  mostrarMensaje('Usuario registrado correctamente', 'success');
  nuevoUsuario.value = { nombre: '', apellidos: '', email: '', tlf: '', clave: '', idGimnasio: '' };
  cargarUsuarios();
}

function editarUsuario(u) {
  usuarioEditando.value = { ...u };
}

async function guardarEdicion() {
  const email = usuarioEditando.value.email;
  const original = usuarios.value.find(u => u.email === email);

  const dUsuario = {
    nombre: usuarioEditando.value.nombre,
    apellidos: usuarioEditando.value.apellidos,
    tlf: usuarioEditando.value.tlf,
    email: usuarioEditando.value.email,
    clave: usuarioEditando.value.clave
  };
  if (original && original.cuotaPagada !== undefined) {
    dUsuario.cuotaPagada = original.cuotaPagada;
  }
  if (original && original.cuotaValidaHasta !== undefined && original.cuotaValidaHasta !== null) {
    dUsuario.cuotaValidaHasta = original.cuotaValidaHasta;
  }

  const token = localStorage.getItem('token');
  const authHeader = 'Bearer ' + token;
  const res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}`, {
    method: 'PUT',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': authHeader 
    },
    body: JSON.stringify(dUsuario)
  });
  if (!res.ok) {
    mostrarMensaje('Error al editar usuario: ' + await res.text(), 'danger');
    return;
  }
  mostrarMensaje('Usuario editado correctamente', 'success');
  usuarioEditando.value = null;
  cargarUsuarios();
}

async function eliminarUsuario(email) {
  const idGimnasio = Object.entries(usuarioGimnasios.value).find(([mail]) => mail === email)?.[1];
  if (!idGimnasio) {
    mostrarMensaje('No se pudo determinar el gimnasio del usuario', 'danger');
    return;
  }
  
  const gim = gimnasios.value.find(g => g.nombre === idGimnasio);
  if (!gim) {
    mostrarMensaje('No se pudo determinar el gimnasio del usuario', 'danger');
    return;
  }
  const token = localStorage.getItem('token');
  const authHeader = 'Bearer ' + token;
  const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${gim.id}/usuarios/${email}`, {
    method: 'DELETE',
    headers: { 'Authorization': authHeader }
  });
  if (!res.ok) {
    mostrarMensaje('Error al eliminar usuario: ' + await res.text(), 'danger');
    return;
  }
  mostrarMensaje('Usuario eliminado correctamente', 'success');
  cargarUsuarios();
}

async function marcarCuotaPagada(email) {
  const meses = 1;
  const token = localStorage.getItem('token');
  const authHeader = 'Bearer ' + token;
  const res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/cuota`, {
    method: 'PUT',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': authHeader
    },
    body: JSON.stringify(meses)
  });
  if (!res.ok) {
    mostrarMensaje('Error al marcar cuota pagada: ' + await res.text(), 'danger');
    return;
  }
  mostrarMensaje('Cuota marcada como pagada correctamente', 'success');
  cargarUsuarios();
}

function sortBy(key) {
  if (sortKey.value === key) {
    sortAsc.value = !sortAsc.value;
  } else {
    sortKey.value = key;
    sortAsc.value = true;
  }
}

// Función para generar usuarios de prueba
async function generarUsuariosPrueba() {
  if (!nuevoUsuario.value.idGimnasio) {
    mostrarMensaje('Selecciona un gimnasio primero', 'warning');
    return;
  }

  try {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/generar-test/${nuevoUsuario.value.idGimnasio}`, {
      method: 'POST',
      headers: { 'Authorization': authHeader }
    });

    if (!res.ok) throw new Error('Error al generar usuarios de prueba');
    
    mostrarMensaje('Usuarios de prueba generados correctamente', 'success');
    await cargarUsuarios();
  } catch (error) {
    mostrarMensaje('Error al generar usuarios de prueba: ' + error.message, 'danger');
  }
}
</script>

<style>
/* No quedan estilos específicos, todo está en global.css */
</style>