<template>
  <div class="contenedor-calendario">
    <div class="envoltorio-calendario" ref="calendarWrapper">
      <div
        v-for="(mes, indice) in meses"
        :key="indice"
        class="mes-calendario"
      >
        <h3 class="titulo-mes">{{ mes.nombre }} {{ mes.año }}</h3>
        <div class="grilla-calendario">
          <div class="encabezado-dia" v-for="dia in ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom']" :key="dia">
            {{ dia }}
          </div>
          <div
            class="dia-calendario"
            v-for="(dia, idx) in mes.dias"
            :key="idx"
            :class="{ 
              'fuera-mes': !dia.esDelMes, 
              'tiene-acceso': dia.esDelMes && dia.accesos > 0, 
              'hoy': dia.esHoy 
            }"
            :title="dia.accesos > 0 ? `${dia.accesos} accesos` : ''"
          >
            <span v-if="dia.esDelMes">{{ dia.dia }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Calendario',
  props: {
    accesos: {
      type: Array,
      default: () => []
    },
    currentMonth: {
      type: Date,
      default: () => new Date()
    },
    filtroFechaInicio: {
      type: String,
      default: ''
    },
    filtroFechaFin: {
      type: String,
      default: ''
    },
    soloMesActual: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      meses: [],
      mesActual: this.currentMonth.getMonth(),
      añoActual: this.currentMonth.getFullYear()
    };
  },
  watch: {
    currentMonth(newVal) {
      this.mesActual = newVal.getMonth();
      this.añoActual = newVal.getFullYear();
      this.generarCalendarios();
    },
    accesos: {
      handler() {
        this.generarCalendarios();
      },
      deep: true
    }
  },
  mounted() {
    this.generarCalendarios();
    this.$nextTick(() => {
      this.desplazarAMesActual();
    });
  },
  methods: {
    generarCalendarios() {
      this.meses = [];
      
      // Si solo queremos mostrar el mes actual (para dashboard)
      if (this.soloMesActual) {
        this.meses.push(this.generarDatosMes(this.currentMonth));
        return;
      }
      
      // Si hay filtro de fechas, mostrar los meses según el rango
      if (this.filtroFechaInicio || this.filtroFechaFin) {
        const hoy = new Date();
        const unAnoAtras = new Date(hoy.getFullYear() - 1, hoy.getMonth(), 1);
        
        // Determinar mes y año de inicio y fin
        let inicio = this.filtroFechaInicio ? new Date(this.filtroFechaInicio) : unAnoAtras;
        let fin = this.filtroFechaFin ? new Date(this.filtroFechaFin) : hoy;

        // Si solo hay fecha de inicio, mostrar hasta hoy
        if (this.filtroFechaInicio && !this.filtroFechaFin) {
          fin = hoy;
        }
        // Si solo hay fecha de fin, mostrar desde hace un año o desde el inicio si es más reciente
        else if (!this.filtroFechaInicio && this.filtroFechaFin) {
          inicio = new Date(Math.max(unAnoAtras.getTime(), new Date(fin).setMonth(fin.getMonth() - 11)));
        }

        // Normalizar a primer día de mes
        inicio = new Date(inicio.getFullYear(), inicio.getMonth(), 1);
        fin = new Date(fin.getFullYear(), fin.getMonth(), 1);
        
        let actual = new Date(inicio);
        while (actual <= fin) {
          this.meses.push(this.generarDatosMes(new Date(actual)));
          // Siguiente mes
          actual.setMonth(actual.getMonth() + 1);
        }
      } else {
        // Sin filtro: mostrar hasta 12 meses atrás, nunca meses futuros
        const hoy = new Date();
        const mesActual = hoy.getMonth();
        const añoActual = hoy.getFullYear();
        const mesesAtras = 11;
        for (let i = mesesAtras; i >= 0; i--) {
          const fechaMes = new Date(añoActual, mesActual - i, 1);
          if (
            fechaMes.getFullYear() < añoActual ||
            (fechaMes.getFullYear() === añoActual && fechaMes.getMonth() <= mesActual)
          ) {
            this.meses.push(this.generarDatosMes(fechaMes));
          }
        }
      }
    },
    generarDatosMes(fecha) {
      const primerDia = new Date(fecha.getFullYear(), fecha.getMonth(), 1);
      const ultimoDia = new Date(fecha.getFullYear(), fecha.getMonth() + 1, 0);
      const diasEnMes = ultimoDia.getDate();
      
      // Ajustar para que el lunes sea el primer día (0 = domingo, 1 = lunes, ...)
      let primerDiaSemana = primerDia.getDay();
      primerDiaSemana = primerDiaSemana === 0 ? 6 : primerDiaSemana - 1; // Convertir domingo de 0 a 6
      
      const ultimoDiaMesAnterior = new Date(fecha.getFullYear(), fecha.getMonth(), 0);
      let dias = [];

      // Añadir días del mes anterior para completar la primera semana
      for (let i = primerDiaSemana - 1; i >= 0; i--) {
        const diaAnterior = ultimoDiaMesAnterior.getDate() - i;
        const d = new Date(fecha.getFullYear(), fecha.getMonth() - 1, diaAnterior);
        const fechaAnterior = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
        dias.push({
          esDelMes: false,
          dia: null,
          accesos: this.calcularAccesosPorFecha(fechaAnterior)
        });
      }

      // Calcular la fecha de hoy
      const hoy = new Date();
      const hoyStr = `${hoy.getFullYear()}-${String(hoy.getMonth() + 1).padStart(2, '0')}-${String(hoy.getDate()).padStart(2, '0')}`;
      
      // Añadir días del mes actual
      for (let dia = 1; dia <= diasEnMes; dia++) {
        const d = new Date(fecha.getFullYear(), fecha.getMonth(), dia);
        const fechaDia = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
        dias.push({
          esDelMes: true,
          dia: dia,
          accesos: this.calcularAccesosPorFecha(fechaDia),
          esHoy: fechaDia === hoyStr
        });
      }

      // Añadir días del mes siguiente hasta completar 6 semanas (42 días)
      const diasNecesarios = 42 - dias.length;
      for (let i = 1; i <= diasNecesarios; i++) {
        const d = new Date(fecha.getFullYear(), fecha.getMonth() + 1, i);
        const fechaSiguiente = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
        dias.push({
          esDelMes: false,
          dia: null,
          accesos: this.calcularAccesosPorFecha(fechaSiguiente)
        });
      }

      return {
        nombre: new Date(fecha).toLocaleString('es', { month: 'long' }),
        año: fecha.getFullYear(),
        dias: dias
      };
    },
    calcularAccesosPorFecha(fechaStr) {
      const accesosFiltrados = this.accesos.filter(acceso => {
        const fechaAcceso = new Date(acceso.horaEntrada).toISOString().split('T')[0];
        if (this.filtroFechaInicio && this.filtroFechaFin) {
          const inicio = new Date(this.filtroFechaInicio);
          const fin = new Date(this.filtroFechaFin);
          return fechaAcceso === fechaStr && new Date(acceso.horaEntrada) >= inicio && new Date(acceso.horaEntrada) <= fin;
        } else if (this.filtroFechaInicio) {
          return fechaAcceso === fechaStr && new Date(acceso.horaEntrada) >= new Date(this.filtroFechaInicio);
        } else if (this.filtroFechaFin) {
          return fechaAcceso === fechaStr && new Date(acceso.horaEntrada) <= new Date(this.filtroFechaFin);
        }
        return fechaAcceso === fechaStr;
      });
      return accesosFiltrados.length;
    },
    desplazarAMesActual() {
      // Solo desplazar si no es solo un mes (para evitar scroll innecesario en dashboard)
      if (this.soloMesActual) return;
      
      // Simplemente desplaza el scroll al final para mostrar el mes actual
      this.$nextTick(() => {
        setTimeout(() => {
          const envoltorio = this.$refs.calendarWrapper;
          if (!envoltorio) return;
          envoltorio.scrollLeft = envoltorio.scrollWidth;
        }, 50);
      });
    }
  }
};
</script>

<style scoped>
.contenedor-calendario {
  overflow-x: auto;
  white-space: nowrap;
  padding: 1rem 0;
}

.envoltorio-calendario {
  display: inline-block;
  overflow-x: auto;
  scroll-behavior: smooth;
}

.mes-calendario {
  display: inline-block;
  vertical-align: top;
  margin: 0 2rem;
  width: 320px;
}

.titulo-mes {
  text-align: center;
  margin-bottom: 1.5rem;
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  text-transform: capitalize;
}

.grilla-calendario {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.3rem;
  grid-auto-rows: minmax(40px, auto);
}

.encabezado-dia {
  font-weight: 600;
  text-align: center;
  padding: 0.5rem;
  background-color: #f8f9fa;
  border-radius: 6px;
  color: #6c757d;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dia-calendario {
  text-align: center;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  aspect-ratio: 1;
  font-size: 0.95rem;
  background: #f8f9fa;
  color: #495057;
  transition: all 0.2s ease;
  cursor: pointer;
}

.tiene-acceso {
  background-color: #42b983;
  color: white;
  border: none;
  box-shadow: 0 2px 4px rgba(66, 185, 131, 0.2);
  font-weight: 500;
}

.fuera-mes {
  background-color: #e9ecef;
  color: #adb5bd;
  cursor: default;
}

.fuera-mes span {
  display: none;
}

.tiene-acceso:hover {
  background-color: #3aa876;
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(66, 185, 131, 0.3);
}

.hoy {
  font-weight: bold;
  position: relative;
  border: 2px solid #42b983;
}

@media (max-width: 768px) {
  .contenedor-calendario {
    position: relative;
    padding-bottom: 2rem;
  }
  


  @keyframes slideHint {
    0% { transform: translateX(0); }
    50% { transform: translateX(10px); }
    100% { transform: translateX(0); }
  }
}
</style>