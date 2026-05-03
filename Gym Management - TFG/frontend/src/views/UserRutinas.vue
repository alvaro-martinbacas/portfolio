<template>
  <div class="background-container">
    <div class="background-image bg-fondo5"></div>
    
    <div class="gradient-overlay"></div>
    
    <div class="container py-4 position-relative" style="z-index: 10;">
      <div class="row">
        <div class="col-12">
          <h2 class="mb-4">Mis Rutinas</h2>
        
        <!-- Lista de rutinas activas -->
        <div class="row g-4">
          <div v-if="rutinas.length === 0" class="col-12">
            <div class="card shadow-sm border-0">
              <div class="card-body text-center py-5">
                <i class="fas fa-dumbbell fs-1 text-muted mb-3"></i>
                <h5>No tienes rutinas asignadas</h5>
                <p class="text-muted">Consulta con tu entrenador para que te asigne una rutina personalizada.</p>
              </div>
            </div>
          </div>
          
          <div v-for="rutina in rutinas" :key="rutina.id" class="col-md-6 col-lg-4">
            <div class="card shadow-sm h-100 border-0">
              <div class="card-body">
                <h5 class="card-title mb-2">{{ rutina.nombre }}</h5>
                <p class="card-text text-muted">{{ rutina.descripcion }}</p>
                <div class="small mb-2">
                  <div v-if="entrenadoresRutinas[rutina.id]">
                    <i class="fas fa-user-md me-2"></i>
                    Entrenador: <b>{{ entrenadoresRutinas[rutina.id].nombre }}</b> ({{ entrenadoresRutinas[rutina.id].email }})
                  </div>
                  <div v-else>
                    <i class="fas fa-user-md me-2"></i>Entrenador: Cargando...
                  </div>
                </div>
              </div>
              <div class="card-footer bg-transparent border-0 text-center">
                <button @click="verDetalles(rutina.id)" class="btn btn-outline-dark w-75" style="border-radius: 20px; font-weight: 500;">
                  <i class="fas fa-dumbbell me-2"></i>Ver ejercicios
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- Modal de ejercicios estilo Bootstrap -->
  <div v-if="modalVisible" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.15);">
    <div class="modal-dialog modal-fullscreen-lg-down modal-xl" style="min-width: 95vw;">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" style="color: #111">Ejercicios de la rutina: {{ rutinaDetalles?.nombre }}</h5>
        </div>
        <div class="modal-body">
          <div v-if="ejerciciosRutina && ejerciciosRutina.length">
            <!-- Vista de escritorio -->
            <div class="table-responsive d-none d-md-block">
              <table class="table table-bordered align-middle">
                <thead>
                  <tr>
                    <th style="width: 30px;">#</th>
                    <th>Ejercicio</th>
                    <th>Equipo</th>
                    <th>Grupo Muscular</th>
                    <th>Series</th>
                    <th>Repeticiones</th>
                    <th>Descanso</th>
                    <th>Indicaciones</th>
                    <th>Video</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(ej, index) in ejerciciosRutina" :key="ej.id">
                    <td class="text-center fw-bold">{{ index + 1 }}</td>
                    <td>{{ ej.nombreEjercicio || ej.nombre }}</td>
                    <td>{{ ej.equipo || '---' }}</td>
                    <td>{{ ej.grupoMuscular || '---' }}</td>
                    <td>{{ ej.series }}</td>
                    <td>{{ ej.repeticiones }}</td>
                    <td>{{ ej.descanso }} s</td>
                    <td>{{ ej.indicaciones }}</td>
                    <td>
                      <span v-if="ej.video && ej.video.trim()">
                        <a :href="ej.video" target="_blank" rel="noopener noreferrer" style="color:blue">Ver vídeo</a>
                      </span>
                      <span v-else>---</span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- Vista móvil -->
            <div class="d-md-none">
              <div v-for="(ej, index) in ejerciciosRutina" :key="ej.id" class="ejercicio-card mb-3">
                <div class="ejercicio-header" @click="toggleEjercicio(index)">
                  <div class="d-flex justify-content-between align-items-center p-3">
                    <div class="d-flex align-items-center">
                      <span class="me-3 fw-bold text-dark">{{ index + 1 }}.</span>
                      <h6 class="mb-0 text-dark">{{ ej.nombreEjercicio || ej.nombre }}</h6>
                    </div>
                    <i :class="['fas', ej.expanded ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
                  </div>
                </div>
                <div v-show="ej.expanded" class="ejercicio-details p-3">
                  <div class="detail-row">
                    <span class="detail-label">Equipo:</span>
                    <span class="detail-value">{{ ej.equipo || '---' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">Grupo Muscular:</span>
                    <span class="detail-value">{{ ej.grupoMuscular || '---' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">Series:</span>
                    <span class="detail-value">{{ ej.series }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">Repeticiones:</span>
                    <span class="detail-value">{{ ej.repeticiones }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">Descanso:</span>
                    <span class="detail-value">{{ ej.descanso }} s</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">Indicaciones:</span>
                    <span class="detail-value">{{ ej.indicaciones }}</span>
                  </div>
                  <div v-if="ej.video && ej.video.trim()" class="detail-row">
                    <div class="px-3 py-2 rounded bg-light border">
                      <a :href="ej.video" target="_blank" rel="noopener noreferrer" class="text-primary text-decoration-none d-inline-block">
                        <i class="fas fa-play-circle me-2"></i>Ver vídeo
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="alert alert-info">No hay ejercicios en esta rutina.</div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="cerrarModal">Cerrar</button>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script>
export default {
  name: 'UserRutinas',
  data() {
    return {
      rutinas: [],
      entrenadoresRutinas: {}, // idRutina -> {nombre, email}
      ejerciciosRutina: [],
      rutinaDetalles: null,
      modalVisible: false
    }
  },
  methods: {
    toggleEjercicio(index) {
      // Cerrar todos los ejercicios excepto el actual
      this.ejerciciosRutina.forEach((ej, i) => {
        if (i !== index) {
          ej.expanded = false;
        }
      });
      // Toggle el ejercicio actual
      this.ejerciciosRutina[index].expanded = !this.ejerciciosRutina[index].expanded;
    },
    async verDetalles(id) {
      // Buscar la rutina seleccionada
      const rutina = this.rutinas.find(r => r.id === id);
      if (!rutina) return;
      this.rutinaDetalles = rutina;
      // Cargar ejercicios de la rutina
      try {
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const res = await fetch(`http://localhost:8080/gestiongimnasios/rutinas/${id}/ejerciciosRutina`, {
          headers: { 'Authorization': authHeader }
        });
        if (res.ok) {
          const ejercicios = await res.json();
          // Cargar el video y datos del ejercicio para cada ejercicio
          const ejerciciosCompletos = await Promise.all(
            ejercicios.map(async (ej) => {
              // Obtener video
              const resVideo = await fetch(`http://localhost:8080/gestiongimnasios/ejerciciosrutina/${ej.id}/video`, {
                headers: { 'Authorization': authHeader }
              });
              
              // Obtener datos del ejercicio base (equipo y grupo muscular) usando el nombre
              let ejercicioData = {};
              if (ej.nombreEjercicio) {
                try {
                  // Buscar el ejercicio por nombre en todos los ejercicios
                  const resEjercicios = await fetch('http://localhost:8080/gestiongimnasios/ejercicios/todos', {
                    headers: { 'Authorization': authHeader }
                  });
                  if (resEjercicios.ok) {
                    const todosEjercicios = await resEjercicios.json();
                    const ejercicioEncontrado = todosEjercicios.find(e => e.nombre === ej.nombreEjercicio);
                    if (ejercicioEncontrado) {
                      ejercicioData = ejercicioEncontrado;
                    }
                  }
                } catch (error) {
                  console.error('Error al buscar ejercicio por nombre:', error);
                }
              }
              
              let video = '';
              if (resVideo.ok) {
                video = await resVideo.text();
              }
              
              return {
                ...ej,
                video,
                equipo: ejercicioData.equipo || '---',
                grupoMuscular: ejercicioData.grupoMuscular || '---',
                expanded: false
              };
            })
          );
          this.ejerciciosRutina = ejerciciosCompletos;
        } else {
          this.ejerciciosRutina = [];
        }
      } catch (e) {
        this.ejerciciosRutina = [];
      }
      this.modalVisible = true;
    },
    cerrarModal() {
      this.modalVisible = false;
      this.rutinaDetalles = null;
      this.ejerciciosRutina = [];
    },
    async cargarRutinas() {
      try {
        const email = localStorage.getItem('email');
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/rutinas`, {
          headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
          this.rutinas = await response.json();
          // Cargar datos de entrenador para cada rutina
          for (const rutina of this.rutinas) {
            if (rutina.emailEntrenador) {
              const resEntrenador = await fetch(`http://localhost:8080/gestiongimnasios/entrenadores/${rutina.emailEntrenador}`, {
                headers: { 'Authorization': authHeader }
              });
              if (resEntrenador.ok) {
                const datos = await resEntrenador.json();
                this.entrenadoresRutinas = { ...this.entrenadoresRutinas, [rutina.id]: { nombre: datos.nombre, email: datos.email } };
              } else {
                this.entrenadoresRutinas = { ...this.entrenadoresRutinas, [rutina.id]: { nombre: rutina.emailEntrenador, email: rutina.emailEntrenador } };
              }
            }
          }
        }
      } catch (error) {
        console.error('Error al cargar rutinas:', error)
      }
    }
  },
  mounted() {
    this.cargarRutinas()
  }
}

</script>

<style scoped>
/* Estilos específicos de UserRutinas.vue */
.modal-content {
  max-width: none !important;
  width: 90vw !important;
  margin: 2rem auto;
  padding: 1rem;
}

.modal-body {
  padding: 1.5rem;
}

.table-responsive {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.table {
  margin-bottom: 0;
  width: 100%;
}

.table th {
  background-color: #f8f9fa;
  padding: 1rem;
  font-weight: 600;
  color: #2c3e50;
  text-transform: uppercase;
  font-size: 0.85rem;
}

.table td {
  padding: 1rem;
  vertical-align: middle;
}

/* Ajustes específicos para cada columna */
.table th:nth-child(1),
.table td:nth-child(1) {
  width: 30px; /* Número */
}

.table th:nth-child(2),
.table td:nth-child(2) {
  min-width: 120px; /* Ejercicio */
}

.table th:nth-child(3),
.table td:nth-child(3) {
  width: 100px; /* Equipo */
}

.table th:nth-child(4),
.table td:nth-child(4) {
  width: 120px; /* Grupo Muscular */
}

.table th:nth-child(5),
.table td:nth-child(5) {
  width: 60px; /* Series */
  text-align: center;
}

.table th:nth-child(6),
.table td:nth-child(6) {
  width: 80px; /* Repeticiones */
  text-align: center;
}

.table th:nth-child(7),
.table td:nth-child(7) {
  width: 80px; /* Descanso */
  text-align: center;
}

.table th:nth-child(8),
.table td:nth-child(8) {
  min-width: 150px; /* Indicaciones */
  width: auto;
}

.table th:nth-child(9),
.table td:nth-child(9) {
  width: 100px; /* Video */
  text-align: center;
}

.table tr:hover {
  background-color: #f8f9fa;
}

/* Mejoras visuales para el modal */
.modal-header {
  background-color: #f8f9fa;
  border-bottom: 2px solid #eee;
}

.modal-footer {
  background-color: #f8f9fa;
  border-top: 2px solid #eee;
}

/* Estilos para la vista móvil */
.ejercicio-card {
  border: 1px solid #eee;
  border-radius: 12px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.ejercicio-header {
  background: #f8f9fa;
  cursor: pointer;
  transition: background-color 0.2s;
}

.ejercicio-header:hover {
  background: #e9ecef;
}

.ejercicio-header h6 {
  font-weight: 600;
}

.ejercicio-details {
  border-top: 1px solid #eee;
  background: white;
}

.detail-row {
  display: flex;
  flex-direction: column;
  margin-bottom: 0.75rem;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-weight: 600;
  color: #495057;
  font-size: 0.9rem;
  margin-bottom: 0.25rem;
}

.detail-value {
  color: #212529;
}

@media (max-width: 768px) {
  .modal-content {
    width: 100vw !important;
    margin: 0;
    min-height: 100vh;
    padding: 0;
  }

  .modal-body {
    padding: 1rem;
  }
  
  .modal-header {
    padding: 1rem;
  }
  
  .modal-footer {
    padding: 1rem;
  }
}

/* Estilos de fondo */
.background-container {
  position: relative;
  min-height: 100vh;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.1;
  z-index: 0;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
  z-index: 1;
}
</style>
