<template>
  <div class="container py-4">
    <h1 class="mb-2" style="color:#111;font-weight:bold;">Panel Administrador - Gimnasios</h1>
    <h2 class="mb-4">Gestión de Gimnasios</h2>
    <!-- Formulario de creación -->
    <form @submit.prevent="crearGimnasio" class="mb-4 bg-light p-3 rounded">
      <div class="row g-2 mt-2">
        <div class="col">
          <input v-model="nuevoGimnasio.nombre" class="form-control" placeholder="Nombre" required />
        </div>
        <div class="col">
          <input v-model="direccionCalle" class="form-control" placeholder="Calle" required />
        </div>
        <div class="col">
          <input v-model="direccionNumero" class="form-control" placeholder="Número" required />
        </div>
        <div class="col">
          <input v-model="direccionLocalidad" class="form-control" placeholder="Localidad" required />
        </div>
        <div class="col">
          <input v-model="direccionCP" class="form-control" placeholder="Código Postal" required pattern="^\d{5}$" title="Introduce un código postal de 5 cifras"/>
        </div>
        <div class="col">
          <input v-model="nuevoGimnasio.telefono" class="form-control" placeholder="Teléfono" required />
        </div>
        <div class="col">
          <input v-model="nuevoGimnasio.horaApertura" type="time" class="form-control" placeholder="Hora Apertura" required />
        </div>
        <div class="col">
          <input v-model="nuevoGimnasio.horaCierre" type="time" class="form-control" placeholder="Hora Cierre" required />
        </div>
        <div class="col">
          <button class="btn btn-primary w-100" type="submit">Crear</button>
        </div>
      </div>
    </form>

    <!-- Lista de gimnasios -->
    <table class="table table-hover bg-white rounded shadow-sm">
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Dirección</th>
          <th>Teléfono</th>
          <th>Hora Apertura</th>
          <th>Hora Cierre</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="gim in gimnasios" :key="gim.id">
          <td>{{ gim.nombre }}</td>
          <td>{{ gim.direccion }}</td>
          <td>{{ gim.telefono }}</td>
          <td>{{ gim.horaApertura ? gim.horaApertura.slice(0, 5) : '' }}</td>
          <td>{{ gim.horaCierre ? gim.horaCierre.slice(0, 5) : '' }}</td>
          <td>
            <button class="btn btn-sm btn-warning me-2" @click="editarGimnasio(gim)">Editar</button>
            <button class="btn btn-sm btn-danger" @click="eliminarGimnasio(gim.id)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Modal de edición -->
    <div v-if="gimnasioEditando" class="modal-backdrop">
      <div class="modal-content">
        <h4>Editar Gimnasio</h4>
        <form @submit.prevent="guardarEdicion">
          <label class="form-label">Nombre</label>
          <input v-model="gimnasioEditando.nombre" class="form-control mb-2" placeholder="Nombre" required />
          <label class="form-label">Calle</label>
          <input v-model="direccionEditCalle" class="form-control mb-2" placeholder="Calle" required />
          <label class="form-label">Número</label>
          <input v-model="direccionEditNumero" class="form-control mb-2" placeholder="Número" required />
          <label class="form-label">Localidad</label>
          <input v-model="direccionEditLocalidad" class="form-control mb-2" placeholder="Localidad" required />
          <label class="form-label">Código Postal</label>
          <input v-model="direccionEditCP" class="form-control mb-2" placeholder="Código Postal" required pattern="^\d{5}$" title="Introduce un código postal de 5 cifras" />
          <label class="form-label">Teléfono</label>
          <input v-model="gimnasioEditando.telefono" class="form-control mb-2" placeholder="Teléfono" required />
          <label class="form-label">Hora Apertura</label>
          <input v-model="gimnasioEditando.horaApertura" type="time" class="form-control mb-2" placeholder="Hora Apertura" required />
          <label class="form-label">Hora Cierre</label>
          <input v-model="gimnasioEditando.horaCierre" type="time" class="form-control mb-2" placeholder="Hora Cierre" required />
          <div class="form-text mb-3">Asegúrate de que los horarios estén en formato 24 horas.</div>
          <button class="btn btn-success me-2" type="submit">Guardar</button>
          <button class="btn btn-secondary" @click="gimnasioEditando = null" type="button">Cancelar</button>
        </form>
      </div>
    </div>
    <div v-if="gimnasioViendo" class="modal-backdrop">
      <div class="modal-content">
        <h4>Datos del Gimnasio</h4>
        <ul class="list-group mb-3">
          <li class="list-group-item"><b>Nombre:</b> {{ gimnasioViendo.nombre }}</li>
          <li class="list-group-item"><b>Dirección:</b> {{ gimnasioViendo.direccion }}</li>
          <li class="list-group-item"><b>Teléfono:</b> {{ gimnasioViendo.telefono }}</li>
          <li class="list-group-item"><b>Hora Apertura:</b> {{ gimnasioViendo.horaApertura }}</li>
          <li class="list-group-item"><b>Hora Cierre:</b> {{ gimnasioViendo.horaCierre }}</li>
        </ul>
        <button class="btn btn-secondary" @click="cerrarVerGimnasio">Cerrar</button>
      </div>
    </div>
    <AlertMessage />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAlertMessage } from '../composables/useAlertMessage';
import AlertMessage from '../components/AlertMessage.vue';

const { mostrarMensaje } = useAlertMessage();

const gimnasios = ref([]);
const nuevoGimnasio = ref({ nombre: '', direccion: '', telefono: '', horaApertura: '', horaCierre: ''});
const direccionCalle = ref('');
const direccionNumero = ref('');
const direccionLocalidad = ref('');
const direccionCP = ref('');

const gimnasioEditando = ref(null);
const gimnasioViendo = ref(null);

const direccionEditCalle = ref("");
const direccionEditNumero = ref("");
const direccionEditLocalidad = ref("");
const direccionEditCP = ref("");

async function cargarGimnasios() {
  const token = localStorage.getItem('token');
  const authHeader = 'Bearer ' + token;
  const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/todos', {
    headers: { 'Authorization': authHeader }
  });
  gimnasios.value = await res.json();
}

async function crearGimnasio() {
  nuevoGimnasio.value.direccion = `${direccionCalle.value} ${direccionNumero.value}, ${direccionLocalidad.value}, ${direccionCP.value}`;

  try {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch('http://localhost:8080/gestiongimnasios/gimnasios/', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
      body: JSON.stringify(nuevoGimnasio.value)
    });
    if (!res.ok) {
      const msg = await res.text();
      mostrarMensaje('Error al crear gimnasio: ' + msg, 'danger');
      return;
    }
    mostrarMensaje('Gimnasio creado correctamente', 'success');
    nuevoGimnasio.value = { nombre: '', direccion: '', telefono: '', horaApertura: '', horaCierre: '' };
    direccionCalle.value = '';
    direccionNumero.value = '';
    direccionLocalidad.value = '';
    direccionCP.value = '';
    cargarGimnasios();
  } catch (e) {
    mostrarMensaje('Error de conexión al crear gimnasio', 'danger');
  }
}


function editarGimnasio(gim) {
  gimnasioEditando.value = { ...gim };
  // Rellenar los campos de dirección para edición
  const partes = gim.direccion ? gim.direccion.split(',').map(s => s.trim()) : ["", "", ""];
  // partes[0] = "calle numero"
  const calleNumero = partes[0] ? partes[0].split(' ') : ["", ""];
  direccionEditCalle.value = calleNumero.slice(0, -1).join(' ') || "";
  direccionEditNumero.value = calleNumero.slice(-1)[0] || "";
  direccionEditLocalidad.value = partes[1] || "";
  direccionEditCP.value = partes[2] || "";
}

async function guardarEdicion() {
  const direccionCompuesta = `${direccionEditCalle.value}, ${direccionEditNumero.value}, ${direccionEditLocalidad.value}, ${direccionEditCP.value}`;
  const datos = {
    nombre: gimnasioEditando.value.nombre,
    direccion: direccionCompuesta,
    telefono: gimnasioEditando.value.telefono,
    horaApertura: gimnasioEditando.value.horaApertura,
    horaCierre: gimnasioEditando.value.horaCierre
  };
  
  try {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${gimnasioEditando.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'Authorization': authHeader },
      body: JSON.stringify(datos)
    });
    if (!res.ok) {
      mostrarMensaje('Error al editar gimnasio: ' + await res.text(), 'danger');
      return;
    }
    mostrarMensaje('Gimnasio editado correctamente', 'success');
    gimnasioEditando.value = null;
    cargarGimnasios();
  } catch (e) {
    mostrarMensaje('Error de conexión al editar gimnasio', 'danger');
  }
}

async function eliminarGimnasio(id) {
  if (!confirm('¿Seguro que quieres eliminar este gimnasio?')) return;
  
  try {
    const token = localStorage.getItem('token');
    const authHeader = 'Bearer ' + token;
    const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${id}`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json', 'Authorization': authHeader }
    });
    if (!res.ok) {
      mostrarMensaje('Error al eliminar: ' + await res.text(), 'danger');
      return;
    }
    mostrarMensaje('Gimnasio eliminado correctamente', 'success');
    cargarGimnasios();
  } catch (e) {
    mostrarMensaje('Error de conexión al eliminar', 'danger');
  }
}

onMounted(cargarGimnasios);
</script>

<style>
/* No quedan estilos específicos, todo está en global.css */
</style>