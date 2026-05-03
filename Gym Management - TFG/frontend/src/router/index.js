import { createRouter, createWebHistory } from "vue-router"
import { useUserStore } from '../stores/user'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: "/",
            name: "¡Bienvenido!",
            component: () => import("../views/Landing.vue"),
            meta: { hideHeader: true, public: true }
        },
        {
            path: "/home",
            name: "Página de inicio",
            component: () => import("../views/Home.vue"),
            meta: { title: "Página de Inicio", requiresAuth: true }
        },
        {
            path: "/login",
            name: "Login",
            component: () => import("../views/Login.vue"),
            meta: { hideHeader: true, public: true }
        },
        {
            path: "/home-usuario",
            name: "Homepage Usuario",
            component: () => import("../views/HomeUser.vue"),
        },
        {
            path: "/home-entrenador",
            name: "Homepage Entrenador",
            component: () => import("../views/HomeEntrenador.vue"),
        },
        {
            path: "/home-admin",
            name: "Homepage Administrador",
            component: () => import("../views/HomeAdmin.vue"),
            meta: { hideHeader: true }
        },
        {
            path: "/user/rutinas",
            name: "Mis Rutinas",
            component: () => import("../views/UserRutinas.vue"),
            meta: { title: "Mis Rutinas" }
        },
        {
            path: "/user/clases-colectivas",
            name: "Clases Colectivas",
            component: () => import("../views/UserClases.vue"),
            meta: { title: "Clases Colectivas" }
        },
        {
            path: "/user/medallas",
            name: "Mis Medallas",
            component: () => import("../views/UserMedallas.vue"),
            meta: { title: "Mis Medallas" }
        },
        {
            path: "/user/accesos",
            name: "Historial de Accesos",
            component: () => import("../views/UserAccesos.vue"),
            meta: { title: "Mi Historial de Accesos" }
        },
        {
            path: "/registro",
            name: "Registro Usuario",
            component: () => import("../views/Registro.vue"),
            meta: { hideHeader: true, public: true }
        },
        {
            path: "/admin/gimnasios",
            name: "Administración de gimnasios",
            component: () => import("../views/AdminGimnasio.vue"),
            meta: { title: "Gestión de Gimnasios - Admin" }
        },
        {
            path: "/admin/usuarios",
            name: "Administración de usuarios",
            component: () => import("../views/AdminUsuario.vue"),
            meta: { title: "Gestión de Usuarios - Admin" }
        },
        {
            path: "/admin/entrenadores",
            name: "Administración de entrenadores",
            component: () => import("../views/AdminEntrenador.vue"),
            meta: { title: "Gestión de Entrenadores - Admin" }
        },
        {
            path: "/admin/clases",
            name: "Administración de clases",
            component: () => import("../views/AdminClase.vue"),
            meta: { title: "Gestión de Clases - Admin" }
        },
        {
            path: "/admin/rutinas",
            name: "Administración de rutinas",
            component: () => import("../views/AdminRutina.vue"),
            meta: { title: "Gestión de Rutinas - Admin" }
        },
        {
            path: "/admin/ejercicios",
            name: "Administración de ejercicios",
            component: () => import("../views/AdminEjercicio.vue"),
            meta: { title: "Gestión de Ejercicios - Admin" }
        },
        {
            path: "/admin/solicitudes",
            name: "Administración de solicitudes",
            component: () => import("../views/AdminSolicitud.vue"),
            meta: { title: "Gestión de Solicitudes - Admin" }
        },
        {
          path: "/admin/accesos",
          name: "Administración de accesos",
          component: () => import("../views/AdminAcceso.vue"),
          meta: { title: "Gestión de Accesos - Admin" }
        },
        {
          path: "/admin/medallas",
          name: "Administración de medallas",
          component: () => import("../views/AdminMedalla.vue"),
          meta: { title: "Gestión de Medallas - Admin" }
        },
        {
          path: '/user/entrada-salida',
          name: 'Usuario Entrada/Salida',
          component: () => import("../views/UserEntradaSalida.vue"),
          meta: { title: "Gestión de Entrada/Salida - Usuario", requiresAuth: true }
        },
        {
          path: '/user/aforo',
          name: 'UserAforo',
          component: () => import('../views/UserAforo.vue'),
          meta: { title: "Gestión de Aforo - Usuario", requiresAuth: true }
        },
        {
          path: '/entrenador/rutinas-entrenador',
          name: 'Rutinas Entrenador',
          component: () => import("../views/EntrenadorRutinas.vue"),
          meta: { title: "Gestión de Rutinas - Entrenador", requiresAuth: true }
        },
        {
          path: '/entrenador/horario',
          name: 'Horario Entrenador',
          component: () => import("../views/EntrenadorHorario.vue"),
          meta: { title: "Horario Semanal - Entrenador", requiresAuth: true }
        }
    ]
})

router.beforeEach(async (to, from, next) => {
  // Validar token al inicio/recarga
  const token = localStorage.getItem('token');
  const userStore = useUserStore();
  
  if (token) {
    try {
      const response = await fetch('http://localhost:8080/gestiongimnasios/auth/login', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        credentials: 'include'
      });
      
      if (!response.ok) {
        // Si el token no es válido, limpia todo
        localStorage.removeItem('rol');
        localStorage.removeItem('email');
        localStorage.removeItem('token');
        localStorage.removeItem('gimnasioId');
        userStore.setAuthenticated(false);
        return next('/');
      }
    } catch (e) {
      // Si hay error de conexión, asume que no está autenticado
      localStorage.removeItem('rol');
      localStorage.removeItem('email');
      localStorage.removeItem('token');
      localStorage.removeItem('gimnasioId');
      userStore.setAuthenticated(false);
      return next('/');
    }
  }

  // Cierra sesión si va a la landing page
  if (to.path === '/' && localStorage.getItem('rol')) {
    try {
      await fetch('http://localhost:8080/gestiongimnasios/auth/logout', {
        method: 'POST',
        credentials: 'include'
      });
    } catch (e) {
      // Ignora errores de logout
    }
    localStorage.removeItem('rol');
    localStorage.removeItem('email');
    localStorage.removeItem('token');
    localStorage.removeItem('gimnasioId');
    userStore.setIsAuthenticated(false);
  }

  // Cambia el título de la página automáticamente
  if (to.meta && to.meta.title) {
    document.title = to.meta.title;
  } else if (to.name) {
    document.title = to.name + ' | Gestión Gimnasios';
  } else {
    document.title = 'Gestión Gimnasios';
  }

  const isAuthenticated = userStore.getIsAuthenticated
  const isPublicRoute = to.meta.public || false
  const rol = localStorage.getItem('rol');

  // Si no está autenticado y la ruta no es pública, redirige a la landing
  if (!isAuthenticated && !isPublicRoute) {
    return next({ path: '/' });
  }

  // Verificar permisos según el rol
  if (isAuthenticated) {
    if (to.path.startsWith('/admin') && rol !== 'ADMIN') {
      return next('/');
    }
    if (to.path.startsWith('/user') && rol !== 'USUARIO') {
      return next('/');
    }
    if (to.path.startsWith('/entrenador') && rol !== 'ENTRENADOR') {
      return next('/');
    }
  }

  // Si todo está bien, permite la navegación
  next();
});

export default router;