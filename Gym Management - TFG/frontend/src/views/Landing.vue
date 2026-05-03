<template>
  <div class="background-container">
    <!-- Imagen de fondo de mancuernas -->
    <div class="background-image bg-fondo2"></div>
    
    <!-- Gradiente superpuesto -->
    <div class="gradient-overlay"></div>
    
    <div class="container d-flex flex-column justify-content-center align-items-center min-vh-100 position-relative">
      <div class="hero-section text-center mb-5">
        <img
          src="..\assets\logo.png"
          alt="Logo gimnasio"
          class="app-logo mb-4"
        />
      </div>

      <!-- Cards principales -->
      <div class="landing-options mb-4">
        <div class="landing-option-card login-card me-4">
          <div class="card-icon mb-3">🔑</div>
          <div class="option-title mb-3 text-center px-4">¿Ya tienes cuenta?</div>
          <p class="card-description mb-3">Accede a tu panel personalizado</p>
          <router-link to="/login" class="w-100 d-block">
            <button class="button btn-lg w-100 btn-primary-custom">Inicia sesión</button>
          </router-link>
        </div>
        
        <div class="landing-option-card register-card">
          <div class="card-icon mb-3">🚀</div>
          <div class="option-title mb-3 text-center">¿Quieres darte de alta?</div>
          <p class="card-description mb-3">Únete a nuestra comunidad fitness</p>
          <router-link to="/registro" class="w-100 d-block">
            <button class="button btn-lg w-100 btn-primary-custom">Regístrate</button>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

/* Hero Section */
.hero-section {
  z-index: 3;
  position: relative;
}

.hero-title {
  font-size: 4rem;
  font-weight: 900;
  margin-bottom: 1rem;
  text-shadow: 2px 2px 4px rgba(45, 35, 46, 0.1);
  color: #2D232E;
}

.gradient-text {
  background: linear-gradient(45deg, #474448, #534B52, #2D232E);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
  0%, 100% { filter: hue-rotate(0deg); }
  50% { filter: hue-rotate(15deg); }
}

.hero-subtitle {
  font-size: 1.3rem;
  color: #534B52;
  font-weight: 300;
  text-shadow: 1px 1px 2px rgba(45, 35, 46, 0.1);
  max-width: 600px;
  margin: 0 auto;
}

/* Los estilos del logo ahora están en global.css */

/* Cards principales */
.landing-options {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: stretch;
  width: 90%;
  max-width: 700px;
  z-index: 3;
  position: relative;
}

.landing-option-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 32px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 16px 48px rgba(45, 35, 46, 0.15);
  min-width: 300px;
  max-width: 320px;
  transition: all 0.3s ease;
  border: 2px solid rgba(45, 35, 46, 0.1);
  margin-left: 80px;
  margin-right: 80px;
}

.landing-option-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(45, 35, 46, 0.15);
}

.card-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #474448;
}

.option-title {
  font-size: 1.35rem;
  font-weight: bold;
  color: #2D232E;
  margin-bottom: 0.8rem;
  line-height: 1.2;
}

.card-description {
  font-size: 0.9rem;
  color: #534B52;
  text-align: center;
  margin-bottom: 1.5rem;
}

/* Botones con estética de la aplicación */
.btn-primary-custom {
  background-color: #474448;
  border: 2px solid #474448;
  color: #F1F0EA;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 2px 8px rgba(45, 35, 46, 0.2);
}

.btn-primary-custom:hover {
  background-color: #534B52;
  border-color: #534B52;
  color: #F1F0EA;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(45, 35, 46, 0.3);
}

.btn-lg {
  font-size: 1.1rem;
  padding: 0.85em 2em;
}

.features-section {
  z-index: 3;
  position: relative;
  max-width: 800px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(45, 35, 46, 0.08);
  border: 1px solid rgba(45, 35, 46, 0.05);
}

.feature-card {
  background: transparent;
  padding: 2rem;
  text-align: center;
  height: 100%;
}



.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  color: #474448;
}

.feature-card h5 {
  color: #2D232E;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.feature-card p {
  color: #534B52;
  font-size: 0.9rem;
  margin: 0;
}

@media (max-width: 768px) {
  .background-image {
    opacity: 0.1;
    filter: grayscale(30%) blur(2px);
    background-position: center center;
  }
  
  .gradient-overlay {
    background: linear-gradient(45deg, 
      rgba(45, 35, 46, 0.4) 0%, 
      rgba(83, 75, 82, 0.3) 50%,
      rgba(241, 240, 234, 0.85) 100%);
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-subtitle {
    font-size: 1.1rem;
  }
  
  .hero-section {
    margin-top: 3rem;
  }
  
  .landing-options {
    flex-direction: column;
    max-width: 350px;
    align-items: center;
    width: 100%;
  }
  
  .landing-option-card {
    padding: 24px 16px;
    margin: 10px 0;
    min-width: auto;
    width: 100%;
    max-width: 300px;
  }
  
  .landing-option-card.me-4 {
    margin-right: 0 !important;
  }
  
  /* Estilos responsive del logo en global.css */
  
  .features-section {
    margin-top: 2rem;
    padding: 2rem 1.5rem;
  }
}
</style>