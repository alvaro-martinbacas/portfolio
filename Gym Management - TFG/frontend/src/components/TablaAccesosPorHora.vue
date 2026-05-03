<template>
  <div class="card shadow-sm border-0 mb-4">
    <div class="card-body">
      <h5 class="card-title mb-3">
        Accesos del gimnasio por horas
        <span v-if="filtroFechaInicio || filtroFechaFin" class="text-secondary" style="font-size: 1rem; font-weight: normal;">
          (
          <template v-if="filtroFechaInicio && filtroFechaFin">
            {{ formatearFechaFiltro(filtroFechaInicio) }} - {{ formatearFechaFiltro(filtroFechaFin) }}
          </template>
          <template v-else-if="filtroFechaInicio">
            desde: {{ formatearFechaFiltro(filtroFechaInicio) }}
          </template>
          <template v-else-if="filtroFechaFin">
            hasta: {{ formatearFechaFiltro(filtroFechaFin) }}
          </template>
          )
        </span>
      </h5>
      <div class="row g-3 align-items-end mb-3">
        <div class="col-md-5">
          <div class="d-flex gap-2">
            <input type="date" class="form-control" v-model="fechaInicio" :max="hoy" @change="onFiltroChange">
            <input type="date" class="form-control" v-model="fechaFin" :min="fechaInicio" :max="hoy" @change="onFiltroChange">
          </div>
        </div>
        <div class="col-md-2 d-flex align-items-end" v-if="fechaInicio || fechaFin">
          <button class="btn btn-outline-secondary mb-3" @click="limpiarFiltros">Limpiar</button>
        </div>
      </div>
      <div class="chart-container chart-scroll-x">
        <canvas ref="graficoAccesos" class="ocupacion-canvas"></canvas>
      </div>
    </div>
  </div>
</template>

<script>
import Chart from 'chart.js/auto';

export default {
  name: 'TablaAccesosPorHora',
  props: {
    accesos: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      chart: null,
      fechaInicio: '',
      fechaFin: '',
      filtroFechaInicio: '',
      filtroFechaFin: '',
      hoy: new Date().toISOString().split('T')[0]
    };
  },
  watch: {
    accesos: {
      handler() {
        this.$nextTick(() => {
          this.dibujarGrafico();
        });
      },
      deep: true
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
    limpiarFiltros() {
      this.fechaInicio = '';
      this.fechaFin = '';
      this.filtroFechaInicio = '';
      this.filtroFechaFin = '';
      this.onFiltroChange();
      this.dibujarGrafico();
    },
    onFiltroChange() {
      this.filtroFechaInicio = this.fechaInicio;
      this.filtroFechaFin = this.fechaFin;
      this.$emit('filtro-change', {
        fechaInicio: this.fechaInicio,
        fechaFin: this.fechaFin
      });
      this.dibujarGrafico();
    },
    dibujarGrafico() {
      if (this.chart) this.chart.destroy();

      const ctx = this.$refs.graficoAccesos.getContext('2d');
      const horas = Array.from({length: 24}, (_, i) => `${i}:00`);
      const datos = Array(24).fill(0);

      // Filtrar accesos por rango de fechas si hay filtro
      let accesosFiltrados = [...this.accesos];
      if (this.filtroFechaInicio || this.filtroFechaFin) {
        const inicio = this.filtroFechaInicio ? new Date(this.filtroFechaInicio + 'T00:00:00') : null;
        const fin = this.filtroFechaFin ? new Date(this.filtroFechaFin + 'T23:59:59') : null;
        accesosFiltrados = accesosFiltrados.filter(acceso => {
          const fecha = new Date(acceso.horaEntrada || acceso.fecha);
          if (inicio && fecha < inicio) return false;
          if (fin && fecha > fin) return false;
          return true;
        });
      }
      
      accesosFiltrados.forEach(acceso => {
        const hora = new Date(acceso.horaEntrada || acceso.fecha).getHours();
        datos[hora]++;
      });

      this.chart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: horas,
          datasets: [{
            label: 'Media de accesos',
            data: datos,
            backgroundColor: 'rgba(54, 162, 235, 0.5)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1,
            barPercentage: window.innerWidth <= 768 ? 2.0 : 0.6,
            categoryPercentage: window.innerWidth <= 768 ? 0.5 : 0.7
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: {
              ticks: {
                font: {
                  size: window.innerWidth <= 768 ? 15 : 12
                }
              }
            },
            y: {
              beginAtZero: true,
              ticks: {
                stepSize: 1,
                font: {
                  size: window.innerWidth <= 768 ? 14 : 12
                }
              }
            }
          },
          plugins: {
            legend: { display: false }
          }
        }
      });
    }
  },
  watch: {
    accesos: {
      handler() {
        this.$nextTick(() => {
          this.dibujarGrafico();
        });
      },
      deep: true
    }
  },
  mounted() {
    this.dibujarGrafico();
  },
  beforeUnmount() {
    if (this.chart) this.chart.destroy();
  }
};
</script>
