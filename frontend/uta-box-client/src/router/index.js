import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AdminView from '../views/AdminView.vue'
import BookingView from '../views/BookingView.vue'
import RegisterView from '../views/RegisterView.vue'
import AboutView from '../views/AboutView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/nosotros',
      name: 'about',
      component: AboutView,
    },
    {
      path: '/reservar/:id?',
      name: 'booking',
      component: BookingView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      // "meta" es información extra para la ruta
      meta: {
        requiresAuth: true, // Esta ruta requiere estar logueado
        requiresAdmin: true, // Esta ruta requiere ser admin
      },
    },
  ],
})

// "Guardia de Navegación"
// Se ejecuta antes de cada cambio de página
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const rol = localStorage.getItem('rol')

  // 1. ¿La ruta requiere ser ADMIN? (es lo más restrictivo)
  if (to.meta.requiresAdmin) {
    if (token && rol === 'admin') {
      next() // Es admin, puede pasar
    } else {
      console.warn('Acceso denegado: Se requiere rol de ADMIN.')
      next('/') // No es admin, al Home
    }

    // 2. ¿La ruta requiere SÓLO estar logueado? (ej. /reservar)
  } else if (to.meta.requiresAuth) {
    if (token && (rol === 'admin' || rol === 'cliente')) {
      next() // Es admin O cliente, puede pasar
    } else {
      // No está logueado
      console.warn('Acceso denegado: Se requiere login para /reservar.')
      // Lo mandamos al Home Y le pasamos una "query" para abrir el login
      next({ path: '/', query: { login: 'true' } })
    }

    // 3. Si no es ninguna de las anteriores, es pública
  } else {
    next() // Es una ruta pública (Home), puede pasar
  }
})

export default router
