<template>
  <div class="background-container">
    <!-- Imagen de fondo -->
    <div class="background-image bg-fondo3"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="container py-4 px-3 position-relative" style="z-index: 10;">
      <div class="row">
        <div class="col-12">
          <h2 class="mb-4">Historial de Accesos</h2>

        <!-- Filtros -->
        <div class="mb-4 bg-light p-3 rounded">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">Rango de fechas</label>
              <div class="d-flex gap-2">
                <input type="date" class="form-control" 
                       v-model="filtroFechaInicio"
                       :max="filtroFechaFin || new Date().toISOString().split('T')[0]"
                       @change="cargarAccesos">
                <input type="date" class="form-control"
                       v-model="filtroFechaFin"
                       :min="filtroFechaInicio"
                       :max="new Date().toISOString().split('T')[0]"
                       @change="cargarAccesos">
              </div>
            </div>
            <div class="col-md-2">
              <label class="form-label d-block">&nbsp;</label>
              <button class="btn btn-secondary w-100" @click="limpiarFiltros">
                <i class="fas fa-times me-2"></i>
                Limpiar
              </button>
            </div>
          </div>
        </div>

        <!-- Estadísticas generales -->
      <div class="container my-4">
        <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-5 g-2">
          <div class="col">
            <div class="card h-90">
              <div class="card-body py-2 mb-4">     
                <h6 class="text-muted mb-4 text-center">Total Accesos</h6>
                <div class="d-flex align-items-center">
                  <i class="fas fa-users fs-3 text-primary me-3"></i>
                  <div class="h3 mb-0">{{ accesos.filter(acceso => {
                    const fechaAcceso = new Date(acceso.fecha);
                    if (filtroFechaInicio && filtroFechaFin) {
                      return fechaAcceso >= new Date(filtroFechaInicio) && 
                             fechaAcceso <= new Date(filtroFechaFin + 'T23:59:59');
                    } else if (filtroFechaInicio) {
                      return fechaAcceso >= new Date(filtroFechaInicio);
                    } else if (filtroFechaFin) {
                      return fechaAcceso <= new Date(filtroFechaFin + 'T23:59:59');
                    }
                    return true;
                  }).length }}</div>
                </div>
              </div>
            </div>
          </div>
            <div class="col">
              <div class="card h-90">
                <div class="card-body py-2">
                <h6 class="text-muted mb-4 text-center">Récord Mensual</h6>
                <div class="d-flex align-items-center">
                  <i class="fas fa-trophy fs-3 text-success me-3"></i>
                  <div>
                    <div class="h3 mb-0">{{ stats.maximoMensual }} días</div>
                    <small class="text-muted">{{ stats.maximoMensualFecha }}</small>
                  </div>
                </div>
              </div>
            </div>
          </div>
            <div class="col">
              <div class="card h-90">
                <div class="card-body py-2">
                <h6 class="text-muted mb-4 text-center">Hora Favorita</h6>
                <div class="d-flex align-items-center mt-2">
                  <i class="fas fa-clock fs-3 text-warning me-3"></i>
                  <div>
                    <div class="h3 mb-0">{{ stats.horaPico }}</div>
                    <small class="text-muted">{{ stats.personasHoraPico }} accesos</small>
                  </div>
                </div>
              </div>
            </div>
          </div>
            <div class="col">
              <div class="card h-90">
                <div class="card-body py-2">
                <h6 class="text-muted mb-4 text-center">Duración Media</h6>
                <div class="d-flex align-items-center mt-2">
                  <i class="fas fa-clock fs-3 text-info me-3"></i>
                  <div>
                    <div class="h3 mb-0">{{ stats.mediaAcceso }}</div>
                    <small class="text-muted">por acceso</small>
                  </div>
                </div>
              </div>
            </div>
          </div>
            <div class="col">
              <div class="card h-90">
                <div class="card-body py-2">
                <h6 class="text-muted mb-4 text-center">Días Consecutivos</h6>
                <div class="d-flex align-items-center mt-2">
                  <i class="fas fa-fire fs-3 text-danger me-3"></i>
                  <div>
                    <div class="h3 mb-0">{{ rachaActual }}</div>
                    <small class="text-muted">Récord: {{ diasConsecutivos }}</small>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

        <!-- Gráfico de ocupación por horas -->
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-body">
            <h5 class="card-title mb-4">
              ¿A qué horas he entrado?
              <span v-if="filtroFechaInicio || filtroFechaFin" class="text-secondary" style="font-size: 1rem; font-weight: normal;">
                (
                <template v-if="filtroFechaInicio && filtroFechaFin">
                  {{ formatearFechaFiltro(filtroFechaInicio) }} a {{ formatearFechaFiltro(filtroFechaFin) }}
                </template>
                <template v-else-if="filtroFechaInicio">
                  Desde {{ formatearFechaFiltro(filtroFechaInicio) }}
                </template>
                <template v-else-if="filtroFechaFin">
                  Hasta {{ formatearFechaFiltro(filtroFechaFin) }}
                </template>
                )
              </span>
            </h5>
            <div class="chart-container" style="position: relative; height:300px;">
              <canvas ref="ocupacionChart"></canvas>
            </div>
          </div>
        </div>

        <!-- Calendario de accesos -->
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-body">
            <h5 class="card-title mb-4">
              Mis Accesos
              <span v-if="filtroFechaInicio || filtroFechaFin" class="text-secondary" style="font-size: 1rem; font-weight: normal;">
                (
                <template v-if="filtroFechaInicio && filtroFechaFin">
                  {{ formatearFechaFiltro(filtroFechaInicio) }} a {{ formatearFechaFiltro(filtroFechaFin) }}
                </template>
                <template v-else-if="filtroFechaInicio">
                  Desde {{ formatearFechaFiltro(filtroFechaInicio) }}
                </template>
                <template v-else-if="filtroFechaFin">
                  Hasta {{ formatearFechaFiltro(filtroFechaFin) }}
                </template>
                )
              </span>
            </h5>
            <!-- Componente Calendario -->
            <Calendario
              :accesos="accesos"
              :current-month="currentMonth"
              :filtro-fecha-inicio="filtroFechaInicio"
              :filtro-fecha-fin="filtroFechaFin"
            />
          </div>
        </div>

        <!-- Lista de accesos -->
        <div class="card shadow-sm border-0">
          <div class="card-body">
            <h5 class="card-title mb-4">Registro de Accesos</h5>
            
            <div v-if="accesos.length === 0" class="text-center py-5">
              <i class="fas fa-door-closed fs-1 text-muted mb-3"></i>
              <h5>Sin registros de acceso</h5>
              <p class="text-muted">No hay accesos registrados en el período seleccionado.</p>
            </div>

            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead>
                  <tr>
                    <th class="cursor-pointer" @click="ordenarPor('fecha')">
                      Fecha
                      <i :class="['fas', {
                        'fa-sort': ordenamiento.columna !== 'fecha',
                        'fa-sort-up': ordenamiento.columna === 'fecha' && ordenamiento.direccion === 'asc',
                        'fa-sort-down': ordenamiento.columna === 'fecha' && ordenamiento.direccion === 'desc'
                      }]"></i>
                    </th>
                    <th>Hora Entrada</th>
                    <th>Hora Salida</th>
                    <th>Duración</th>
                  </tr>
                </thead>
                <tbody>
                  <template v-for="grupo in accesosPorDia" :key="grupo.fecha">
                    <tr>
                      <td class="fw-bold">{{ formatearFecha(grupo.fecha) }}</td>
                      <td>{{ formatearHora(grupo.horaEntrada) }}</td>
                      <td>
                        <template v-if="grupo.horaSalida">
                          {{ formatearHora(grupo.horaSalida) }}
                        </template>
                        <span v-else class="text-muted">-</span>
                      </td>
                      <td>
                        <template v-if="grupo.duracion">
                          {{ formatearDuracion(grupo.duracion) }}
                        </template>
                        <span v-else class="badge bg-warning">En curso</span>
                      </td>
                    </tr>
                  </template>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script>
import Chart from 'chart.js/auto';
import Calendario from '../components/Calendario.vue';

export default {
  name: 'UserAccesos',
  components: { Calendario },
  data() {
    return {
      accesos: [],
      diasConsecutivos: 0,
      rachaActual: 0,
      currentMonth: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
      chart: null,
      gimnasio: null,
      filtroFechaInicio: '',
      filtroFechaFin: '',
      ordenamiento: {
        columna: 'fecha',
        direccion: 'desc'
      },
      stats: {
        mediaDiaria: 0,
        maximoMensual: 0,
        maximoMensualFecha: '',
        horaPico: '',
        personasHoraPico: 0,
        mediaAcceso: 0
      }
    }
  },
  computed: {

    accesosPorDia() {
      const grupos = {};
      
      const accesosOrdenados = [...this.accesos].sort((a, b) => {
        return new Date(b.horaEntrada) - new Date(a.horaEntrada);
      });

      accesosOrdenados.forEach(acceso => {
        const fecha = new Date(acceso.horaEntrada).toISOString().split('T')[0];
        if (!grupos[fecha]) {
          grupos[fecha] = {
            fecha: acceso.horaEntrada,
            horaEntrada: acceso.horaEntrada,
            horaSalida: acceso.horaSalida,
            duracion: acceso.duracion
          };
        }
      });

      // Convertir a array y ordenar por fecha
      return Object.values(grupos).sort((a, b) => {
        return this.ordenamiento.direccion === 'asc' 
          ? new Date(a.horaEntrada) - new Date(b.horaEntrada)
          : new Date(b.horaEntrada) - new Date(a.horaEntrada);
      });
    }
  },

  methods: {

    formatearFechaFiltro(fechaStr) {
      if (!fechaStr) return '';
      const partes = fechaStr.split('-');
      if (partes.length === 3) {
        return `${partes[2]}-${partes[1]}-${partes[0]}`;
      }
      return fechaStr;
    },

    formatMonth(date) {
      return new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' }).format(date);
    },

    ordenarPor(columna) {
      if (this.ordenamiento.columna === columna) {
        this.ordenamiento.direccion = this.ordenamiento.direccion === 'asc' ? 'desc' : 'asc';
      } else {
        this.ordenamiento.columna = columna;
        this.ordenamiento.direccion = 'asc';
      }
    },
    async cargarGimnasio() {
      try {
        const idGimnasio = localStorage.getItem('gimnasioId');
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}`, {
          headers: { 'Authorization': authHeader },
          credentials: 'include'
        });
        if (response.ok) {
          this.gimnasio = await response.json();
        }
      } catch (e) {
        console.error('Error al cargar información del gimnasio:', e);
      }
    },

    async cargarAccesos() {
      try {
        const email = localStorage.getItem('email');
        if (!email) {
          console.error('No hay usuario logueado');
          return;
        }

        let url = `http://localhost:8080/gestiongimnasios/usuarios/${email}/accesos`;
        if (this.filtroFechaInicio || this.filtroFechaFin) {
          const params = new URLSearchParams();
          if (this.filtroFechaInicio) {
            params.append('fechaInicio', this.filtroFechaInicio);
          }
          if (this.filtroFechaFin) {
            params.append('fechaFin', this.filtroFechaFin);
          }
          url += '?' + params.toString();
        }

        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(url, {
          headers: { 'Authorization': authHeader },
          credentials: 'include'
        });
        
        if (response.ok) {
          const data = await response.json();
          this.accesos = data.map(acceso => ({
            id: acceso.id,
            fecha: acceso.horaEntrada,
            horaEntrada: acceso.horaEntrada,
            horaSalida: acceso.horaSalida,
            duracion: acceso.horaSalida ? this.calcularDuracionEnMinutos(acceso.horaEntrada, acceso.horaSalida) : null,
            nombreGimnasio: acceso.nombreGimnasio
          }));
          // No modificar currentMonth aquí, para que el calendario siempre muestre hasta el mes actual
          this.calcularEstadisticas();
          this.actualizarGraficoOcupacion();
        }
      } catch (error) {
        console.error('Error al cargar accesos:', error);
      }
    },

    limpiarFiltros() {
      this.filtroFechaInicio = '';
      this.filtroFechaFin = '';
      this.currentMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1);
      this.cargarAccesos();
    },

    actualizarGraficoOcupacion() {
      if (this.chart) {
        this.chart.destroy();
      }

      const ctx = this.$refs.ocupacionChart.getContext('2d');
      
      // Si no tenemos información del gimnasio, usamos horario completo
      let horasDelDia = Array.from({length: 24}, (_, i) => `${i}:00`);
      
      // Si tenemos información del gimnasio, filtramos por su horario
      if (this.gimnasio && this.gimnasio.horaApertura && this.gimnasio.horaCierre) {
        const horaApertura = parseInt(this.gimnasio.horaApertura.split(':')[0]);
        const horaCierre = parseInt(this.gimnasio.horaCierre.split(':')[0]);
        
        // Si el horario de cierre es menor que el de apertura, significa que cierra al día siguiente
        const horas = horaCierre <= horaApertura ? 
          [...Array(24).keys()].filter(h => h >= horaApertura || h <= horaCierre) :
          [...Array(24).keys()].filter(h => h >= horaApertura && h <= horaCierre);
          
        horasDelDia = horas.map(h => `${h}:00`);
      }
      
      const datos = Array(horasDelDia.length).fill(0);

      const accesosEnRango = this.accesos.filter(acceso => {
        const fechaAcceso = new Date(acceso.fecha);
        if (this.filtroFechaInicio && this.filtroFechaFin) {
          return fechaAcceso >= new Date(this.filtroFechaInicio) && 
                 fechaAcceso <= new Date(this.filtroFechaFin + 'T23:59:59');
        } else if (this.filtroFechaInicio) {
          return fechaAcceso >= new Date(this.filtroFechaInicio);
        } else if (this.filtroFechaFin) {
          return fechaAcceso <= new Date(this.filtroFechaFin + 'T23:59:59');
        }
        return true;
      });

      accesosEnRango.forEach(acceso => {
        const hora = new Date(acceso.fecha).getHours();
        const index = horasDelDia.indexOf(`${hora}:00`);
        if (index !== -1) {
          datos[index]++;
        }
      });

      this.chart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: horasDelDia,
          datasets: [{
            label: 'Nº Accesos',
            data: datos,
            backgroundColor: 'rgba(54, 162, 235, 0.5)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                stepSize: 1
              }
            }
          },
          plugins: {
            legend: {
              display: false
            }
          }
        }
      });
    },
    calcularEstadisticas() {
      if (!this.accesos || !Array.isArray(this.accesos)) {
        return;
      }

      // Filtrar accesos por rango de fechas si está establecido
      const accesosEnRango = this.accesos.filter(acceso => {
        const fechaAcceso = new Date(acceso.fecha);
        if (this.filtroFechaInicio && this.filtroFechaFin) {
          return fechaAcceso >= new Date(this.filtroFechaInicio) && 
                 fechaAcceso <= new Date(this.filtroFechaFin + 'T23:59:59');
        } else if (this.filtroFechaInicio) {
          return fechaAcceso >= new Date(this.filtroFechaInicio);
        } else if (this.filtroFechaFin) {
          return fechaAcceso <= new Date(this.filtroFechaFin + 'T23:59:59');
        }
        return true;
      });

      let totalDias;
      if (this.filtroFechaInicio && this.filtroFechaFin) {
        const inicio = new Date(this.filtroFechaInicio);
        const fin = new Date(this.filtroFechaFin);
        totalDias = Math.ceil((fin - inicio) / (1000 * 60 * 60 * 24)) + 1;
      } else {
        const diasUnicos = new Set(accesosEnRango.map(a => new Date(a.fecha).toDateString())).size;
        totalDias = diasUnicos;
      }
      this.stats.mediaDiaria = totalDias ? Math.round((accesosEnRango.length / totalDias) * 10) / 10 : 0;

      // Calcular máximo mensual
      const accesosPorMes = {};
      accesosEnRango.forEach(acceso => {
        const fecha = new Date(acceso.fecha);
        const mesKey = `${fecha.getFullYear()}-${fecha.getMonth()}`;
        const fechaDia = fecha.toDateString();
        
        if (!accesosPorMes[mesKey]) {
          accesosPorMes[mesKey] = new Set();
        }
        accesosPorMes[mesKey].add(fechaDia);
      });

      let maxDiasMes = 0;
      let mesFechaMax = '';
      Object.entries(accesosPorMes).forEach(([mesKey, diasSet]) => {
        if (diasSet.size > maxDiasMes) {
          maxDiasMes = diasSet.size;
          mesFechaMax = mesKey;
        }
      });

      this.stats.maximoMensual = maxDiasMes;
      if (mesFechaMax) {
        const [año, mes] = mesFechaMax.split('-');
        this.stats.maximoMensualFecha = new Date(parseInt(año), parseInt(mes))
          .toLocaleDateString('es-ES', { 
            month: 'long',
            year: 'numeric'
          });
      } else {
        this.stats.maximoMensualFecha = '';
      }

      const accesosPorHora = Array(24).fill(0);
      accesosEnRango.forEach(acceso => {
        const hora = new Date(acceso.fecha).getHours();
        accesosPorHora[hora]++;
      });

      let maxPorHora = 0;
      let horaPico = 0;
      accesosPorHora.forEach((cantidad, hora) => {
        if (cantidad > maxPorHora) {
          maxPorHora = cantidad;
          horaPico = hora;
        }
      });

      this.stats.personasHoraPico = maxPorHora;
      this.stats.horaPico = `${horaPico}:00`;

      this.diasConsecutivos = this.calcularDiasConsecutivos();


      let duracionTotalAccesos = 0;
      let accesosConDuracion = 0;
      accesosEnRango.forEach(acceso => {
        if (acceso.duracion) {
          duracionTotalAccesos += acceso.duracion;
          accesosConDuracion++;
        }
      });
      
      if (accesosConDuracion > 0) {
        const mediaDuracion = Math.round(duracionTotalAccesos / accesosConDuracion);
        this.stats.mediaAcceso = this.formatearDuracion(mediaDuracion);
      } else {
        this.stats.mediaAcceso = '0min';
      }
    },
    calcularDiasConsecutivos() {
      if (!this.accesos || !Array.isArray(this.accesos) || this.accesos.length === 0) {
        this.rachaActual = 0;
        return 0;
      }

      const fechasUnicas = [...new Set(
        this.accesos.map(a => new Date(a.fecha).toDateString())
      )].map(f => new Date(f)).sort((a, b) => b - a);

      const hoy = new Date().toDateString();
      const ayer = new Date(Date.now() - 86400000).toDateString();
      const ultimaFecha = new Date(fechasUnicas[0]).toDateString();

      // Calcular racha actual
      if (ultimaFecha !== hoy && ultimaFecha !== ayer) {
        this.rachaActual = 0;
      } else {
        this.rachaActual = 1;
        for (let i = 1; i < fechasUnicas.length; i++) {
          const diffDias = Math.floor(
            (new Date(fechasUnicas[i-1]) - new Date(fechasUnicas[i])) / (1000 * 60 * 60 * 24)
          );
          if (diffDias === 1) {
            this.rachaActual++;
          } else {
            break;
          }
        }
      }

      // Calcular récord histórico
      let maxConsecutivos = 1;
      let consecutivosTemp = 1;
      
      for (let i = 1; i < fechasUnicas.length; i++) {
        const diffDias = Math.floor(
          (new Date(fechasUnicas[i-1]) - new Date(fechasUnicas[i])) / (1000 * 60 * 60 * 24)
        );
        
        if (diffDias === 1) {
          consecutivosTemp++;
          maxConsecutivos = Math.max(maxConsecutivos, consecutivosTemp);
        } else {
          consecutivosTemp = 1;
        }
      }

      return maxConsecutivos;
    },

    formatearFecha(fecha) {
      return new Date(fecha).toLocaleDateString('es-ES', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    },

    formatearHora(fecha) {
      return new Date(fecha).toLocaleTimeString('es-ES', {
        hour: '2-digit',
        minute: '2-digit'
      });
    },

    formatearDuracion(minutos) {
      const horas = Math.floor(minutos / 60);
      const minutosRestantes = minutos % 60;
      
      if (horas > 0) {
        return `${horas}h ${minutosRestantes}min`;
      }
      return `${minutosRestantes}min`;
    },

    calcularDuracionEnMinutos(horaEntrada, horaSalida) {
      const entrada = new Date(horaEntrada);
      const salida = new Date(horaSalida);
      return Math.round((salida - entrada) / (1000 * 60)); // Convertir milisegundos a minutos
    }
  },
  mounted() {
    this.cargarGimnasio().then(() => {
      this.cargarAccesos()
    })
  }
}
</script>

<style>


.table th {
  letter-spacing: 0.5px;
  padding: 1rem 0.75rem;
  vertical-align: middle;
}

.table {
  border-radius: 12px;
  overflow: hidden;
  width: 100%;
}

.table-responsive {
  -webkit-overflow-scrolling: touch;
  max-width: 100%;
}

@media (max-width: 768px) {
  .calendar-day {
    font-size: 0.875rem;
  }
}
</style>