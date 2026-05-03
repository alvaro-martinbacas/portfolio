<template>
  <div class="entrenador-container">
    <!-- Imagen de fondo -->
    <div class="background-image"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="container d-flex flex-column justify-content-center align-items-center position-relative" style="z-index: 10;">
    <h1 class="mb-0 text-center" style="color:#111;font-weight:bold;margin-top:50px">Panel Entrenador - Inicio</h1>
    <h2 class="mb-3 text-center">Bienvenido, entrenador</h2>
    <p class="mb-4 text-center text-muted" style="max-width: 500px;">
      Accede rápidamente a tus herramientas principales como entrenador.
    </p>
    <div class="entrenador-actions-grid">
      <router-link to="/entrenador/rutinas-entrenador" class="entrenador-action-card">
        <div class="icono-accion"><i class="fas fa-dumbbell"></i></div>
        <span>Tus rutinas</span>
        <p class="descripcion">Crea, edita y consulta tus rutinas personalizadas para tus clientes.</p>
      </router-link>

      <router-link to="/entrenador/horario" class="entrenador-action-card">
        <div class="icono-accion"><i class="fas fa-calendar-alt"></i></div>
        <span>Tu horario</span>
        <p class="descripcion">Consulta tu horario semanal de clases colectivas.</p>
      </router-link>

    </div>
    <div class="entrenador-ilustracion">
      <img src="/src/assets/logo.png" alt="Entrenador" class="entrenador-logo" />
    </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.entrenador-container {
  position: relative;
  min-height: 100vh;
  background-color: #F1F0EA;
  overflow: hidden;
}

/* Imagen de fondo */
.background-image {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('../assets/fondo5.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.15;
  z-index: 0;
  filter: grayscale(20%) blur(1px);
}

/* Gradiente superpuesto */
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

.entrenador-actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32px;
  width: 100%;
  max-width: 600px;
  margin-top: 32px;
}

.entrenador-action-card {
  background: #f1f0ea;
  border-radius: 18px;
  padding: 32px 16px 24px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  font-size: 1.2rem;
  font-weight: bold;
  color: #2d232e;
  text-decoration: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: box-shadow 0.2s, transform 0.2s;
  text-align: center;
  min-height: 180px;
  position: relative;
}
.entrenador-action-card:hover {
  box-shadow: 0 4px 20px rgba(44, 35, 46, 0.13);
  transform: translateY(-4px) scale(1.03);
  color: #474448;
}
.icono-accion {
  font-size: 2.5rem;
  margin-bottom: 12px;
  color: #42b983;
}
.descripcion {
  font-size: 1rem;
  font-weight: 400;
  color: #6c757d;
  margin-top: 10px;
}
.entrenador-ilustracion {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
.entrenador-logo {
  width: 120px;
  height: 120px;
  object-fit: contain;
  border-radius: 16px;
  border: 2px solid #e0ddcf;
  background: #fff;
  padding: 10px;
  box-shadow: 0 2px 8px rgba(44, 35, 46, 0.08);
}
@media (max-width: 900px) {
  .entrenador-actions-grid {
    grid-template-columns: 1fr;
    gap: 20px;
    max-width: 400px;
  }
}
</style>