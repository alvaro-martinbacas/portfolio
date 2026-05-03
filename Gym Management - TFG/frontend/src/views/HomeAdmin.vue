<template>
  <div class="admin-container">
    <!-- Imagen de fondo -->
    <div class="background-image"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="container d-flex flex-column justify-content-center align-items-center position-relative" style="z-index: 10;">
    <h1 class="mb-0 text-center" style="color:#111;font-weight:bold;margin-top:50px">Panel Administrador - Inicio</h1>
    <h2 class="mb-3 text-center">Panel de Administración</h2>
    <p class="mb-4 text-center text-muted" style="max-width: 500px;">
      Bienvenido, administrador. Desde aquí puedes gestionar el sistema.
    </p>
    <div class="admin-actions-grid">
      <router-link to="/admin/gimnasios" class="admin-action-card">
        <span>Gestionar gimnasios</span>
      </router-link>
      <router-link to="/admin/usuarios" class="admin-action-card">
        <span>Gestionar usuarios</span>
      </router-link>
      <router-link to="/admin/entrenadores" class="admin-action-card">
        <span>Gestionar entrenadores</span>
      </router-link>
      <router-link to="/admin/clases" class="admin-action-card">
        <span>Gestionar clases</span>
      </router-link>
      <router-link to="/admin/rutinas" class="admin-action-card">
        <span>Gestionar rutinas</span>
      </router-link>
      <router-link to="/admin/ejercicios" class="admin-action-card">
        <span>Gestionar ejercicios</span>
      </router-link>
      <router-link to="/admin/solicitudes" class="admin-action-card">
        <span>Gestionar solicitudes</span>
      </router-link>
      <router-link to="/admin/accesos" class="admin-action-card">
        <span>Consultar accesos</span>
      </router-link>
      <router-link to="/admin/medallas" class="admin-action-card">
        <span>Gestionar medallas</span>
      </router-link>
    </div>
    </div>
  </div>
</template>

<script setup>
// Aquí puedes añadir lógica si lo necesitas en el futuro
</script>

<style scoped>
.admin-container {
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
  background-image: url('../assets/fondo4.jpg');
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

.admin-logo {
  width: 100px;
  height: 100px;
  object-fit: contain;
  border-radius: 16px;
  border: 2px solid #e0ddcf;
  background: #fff;
  padding: 10px;
  box-shadow: 0 2px 8px rgba(44, 35, 46, 0.08);
}

.admin-actions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
  width: 100%;
  max-width: 700px;
  margin-top: 32px;
}

.admin-action-card {
  background: #f1f0ea;
  border-radius: 18px;
  padding: 32px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: bold;
  color: #2d232e;
  text-decoration: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: box-shadow 0.2s, transform 0.2s;
  text-align: center;
  min-height: 120px;
}
.admin-action-card:hover {
  box-shadow: 0 4px 20px rgba(44, 35, 46, 0.13);
  transform: translateY(-4px) scale(1.03);
  color: #474448;
}

@media (max-width: 900px) {
  .admin-actions-grid {
    grid-template-columns: 1fr;
    gap: 20px;
    max-width: 400px;
  }
}
</style>