<template>
  <div class="background-container">
    <!-- Imagen de fondo -->
    <div class="background-image bg-fondo7"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="container py-4 px-3 position-relative" style="z-index: 10;">
      <div class="row g-3 mb-4">
        <!-- Panel de Aforo Actual -->
        <div class="col-12 col-md-6 col-lg-4">
          <div class="card h-90 border-0 shadow-sm" :class="[colorAforo, {'loading': cargandoAforo}]">
          <div class="card-body text-center py-4">
            <div v-if="cargandoAforo" class="d-flex flex-column align-items-center gap-3">
              <i class="fas fa-spinner fa-spin fs-1"></i>
              <span>Cargando...</span>
            </div>
            <div v-else>
              <h2 class="display-4 fw-bold mb-2 aforo-number">{{ aforoActual }}</h2>
              <h6 class="text-muted mb-2">Personas ahora</h6>
              <div class="d-flex align-items-center justify-content-center gap-2 text-muted">
                <i class="fas fa-clock"></i>
                {{ horaActual }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Hora Pico -->
      <div v-if="stats.horaMasComun !== null" class="col-12 col-md-6 col-lg-4">
        <div class="card h-80 border-0 shadow-sm">
          <div class="card-body text-center py-4">
            <div class="icon-wrapper peak-icon mx-auto mb-3">
              <i class="fas fa-users"></i>
            </div>
            <h6 class="text-uppercase fw-bold mb-3" style="color: black">Hora Pico</h6>
            <h3 class="mb-3" style="color: black">{{ stats.horaMasComun }}</h3>
            <div class="badge bg-light text-dark px-3 py-2">
              <i class="fas fa-chart-line text-primary me-2"></i>
              {{ stats.accesosHoraMasComun }} visitas
            </div>
          </div>
        </div>
      </div>

      <!-- Hora Tranquila -->
      <div v-if="stats.horaMenosComun !== null" class="col-12 col-md-6 col-lg-4">
        <div class="card h-80 border-0 shadow-sm">
          <div class="card-body text-center py-4">
            <div class="icon-wrapper quiet-icon mx-auto mb-3">
              <i class="fas fa-user-clock"></i>
            </div>
            <h6 class="text-uppercase fw-bold mb-3" style="color: black">Hora Tranquila</h6>
            <h3 class="mb-3" style="color: black">{{ stats.horaMenosComun }}</h3>
            <div class="badge bg-light text-dark px-3 py-2">
              <i class="fas fa-chart-line text-primary me-2"></i>
              {{ stats.accesosHoraMenosComun }} visitas
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Gráfico de ocupación por horas -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body">
        <h5 class="card-title">¿A qué horas suelen venir los usuarios?</h5>
        <div class="chart-container" style="position: relative; height:400px;">
          <canvas ref="ocupacionChart"></canvas>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script>
import Chart from 'chart.js/auto';

export default {
  name: 'UserAforo',
  data() {
    return {
      aforoActual: '-',
      cargandoAforo: true,
      accesos: [],
      horaActual: this.obtenerHoraActual(),
      chart: null,
      filtroFechaInicio: '',
      filtroFechaFin: '',
      gimnasio: null,
      stats: {
        horaMasComun: null,
        accesosHoraMasComun: null,
        horaMenosComun: null,
        accesosHoraMenosComun: null
      }
    }
  },
  beforeUnmount() {
    if (this._intervalHora) clearInterval(this._intervalHora);
  },
  methods: {
    obtenerHoraActual() {
      const ahora = new Date();
      return ahora.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
    },
    formatearFechaFiltro(fechaStr) {
      if (!fechaStr) return '';
      const partes = fechaStr.split('-');
      if (partes.length === 3) {
        return `${partes[2]}-${partes[1]}-${partes[0]}`;
      }
      return fechaStr;
    },
    async obtenerGimnasioUsuario() {
      try {
        const email = localStorage.getItem('email');
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/usuarios/${email}/gimnasio`, {
          headers: {
            'Authorization': authHeader
          }
        });
        
        if (!response.ok) {
          throw new Error('Error al obtener el gimnasio del usuario');
        }

        const gimnasio = await response.json();
        return gimnasio.id;
      } catch (error) {
        console.error('Error al obtener gimnasio:', error);
        return null;
      }
    },
    async cargarAforoActual() {
      this.cargandoAforo = true;
      try {
        let idGimnasio = localStorage.getItem('gimnasioId');
        if (!idGimnasio || idGimnasio === 'null') {
          idGimnasio = await this.obtenerGimnasioUsuario();
          if (idGimnasio) {
            localStorage.setItem('gimnasioId', idGimnasio);
          }
        }
        
        if (!idGimnasio) {
          this.aforoActual = '-';
          return;
        }

        const fechaHora = new Date().toISOString().split('.')[0];
        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/aforo?fechaHora=${encodeURIComponent(fechaHora)}`, {
          headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
          const data = await response.json();
          this.aforoActual = data.aforo !== undefined ? data.aforo : '-';
        } else {
          this.aforoActual = '-';
        }
      } catch (e) {
        this.aforoActual = '-';
      } finally {
        this.cargandoAforo = false;
      }
    },
    async cargarGimnasio() {
      try {
        let idGimnasio = localStorage.getItem('gimnasioId');
        if (!idGimnasio || idGimnasio === 'null') {
          idGimnasio = await this.obtenerGimnasioUsuario();
          if (idGimnasio) {
            localStorage.setItem('gimnasioId', idGimnasio);
          }
        }
        
        if (!idGimnasio) {
          return;
        }

        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}`, {
          headers: { 'Authorization': authHeader }
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
        let idGimnasio = localStorage.getItem('gimnasioId');
        if (!idGimnasio || idGimnasio === 'null') {
          idGimnasio = await this.obtenerGimnasioUsuario();
          if (idGimnasio) {
            localStorage.setItem('gimnasioId', idGimnasio);
          }
        }
        
        if (!idGimnasio) {
          this.accesos = [];
          this.actualizarGraficoOcupacion();
          return;
        }

        const token = localStorage.getItem('token');
        const authHeader = 'Bearer ' + token;
        const response = await fetch(`http://localhost:8080/gestiongimnasios/gimnasios/${idGimnasio}/accesos-sin-usuario`, {
          headers: { 'Authorization': authHeader }
        });
        if (!response.ok) throw new Error('Error al cargar accesos');
        this.accesos = await response.json();
        this.actualizarGraficoOcupacion();
      } catch (e) {
        this.accesos = [];
        this.actualizarGraficoOcupacion();
      }
    },
    onFiltroChange({ fechaInicio, fechaFin }) {
      // Si es necesario, puedes hacer algo con los filtros aquí
      this.cargarAccesos();
    },
    limpiarFiltros() {
      this.filtroFechaInicio = '';
      this.filtroFechaFin = '';
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

      // Filtrar accesos por rango de fechas
      const accesosEnRango = this.accesos.filter(acceso => {
        const fechaAcceso = new Date(acceso.horaEntrada || acceso.fecha);
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

      // Procesar datos
      accesosEnRango.forEach(acceso => {
        const hora = new Date(acceso.horaEntrada || acceso.fecha).getHours();
        const index = horasDelDia.indexOf(`${hora}:00`);
        if (index !== -1) {
          datos[index]++;
        }
      });

      // Calcular estadísticas
      let max = Math.max(...datos);
      let min = Math.min(...datos.filter(v => v > 0));
      let horaMasComun = null, accesosHoraMasComun = null;
      let horaMenosComun = null, accesosHoraMenosComun = null;
      
      if (max > 0) {
        horaMasComun = horasDelDia[datos.indexOf(max)];
        accesosHoraMasComun = max;
      }
      if (min !== Infinity) {
        horaMenosComun = horasDelDia[datos.indexOf(min)];
        accesosHoraMenosComun = min;
      }

      // Actualizar estadísticas
      this.stats = {
        horaMasComun,
        accesosHoraMasComun,
        horaMenosComun,
        accesosHoraMenosComun
      };

      // Crear el gráfico
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
    onDatosProcesados({ datos, horas }) {
      let max = Math.max(...datos);
      let min = Math.min(...datos.filter(v => v > 0));
      let horaMasComun = null, accesosHoraMasComun = null;
      let horaMenosComun = null, accesosHoraMenosComun = null;
      
      if (max > 0) {
        horaMasComun = horas[datos.indexOf(max)];
        accesosHoraMasComun = max;
      }
      if (min !== Infinity) {
        horaMenosComun = horas[datos.indexOf(min)];
        accesosHoraMenosComun = min;
      }
      
      this.stats = {
        horaMasComun,
        accesosHoraMasComun,
        horaMenosComun,
        accesosHoraMenosComun
      };
    }
  },
  computed: {
    colorAforo() {
      if (this.aforoActual === '-' || isNaN(this.aforoActual)) return 'aforo-desconocido';
      const valor = Number(this.aforoActual);
      if (valor <= 5) return 'aforo-bajo';
      if (valor <= 15) return 'aforo-medio';
      if (valor <= 30) return 'aforo-alto';
      return 'aforo-muyalto';
    }
  },
  mounted() {
    this.cargarGimnasio().then(() => {
      this.cargarAforoActual();
      this.cargarAccesos();
    });
    
    // Actualizar aforo cada minuto
    this._intervalAforo = setInterval(() => {
      this.cargarAforoActual();
    }, 60000);
  },
  beforeUnmount() {
    if (this._intervalHora) clearInterval(this._intervalHora);
    if (this._intervalAforo) clearInterval(this._intervalAforo);
  }
}

</script>

<style scoped>
/* Estilos del aforo */
.aforo-number {
  background: linear-gradient(45deg, #2c3e50, #3498db);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* Iconos circulares */
.icon-wrapper {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  position: relative;
}

.icon-wrapper i {
  font-size: 1.5rem;
  z-index: 1;
}

.peak-icon {
  background: linear-gradient(135deg, #6edcc4, #1aab8b);
  color: white;
}

.quiet-icon {
  background: linear-gradient(135deg, #8e9eab, #5c6bc0);
  color: white;
}

@media (max-width: 768px) {
  .time-value {
    font-size: 1.8rem;
  }
  
  .icon-wrapper {
    width: 60px;
    height: 60px;
  }
  
  .visits-count {
    font-size: 0.9rem;
    padding: 0.4rem 0.8rem;
  }
  
  .stat-title {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0.5rem !important;
  }
  
  .stats-card {
    border-radius: 12px;
  }
  
  .card-content {
    padding: 1rem;
  }
  
  .time-value {
    font-size: 1.6rem;
  }
  
  .icon-wrapper {
    width: 50px;
    height: 50px;
  }
  
  .icon-wrapper i {
    font-size: 1.4rem;
  }
}

.visits-count {
  font-size: 1rem;
  color: #666;
  padding: 0.5rem 1rem;
  background: linear-gradient(to right, #f8f9fa, #e9ecef);
  border-radius: 20px;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.visits-count i {
  font-size: 0.9rem;
  color: #3498db;
}


/* Loading State */
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 2rem;
}

.loading-spinner i {
  font-size: 2.5rem;
  color: #3498db;
}

/* Colores para Aforo */
.aforo-bajo { background: linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%); }
.aforo-medio { background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%); }
.aforo-alto { background: linear-gradient(135deg, #f6d365 0%, #fda085 100%); }
.aforo-muyalto { background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%); }
.aforo-desconocido { background: linear-gradient(135deg, #e0e0e0 0%, #f5f5f5 100%); }

.loading .loading-spinner {
  animation: pulse 1.5s ease-in-out infinite;
}

.aforo-info {
  animation: fadeIn 0.5s ease-out;
}

</style>
