<template>
  <div class="background-container">
    <!-- Imagen de fondo -->
    <div class="background-image bg-fondo5"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="container-fluid py-4 px-md-5 position-relative" style="z-index: 10;">
    <div class="row justify-content-center">
      <!-- Columna Izquierda - Mis Medallas -->
      <div class="col-md-5 mt-4">
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-body">
            <div class="d-flex align-items-center justify-content-center mb-4 mt-2">
              <h2 class="card-title mb-0" style="color: black">Mis Medallas</h2>
            </div>
            <div class="medal-list">
              <div v-if="medallas.length === 0" class="text-center py-5">
                <i class="fas fa-award fs-1 text-muted mb-3"></i>
                <h5>¡Aún no tienes medallas!</h5>
                <p class="text-muted mb-0">¡Asiste al gimnasio con frecuencia para ganar medallas!</p>
              </div>
              
              <div v-for="medalla in medallasOrdenadas" 
                   :key="medalla.id" 
                   class="medal-item"
                   :class="{'selected': medallaSeleccionada?.id === medalla.id}"
                   @click="seleccionarMedalla(medalla)">
                <div class="medal-icon" :class="`medal-${medalla.tipo}`">
                  <i :class="medalla.icono"></i>
                </div>
                <div class="medal-content">
                  <h5 class="medal-title">{{ medalla.nombre }}</h5>
                  <p class="medal-description">{{ medalla.descripcion }}</p>
                </div>
                <div class="medal-stats">
                  <div class="progress">
                    <div class="progress-bar bg-primary" 
                         :style="{width: porcentajesPorMedalla[medalla.id] + '%'}"
                         :aria-valuenow="porcentajesPorMedalla[medalla.id]"
                         aria-valuemin="0"
                         aria-valuemax="100">
                    </div>
                  </div>
                  <small class="text-muted">{{ porcentajesPorMedalla[medalla.id] }}% de usuarios</small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Columna Derecha - Lista de Usuarios -->
      <div class="col-md-5 mt-4 ms-md-4">
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-body">
            <h2 class="card-title mb-4">Usuarios con Medallas</h2>
            <div class="mb-4 bg-light p-3 rounded">
              <label class="form-label">Filtrar por:</label>
              <select v-model="vistaSeleccionada" class="form-select">
                <option value="todos">Todos los usuarios con medallas</option>
                <option v-for="medalla in medallas" :key="medalla.id" :value="medalla.id">
                  Usuarios con: {{ medalla.nombre }}
                </option>
              </select>
            </div>
            <div class="users-list">
              <div v-if="!usuariosCargando && usuarios.length === 0" class="text-center py-4">
                <i class="fas fa-users fs-1 text-muted mb-3"></i>
                <p class="text-muted mb-0">No hay usuarios con esta medalla</p>
              </div>
              <div v-else class="list-group list-group-flush">
                <div v-for="usuario in usuarios" :key="usuario.email" 
                     class="list-group-item list-item-hover py-2 px-3 d-flex justify-content-between align-items-center">
                  <div>
                    <span class="fw-medium">{{ usuario.nombre }} {{ usuario.apellidos }}</span>
                    <small class="text-muted ms-2">
                      <i class="fas fa-medal me-1"></i>{{ vistaSeleccionada === 'todos' ? usuario.totalMedallas + ' medallas' : '1 medalla' }}
                    </small>
                  </div>
                  <button @click="verMedallasUsuario(usuario)"
                          class="btn btn-outline-primary btn-sm">
                    <i class="fas fa-medal"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de Medallas de Usuario -->
    <div v-if="modalVisible" class="modal-overlay" @click="cerrarModal">
      <div class="modal-custom" @click.stop>
        <div class="modal-header">
          <h5 class="modal-title" style="color: black">Medallas de {{ usuarioSeleccionado?.nombre }}</h5>
          <button type="button" class="btn-close" @click="cerrarModal"></button>
        </div>
        <div class="modal-body">
          <div class="row g-3">
            <div v-for="medalla in medallasUsuarioSeleccionado" :key="medalla.id" class="col-md-6">
              <div class="card shadow-sm h-100 border-0">
                <div class="card-body text-center">
                  <div :class="['medal-icon mb-3', `medal-${medalla.tipo}`]">
                    <i :class="medalla.icono" style="font-size: 2rem;"></i>
                  </div>
                  <h6 class="card-title">{{ medalla.nombre }}</h6>
                  <small class="text-muted">{{ medalla.descripcion }}</small>
                </div>
              </div>
            </div>
          </div>
        </div>
        </div>
      </div>
    </div>
  </div>
</template><script>
export default {
  name: 'UserMedallas',
  data() {
    return {
      medallas: [],
      usuarios: [],
      usuariosCargando: false,
      modalVisible: false,
      usuarioSeleccionado: null,
      medallasUsuarioSeleccionado: [],
      vistaSeleccionada: 'todos',
      medallaSeleccionada: null,
      idGimnasio: null,
      totalUsuariosGimnasio: 0,
      porcentajesPorMedalla: {},
      tiposMedalla: {
        'DIAS_SEGUIDOS': 'Días consecutivos',
        'TOTAL_DIAS': 'Total días',
        'SEMANAS_SEGUIDAS': 'Semanas consecutivas'
      }
    }
  },
  computed: {
    medallasOrdenadas() {
      if (!this.medallas) return []
      return [...this.medallas].sort((a, b) => {
        if (a.tipo < b.tipo) return -1
        if (a.tipo > b.tipo) return 1
        return a.objetivo - b.objetivo
      })
    }
  },
  watch: {
    vistaSeleccionada: {
      handler(newVal) {
        if (this.idGimnasio) {
          if (newVal === 'todos') {
            this.cargarTodosUsuariosConMedallas();
          } else {
            this.cargarUsuariosPorMedalla(newVal);
          }
        }
      }
    },
    idGimnasio: {
      handler(newVal) {
        if (newVal) {
          if (this.vistaSeleccionada === 'todos') {
            this.cargarTodosUsuariosConMedallas();
          } else {
            this.cargarUsuariosPorMedalla(this.vistaSeleccionada);
          }
        }
      }
    }
  },
  methods: {
    getTipoMedallaLabel(tipo) {
      return this.tiposMedalla[tipo] || tipo
    },
    seleccionarMedalla(medalla) {
      if (this.medallaSeleccionada?.id === medalla.id) {
        // Si la medalla ya está seleccionada, la deseleccionamos
        this.medallaSeleccionada = null;
        this.vistaSeleccionada = 'todos';
      } else {
        this.medallaSeleccionada = medalla;
        this.vistaSeleccionada = medalla.id;
      }
    },
    async cargarMedallas() {
      try {
        const email = localStorage.getItem('email');
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/medallas`, {
          headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
          this.medallas = await response.json();
          // Cargar el ID del gimnasio
          const gimnasioResponse = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/gimnasio`, {
            headers: { 'Authorization': authHeader }
          });
          if (gimnasioResponse.ok) {
            const gimnasio = await gimnasioResponse.json();
            this.idGimnasio = gimnasio.id;
            // Cargar total de usuarios y porcentajes
            await this.cargarTotalUsuariosYPorcentajes();
          }
        }
      } catch (error) {
        console.error('Error al cargar medallas:', error);
      }
    },
    
    async cargarTotalUsuariosYPorcentajes() {
      try {
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        // Cargar total de usuarios del gimnasio
        const response = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${this.idGimnasio}/usuarios`, {
          headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
          const todosUsuarios = await response.json();
          this.totalUsuariosGimnasio = todosUsuarios.length;
          // Calcular porcentaje para cada medalla
          const porcentajes = {};
          await Promise.all(this.medallas.map(async (medalla) => {
            const usuariosConMedallaResponse = await fetch(
              `http://localhost:8080/gestiongimnasios/gimnasios/${this.idGimnasio}/medallas/${medalla.id}/usuarios`,
              { headers: { 'Authorization': authHeader } }
            );
            if (usuariosConMedallaResponse.ok) {
              const usuariosConMedalla = await usuariosConMedallaResponse.json();
              const porcentaje = (usuariosConMedalla.length / this.totalUsuariosGimnasio) * 100;
              porcentajes[medalla.id] = porcentaje.toFixed(1);
            }
          }));
          // Actualizar el objeto completo de una vez
          this.porcentajesPorMedalla = porcentajes;
        }
      } catch (error) {
        console.error('Error al cargar total de usuarios y porcentajes:', error);
      }
    },
    async cargarUsuariosPorMedalla(idMedalla) {
      this.usuariosCargando = true;
      try {
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${this.idGimnasio}/medallas/${idMedalla}/usuarios`, {
          headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
          this.usuarios = await response.json();
        }
      } catch (error) {
        console.error('Error al cargar usuarios por medalla:', error);
      } finally {
        this.usuariosCargando = false;
      }
    },
    async cargarTodosUsuariosConMedallas() {
      this.usuariosCargando = true;
      try {
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${this.idGimnasio}/usuarios`, {
          headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
          const todosUsuarios = await response.json();
          // Para cada usuario, cargar su total de medallas
          const usuariosConMedallas = await Promise.all(
            todosUsuarios.map(async (usuario) => {
              const medallasResponse = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${usuario.email}/total`, {
                headers: { 'Authorization': authHeader }
              });
              const totalMedallas = medallasResponse.ok ? await medallasResponse.json() : 0;
              return {
                ...usuario,
                totalMedallas
              };
            })
          );
          this.usuarios = usuariosConMedallas.filter(u => u.totalMedallas > 0);
        }
      } catch (error) {
        console.error('Error al cargar todos los usuarios con medallas:', error);
      } finally {
        this.usuariosCargando = false;
      }
    },
    async verMedallasUsuario(usuario) {
      try {
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${usuario.email}/medallas`, {
          headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
          this.medallasUsuarioSeleccionado = await response.json();
          this.usuarioSeleccionado = usuario;
          this.modalVisible = true;
        }
      } catch (error) {
        console.error('Error al cargar medallas del usuario:', error);
      }
    },
    cerrarModal() {
      this.modalVisible = false;
      this.usuarioSeleccionado = null;
      this.medallasUsuarioSeleccionado = [];
    }
  },
  async mounted() {
    await this.cargarMedallas();
    if (this.idGimnasio) {
      this.cargarTodosUsuariosConMedallas();
    }
  }
}
</script>

<style scoped>
.medal-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.medal-item {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 1.5rem;
  padding: 1.25rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.medal-item:hover {
  transform: translateX(8px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}

.medal-item.selected {
  background-color: #f8f9fa;
  box-shadow: 0 0 0 2px #007bff, 0 4px 12px rgba(0,123,255,0.2);
}

.medal-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 3.5rem;
  height: 3.5rem;
  border-radius: 50%;
  background: #f8f9fa;
}

.medal-icon i {
  font-size: 1.75rem;
  color: #007bff;
}

.medal-content {
  flex: 1;
  min-width: 0;
}

.medal-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0 0 0.25rem;
  color: #2c3e50;
}

.medal-description {
  font-size: 0.9rem;
  color: #6c757d;
  margin: 0;
}

.medal-stats {
  text-align: right;
  min-width: 120px;
}

.progress {
  height: 6px;
  width: 100px;
  margin-bottom: 0.5rem;
  background-color: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
}

.progress-bar {
  transition: width 0.6s ease;
}
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex; justify-content: center; align-items: center;
  z-index: 1050;
}
.modal-custom {
  background: #fff;
  border-radius: 8px;
  width: 90%; max-width: 600px; max-height: 90vh;
  overflow-y: auto;
}
.users-list {
  max-height: 60vh;
  overflow-y: auto;
}

.list-item-hover {
  border-left: 3px solid transparent;
  transition: all 0.2s ease-in-out;
}
.list-item-hover:hover {
  border-left: 3px solid #007bff;
  background: #f8f9fa;
  transform: translateX(5px);
}
.btn-outline-primary {
  padding: 0.25rem 0.5rem;
  transition: all 0.2s ease;
}
.btn-outline-primary:hover {
  transform: scale(1.1);
}
.form-select {
  border-radius: 8px;
}
.card-title {
  font-weight: 600;
}

/* Estilos para el fondo */
.medallas-container {
  position: relative;
  background-color: #F1F0EA;
  min-height: 100vh;
  padding-top: 2rem;
  overflow: hidden;
}

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
  z-index: 1;
}
</style>
