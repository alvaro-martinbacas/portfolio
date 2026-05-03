<template>
  <div class="container py-4">
    <AlertMessage />
    <h1 class="mb-2" style="color:#111;font-weight:bold;">Panel Administrador - Medallas</h1>
    <h2 class="mb-4">Gestión de Medallas</h2>



    <!-- Formulario de creación de medalla -->
    <form @submit.prevent="crearMedalla" class="mb-4 bg-light p-3 rounded">
      <div class="row g-2 align-items-stretch">
        <div class="col">
          <select v-model="nuevaMedalla.tipo" class="form-select h-100" required @change="actualizarNombreDescripcion">
            <option value="" disabled>Selecciona el tipo de medalla</option>
            <option v-for="tipo in tiposMedalla" :key="tipo.value" :value="tipo.value">
              {{ tipo.label }}
            </option>
          </select>
        </div>
        <div class="col">
          <input v-model.number="nuevaMedalla.objetivo" type="number" min="1" class="form-control h-100" 
            placeholder="Indica el objetivo" required @input="actualizarNombreDescripcion" />
        </div>
        <div class="col d-grid">
          <button class="button btn-primary h-100" type="submit">Crear medalla</button>
        </div>
      </div>
      <!-- Vista previa del nombre y descripción -->
      <div v-if="nuevaMedalla.tipo && nuevaMedalla.objetivo" class="mt-3">
        <div class="alert alert-info">
          <strong>Vista previa:</strong><br>
          <span class="d-block"><b>Nombre:</b> {{ nuevaMedalla.nombre }}</span>
          <span class="d-block"><b>Descripción:</b> {{ nuevaMedalla.descripcion }}</span>
        </div>
      </div>
    </form>
    <!-- Botón de gestión de medallas -->
    <div class="mb-4">
      <button 
        @click="abrirModalGestionMedallas"
        class="btn btn-info text-white"
        data-modal="gestionMedallas"
      >
        Medallas Existentes
      </button>
    </div>
    <!-- Filtros -->
    <form class="mb-3 bg-light p-3 rounded">
      <div class="row g-2 align-items-stretch">
        <div class="col">
          <select v-model="visualGimnasio" class="form-select h-100" @change="cargarUsuariosGimnasio">
            <option value="">Seleccione un gimnasio</option>
            <option v-for="gimnasio in gimnasios" :key="gimnasio.id" :value="gimnasio.id">
              {{ gimnasio.nombre }}
            </option>
          </select>
        </div>
        <div class="col">
          <select v-model="visualUsuario" class="form-select h-100" :disabled="!visualGimnasio">
            <option value="">Todos los usuarios</option>
            <option v-for="usuario in usuariosGimnasio" :key="usuario.email" :value="usuario.email">
              {{ usuario.nombre }} {{ usuario.apellidos }}
            </option>
          </select>
        </div>
      </div>
    </form>

    <!-- Botones de acción -->
    <div class="d-flex mb-4 gap-2">
      <!-- Botón de verificación masiva -->
      <button 
        @click="verificarMedallasGimnasio"
        class="btn btn-primary"
        :disabled="!visualGimnasio"
      >
        Otorgar Medallas a Usuarios automáticamente
      </button>
    </div>

    <!-- Estadísticas -->
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Usuario con más medallas</h5>
            <p class="card-text" v-if="usuarioConMasMedallas">
              {{ usuarioConMasMedallas.nombre }} ({{ usuarioConMasMedallas.medallas }} medallas)
            </p>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Total medallas otorgadas</h5>
            <p class="card-text">{{ totalMedallasOtorgadas }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Tabla de usuarios y sus medallas -->
    <div class="table-responsive">
      <table class="table table-striped table-hover">
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
            <th @click="sortBy('medallas')" style="cursor:pointer">
              Medallas
              <span v-if="sortKey === 'medallas'">{{ sortAsc ? '▲' : '▼' }}</span>
            </th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="usuario in usuariosFiltrados" :key="usuario.email">
            <td>{{ usuario.nombre }} {{ usuario.apellidos }}</td>
            <td>{{ usuario.email }}</td>
            <td>
              <div class="d-flex align-items-center">
                <span class="badge bg-primary me-2">
                  {{ usuario.totalMedallas || (usuario.medallas ? usuario.medallas.length : 0) }} medallas
                </span>
                <button 
                  class="btn btn-sm btn-outline-info"
                  @click="verDetallesMedallas(usuario)"
                >
                  Ver Medallas
                </button>
              </div>
            </td>
            <td>
              <button @click="abrirModalOtorgar(usuario)" class="btn btn-success btn-sm me-2">
                Otorgar medalla
              </button>
              <button
                v-if="usuario.medallas && usuario.medallas.length > 0"
                @click="abrirModalQuitar(usuario)"
                class="btn btn-danger btn-sm"
              >
                Quitar medalla
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal Gestión de Medallas -->
    <div class="modal fade" id="modalGestionMedallas" tabindex="-1" aria-labelledby="modalGestionMedallasTitle">
      <div class="modal-dialog modal-lg" role="document" style="display: flex; justify-content: center;">
        <div class="modal-content" style="min-width: 80vw; max-width: 90vw; margin: 20px auto;">
          <div class="modal-header">
            <h5 class="modal-title" id="modalGestionMedallasTitle" style="color: #111">Gestión de Medallas Existentes</h5>
          </div>
          <div class="modal-body">
            <div class="table-responsive">
              <table class="table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Descripción</th>
                    <th>Tipo</th>
                    <th>Objetivo</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <template v-for="tipo in tiposMedalla" :key="tipo.value">
                    <!-- Cabecera del grupo -->
                    <tr class="table-light">
                      <td colspan="5" class="fw-bold">{{ tipo.label }}</td>
                    </tr>
                    <!-- Medallas del grupo -->
                    <tr v-for="medalla in medallasPorTipo(tipo.value)" 
                        :key="medalla.id"
                        class="align-middle">
                      <td>
                        <div class="d-flex align-items-center gap-2">
                          <div :class="['medal-icon-wrapper', `medal-${medalla.tipo.toLowerCase()}`]">
                            <i :class="medalla.icono" class="fa-lg"></i>
                          </div>
                          {{ medalla.nombre }}
                        </div>
                      </td>
                      <td>{{ medalla.descripcion }}</td>
                      <td>{{ getTipoMedallaLabel(medalla.tipo) }}</td>
                      <td>{{ medalla.objetivo }}</td>
                      <td>
                        <div>
                          <button @click="eliminarMedalla(medalla.id)" class="btn btn-sm btn-danger">Eliminar</button>
                        </div>
                      </td>
                    </tr>
                    <!-- Espacio entre grupos -->
                    <tr class="spacer">
                      <td colspan="5" class="p-0"></td>
                    </tr>
                  </template>
                </tbody>
              </table>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Otorgar Medalla -->
    <div class="modal fade" id="modalOtorgarMedalla" tabindex="-1">
      <div class="modal-dialog" style="display: flex; justify-content: center;">
        <div class="modal-content" style="min-width: 900px; margin: 20px auto;">
          <div class="modal-header">
            <h5 class="modal-title" style="color: #111">Otorgar Medalla</h5>
          </div>
          <div class="modal-body">
            <div v-if="usuarioSeleccionado">
              <p>Otorgar medalla a: {{ usuarioSeleccionado.nombre }} {{ usuarioSeleccionado.apellidos }}</p>
              <select v-model="medallaSeleccionada" class="form-select">
                <option
                  v-for="medalla in medallasDisponibles"
                  :key="medalla.id"
                  :value="medalla.id"
                >
                  {{ medalla.nombre }} - {{ medalla.descripcion }}
                </option>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="otorgarMedalla">Otorgar</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Quitar Medalla -->
    <div class="modal fade" id="modalQuitarMedalla" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Quitar Medalla</h5>
          </div>
          <div class="modal-body">
            <div v-if="usuarioSeleccionado">
              <p>Quitar medalla a: {{ usuarioSeleccionado.nombre }} {{ usuarioSeleccionado.apellidos }}</p>
              <select v-model="medallaSeleccionada" class="form-select">
                <option
                  v-for="medalla in usuarioSeleccionado.medallas"
                  :key="medalla.id"
                  :value="medalla.id"
                >
                  {{ medalla.nombre }}
                </option>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="button" class="btn btn-danger" @click="quitarMedalla">Quitar</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Detalles Medallas -->
    <div class="modal fade" id="modalDetallesMedallas" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" style="color: #111">
              Medallas de {{ usuarioSeleccionado?.nombre }} {{ usuarioSeleccionado?.apellidos }}
            </h5>
          </div>
          <div class="modal-body">
            <div v-if="medallasPorUsuario.length">
              <div v-for="medalla in medallasPorUsuario" :key="medalla.id" class="mb-3">
                <h6 class="mb-1" style="color: #111">{{ medalla.nombre }}</h6>
                <p class="text-muted mb-0 small">{{ medalla.descripcion }}</p>
                <p class="text-muted mb-0 small">
                  <span class="badge bg-secondary me-2">
                    {{ getTipoMedallaLabel(medalla.tipo) }}
                  </span>
                  Objetivo: {{ medalla.objetivo }}
                </p>
                <hr class="my-2">
              </div>
            </div>
            <div v-else class="text-center text-muted">
              Este usuario no tiene medallas
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>


<script>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import { Modal } from 'bootstrap'
import { useAlertMessage } from '../composables/useAlertMessage'
import AlertMessage from '../components/AlertMessage.vue'
import { useGimnasioClasesUsuarios } from '../composables/useGimnasioClasesUsuarios'

const TIPOS_MEDALLA = [
  { value: 'DIAS_SEGUIDOS', label: 'Días consecutivos' },
  { value: 'TOTAL_DIAS', label: 'Total días' },
  { value: 'SEMANAS_SEGUIDAS', label: 'Semanas consecutivas' }
]

export default {
  name: 'AdminMedalla',
  components: {
    AlertMessage
  },
  setup() {
    const { gimnasios, usuarios: usuariosGimnasio, cargarGimnasios, cargarUsuarios } = useGimnasioClasesUsuarios()
    const { mostrarMensaje } = useAlertMessage()

    const visualGimnasio = ref('')
    const visualUsuario = ref('')
    const medallas = ref([])
    const filtroTipoMedalla = ref('')
    const sortKey = ref('nombre')
    const sortAsc = ref(true)
    const usuarioSeleccionado = ref(null)
    const medallaSeleccionada = ref(null)
    const modalOtorgar = ref(null)
    const modalQuitar = ref(null)
    const modalDetalles = ref(null)
    const modalGestionMedallas = ref(null)
    const nuevaMedalla = ref({ nombre: '', descripcion: '', tipo: '', objetivo: '', icono: '' })
    const medallasPorUsuario = ref([])
    const cargarDatos = async () => {
      try {
        const [gimnasiosRes, medallasRes] = await Promise.all([
          axios.get('http://localhost:8080/gestiongimnasios/gimnasios/todos', { withCredentials: true }),
          axios.get('http://localhost:8080/gestiongimnasios/medallas/todos', { withCredentials: true })
        ])
        gimnasios.value = gimnasiosRes.data
        medallas.value = medallasRes.data
      } catch (error) {
        console.error('Error al cargar datos:', error)
        mostrarMensaje('Error al cargar los datos', 'error')
      }
    }

    onMounted(async () => {
      await cargarGimnasios()
      await cargarUsuarios()
      await cargarDatos()
    })

    const cargarUsuariosGimnasio = async () => {
      if (visualGimnasio.value) {
        await cargarUsuarios(visualGimnasio.value)
        if (usuariosGimnasio.value && usuariosGimnasio.value.length > 0) {
          await Promise.all(
            usuariosGimnasio.value.map(async (usuario) => {
              try {
                const token = localStorage.getItem('token');
                // Cargar las medallas del usuario
                const medallasRes = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${usuario.email}/lista-medallas`, {
                  headers: { 'Authorization': `Bearer ${token}` }
                });
                if (medallasRes.ok) {
                  usuario.medallas = await medallasRes.json();
                  usuario.totalMedallas = usuario.medallas.length;
                } else {
                  usuario.medallas = [];
                  usuario.totalMedallas = 0;
                }
              } catch (e) {
                console.error('Error cargando medallas para usuario:', usuario.email, e);
                usuario.medallas = [];
                usuario.totalMedallas = 0;
              }
            })
          )
        }
        await cargarTotalMedallasGimnasio()
      } else {
        usuariosGimnasio.value = []
        totalMedallasGimnasio.value = 0
      }
      visualUsuario.value = ''
    }

    const totalMedallasGimnasio = ref(0)
    const cargarTotalMedallasGimnasio = async () => {
      if (visualGimnasio.value) {
        try {
          const token = localStorage.getItem('token');
          const res = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${visualGimnasio.value}/total-medallas`, {
            headers: { 'Authorization': `Bearer ${token}` }
          });
          if (res.ok) {
            const data = await res.json();
            totalMedallasGimnasio.value = data.totalMedallas || 0;
          } else {
            totalMedallasGimnasio.value = 0;
          }
        } catch (e) {
          console.error('Error cargando total medallas:', e);
          totalMedallasGimnasio.value = 0;
        }
      } else {
        totalMedallasGimnasio.value = 0;
      }
    }

    function sortBy(key) {
      if (sortKey.value === key) {
        sortAsc.value = !sortAsc.value
      } else {
        sortKey.value = key
        sortAsc.value = true
      }
    }

    const usuariosFiltrados = computed(() => {
      if (!usuariosGimnasio.value) return []
      let filtrados = [...usuariosGimnasio.value]
      if (visualUsuario.value) {
        filtrados = filtrados.filter(u => u.email === visualUsuario.value)
      }
      if (filtroTipoMedalla.value) {
        filtrados = filtrados.filter(usuario => 
          usuario.medallas && usuario.medallas.some(medalla => medalla.tipo === filtroTipoMedalla.value)
        )
      }
      return filtrados.sort((a, b) => {
        let valA, valB
        if (sortKey.value === 'nombre') {
          valA = a.nombre
          valB = b.nombre
        } else if (sortKey.value === 'email') {
          valA = a.email
          valB = b.email
        } else if (sortKey.value === 'medallas') {
          valA = typeof a.totalMedallas === 'number' ? a.totalMedallas : (a.medallas ? a.medallas.length : 0)
          valB = typeof b.totalMedallas === 'number' ? b.totalMedallas : (b.medallas ? b.medallas.length : 0)
        }
        if (valA < valB) return sortAsc.value ? -1 : 1
        if (valA > valB) return sortAsc.value ? 1 : -1
        return 0
      })
    })

    const medallasDisponibles = computed(() => {
      if (!usuarioSeleccionado.value || !medallas.value) return []
      if (!usuarioSeleccionado.value.medallas) return medallas.value
      return medallas.value.filter(m => 
        !usuarioSeleccionado.value.medallas.some(um => um.id === m.id)
      )
    })

    const usuarioConMasMedallas = computed(() => {
      if (!usuariosGimnasio.value?.length) return null
      const usuario = [...usuariosGimnasio.value].sort((a, b) => {
        const medallasA = typeof a.totalMedallas === 'number' ? a.totalMedallas : (a.medallas ? a.medallas.length : 0)
        const medallasB = typeof b.totalMedallas === 'number' ? b.totalMedallas : (b.medallas ? b.medallas.length : 0)
        return medallasB - medallasA
      })[0]
      if (!usuario) return null
      return {
        nombre: `${usuario.nombre} ${usuario.apellidos}`,
        medallas: typeof usuario.totalMedallas === 'number' ? usuario.totalMedallas : (usuario.medallas ? usuario.medallas.length : 0)
      }
    })

    const totalMedallasOtorgadas = computed(() => {
      return totalMedallasGimnasio.value
    })

    const getTipoMedallaLabel = (tipo) => {
      const tipoMedalla = TIPOS_MEDALLA.find(t => t.value === tipo)
      return tipoMedalla ? tipoMedalla.label : tipo
    }

    // Eliminado el método getMedallaIcon ya que los iconos se generan al crear la medalla

    const abrirModalOtorgar = (usuario) => {
      usuarioSeleccionado.value = usuario
      medallaSeleccionada.value = null
      modalOtorgar.value.show()
    }

    const abrirModalQuitar = (usuario) => {
      usuarioSeleccionado.value = usuario
      medallaSeleccionada.value = null
      modalQuitar.value.show()
    }

    const verDetallesMedallas = async (usuario) => {
      usuarioSeleccionado.value = usuario
      try {
        const token = localStorage.getItem('token');
        const res = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${usuario.email}/lista-medallas`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
          medallasPorUsuario.value = await res.json();
        } else {
          medallasPorUsuario.value = [];
        }
      } catch (e) {
        console.error('Error cargando medallas del usuario:', e);
        medallasPorUsuario.value = [];
        mostrarMensaje('Error al cargar las medallas del usuario', 'error');
      }
      modalDetalles.value.show()
    }

    const otorgarMedalla = async () => {
      if (!usuarioSeleccionado.value || !medallaSeleccionada.value) {
        mostrarMensaje('Selecciona usuario y medalla', 'error')
        return
      }
      try {
        await axios.post(
          `http://localhost:8080/gestiongimnasios/usuarios/${usuarioSeleccionado.value.email}/medalla/${medallaSeleccionada.value}`,
          null,
          { withCredentials: true }
        )
        await cargarDatos()
        if (visualGimnasio.value) {
          await cargarUsuariosGimnasio() // Recargar los usuarios del gimnasio actual
        }
        modalOtorgar.value.hide()
        mostrarMensaje('Medalla otorgada correctamente', 'success')
      } catch (error) {
        if (error.response && error.response.status === 409) {
          mostrarMensaje('El usuario ya tiene esa medalla', 'error')
        } else {
          mostrarMensaje('Error al otorgar la medalla', 'error')
        }
      }
    }

    const quitarMedalla = async () => {
      if (!usuarioSeleccionado.value || !medallaSeleccionada.value) {
        mostrarMensaje('Selecciona usuario y medalla', 'error')
        return
      }
      try {
        await axios.delete(
          `http://localhost:8080/gestiongimnasios/usuarios/${usuarioSeleccionado.value.email}/medallas/${medallaSeleccionada.value}`
        )
        await cargarDatos()
        modalQuitar.value.hide()
        mostrarMensaje('Medalla quitada correctamente', 'success')
      } catch (error) {
        mostrarMensaje('Error al quitar la medalla', 'error')
      }
    }

    const crearMedalla = async () => {
      if (!nuevaMedalla.value.nombre || !nuevaMedalla.value.descripcion || !nuevaMedalla.value.tipo || !nuevaMedalla.value.objetivo) {
        mostrarMensaje('Completa todos los campos', 'error')
        return
      }
      await axios.post('http://localhost:8080/gestiongimnasios/medallas', {
        nombre: nuevaMedalla.value.nombre,
        descripcion: nuevaMedalla.value.descripcion,
        tipo: nuevaMedalla.value.tipo,
        objetivo: nuevaMedalla.value.objetivo,
        icono: nuevaMedalla.value.icono
      },
      { withCredentials: true }
    )
      .then(() => {
        mostrarMensaje('Medalla creada correctamente', 'success')
        nuevaMedalla.value = { nombre: '', descripcion: '', tipo: '', objetivo: '' }
        cargarDatos()
      })
      .catch(error => {
        if (error.response && error.response.status === 409) {
          mostrarMensaje('Ya existe una medalla con ese tipo y objetivo', 'error')
        } else if (error.response && error.response.data) {
          let msg = typeof error.response.data === 'string' ? error.response.data : (error.response.data.message || JSON.stringify(error.response.data))
          mostrarMensaje('Error: ' + msg, 'error')
        } else {
          mostrarMensaje('Error al crear la medalla', 'error')
        }
      })
    }

    const verificarMedallasGimnasio = async () => {
      if (!visualGimnasio.value) {
        mostrarMensaje('Selecciona un gimnasio primero', 'error')
        return
      }
      try {
        const token = localStorage.getItem('token');
        await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${visualGimnasio.value}/verificacion-medallas-todos`, {
          method: 'POST',
          headers: { 'Authorization': `Bearer ${token}` }
        })
        mostrarMensaje('Verificación de medallas completada correctamente', 'success')
        await cargarUsuariosGimnasio()
      } catch (error) {
        mostrarMensaje('Error al verificar las medallas', 'error')
      }
    }

    const medallasPorTipo = (tipo) => {
      return medallas.value
        .filter(m => m.tipo === tipo)
        .sort((a, b) => a.objetivo - b.objetivo)
    }

    const abrirModalGestionMedallas = () => {
      const modal = document.getElementById('modalGestionMedallas')
      modal.addEventListener('hidden.bs.modal', () => {
        document.querySelector('[data-modal="gestionMedallas"]')?.focus()
      }, { once: true })
      modalGestionMedallas.value.show()
    }

    const eliminarMedalla = async (id) => {
      try {
        const token = localStorage.getItem('token');
        await fetch(`http://localhost:8080/gestiongimnasios/medallas/${id}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${token}` }
        })
        await cargarDatos()
        mostrarMensaje('Medalla eliminada correctamente', 'success')
      } catch (error) {
        if (error.response && error.response.status === 409) {
          mostrarMensaje('No se puede eliminar la medalla porque está asignada a uno o más usuarios', 'error')
        } else if (error.response && error.response.status === 404) {
          mostrarMensaje('La medalla que intentas eliminar ya no existe', 'error')
        } else {
          mostrarMensaje('No se pudo eliminar la medalla. Por favor, inténtalo de nuevo más tarde', 'error')
        }
      }
    }

    // Método para generar automáticamente el nombre y descripción
    const actualizarNombreDescripcion = () => {
      if (!nuevaMedalla.value.tipo || !nuevaMedalla.value.objetivo) {
        nuevaMedalla.value.nombre = ''
        nuevaMedalla.value.descripcion = ''
        nuevaMedalla.value.icono = ''
        return
      }

      switch(nuevaMedalla.value.tipo) {
        case 'DIAS_SEGUIDOS':
          nuevaMedalla.value.nombre = `Constancia ${nuevaMedalla.value.objetivo} días`
          nuevaMedalla.value.descripcion = `Asistir al gimnasio durante ${nuevaMedalla.value.objetivo} días consecutivos`
          if (nuevaMedalla.value.objetivo <= 5) {
            nuevaMedalla.value.icono = 'fas fa-fire text-danger'
          } else if (nuevaMedalla.value.objetivo <= 15) {
            nuevaMedalla.value.icono = 'fas fa-fire-alt text-warning'
          } else {
            nuevaMedalla.value.icono = 'fas fa-crown text-warning'
          }
          break

        case 'TOTAL_DIAS':
          nuevaMedalla.value.nombre = `Veterano ${nuevaMedalla.value.objetivo} días`
          nuevaMedalla.value.descripcion = `Acumular un total de ${nuevaMedalla.value.objetivo} días de asistencia al gimnasio`
          if (nuevaMedalla.value.objetivo <= 10) {
            nuevaMedalla.value.icono = 'fas fa-dumbbell text-secondary'
          } else if (nuevaMedalla.value.objetivo <= 30) {
            nuevaMedalla.value.icono = 'fas fa-award text-primary'
          } else {
            nuevaMedalla.value.icono = 'fas fa-trophy text-warning'
          }
          break

        case 'SEMANAS_SEGUIDAS':
          nuevaMedalla.value.nombre = `Dedicación ${nuevaMedalla.value.objetivo} semanas`
          nuevaMedalla.value.descripcion = `Acceso al gimnasio durante ${nuevaMedalla.value.objetivo} semanas consecutivas`
          if (nuevaMedalla.value.objetivo <= 4) {
            nuevaMedalla.value.icono = 'fas fa-star text-info'
          } else if (nuevaMedalla.value.objetivo <= 12) {
            nuevaMedalla.value.icono = 'fas fa-star text-primary'
          } else {
            nuevaMedalla.value.icono = 'fas fa-medal text-warning'
          }
          break
      }
    }

    onMounted(() => {
      modalOtorgar.value = new Modal(document.getElementById('modalOtorgarMedalla'))
      modalQuitar.value = new Modal(document.getElementById('modalQuitarMedalla'))
      modalDetalles.value = new Modal(document.getElementById('modalDetallesMedallas'))
      modalGestionMedallas.value = new Modal(document.getElementById('modalGestionMedallas'))
      cargarDatos()
    })

    watch(visualGimnasio, async (nuevo) => {
      if (nuevo) {
        await cargarUsuariosGimnasio()
      }
    })

    return {
      gimnasios,
      usuariosGimnasio,
      visualGimnasio,
      visualUsuario,
      filtroTipoMedalla,
      sortKey,
      sortAsc,
      usuarioSeleccionado,
      medallaSeleccionada,
      medallasPorUsuario,
      cargarUsuariosGimnasio,
      usuariosFiltrados,
      medallasDisponibles,
      usuarioConMasMedallas,
      totalMedallasOtorgadas,
      abrirModalOtorgar,
      abrirModalQuitar,
      verDetallesMedallas,
      otorgarMedalla,
      quitarMedalla,
      nuevaMedalla,
      crearMedalla,
      sortBy,
      tiposMedalla: TIPOS_MEDALLA,
      getTipoMedallaLabel,
      verificarMedallasGimnasio,
      abrirModalGestionMedallas,
      actualizarNombreDescripcion,
      medallas,
      eliminarMedalla,
      abrirModalGestionMedallas,
      medallasPorTipo,
    }
  }
}
</script>

<style>
.table .spacer td {
  height: 1rem;
  background-color: #f8f9fa;
  border: none;
}

.table-light {
  background-color: #f8f9fa;
}

/* Los estilos de medallas se han movido a global.css */
</style>
