import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AdminView from '../views/AdminView.vue'
import BookingView from '../views/BookingView.vue'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/reservar/:id?',
      name: 'booking',
      component: BookingView
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      // "meta" es información extra para la ruta
      meta: { 
        requiresAuth: true, // Esta ruta requiere estar logueado
        requiresAdmin: true // Esta ruta requiere ser admin
      } 
    }
  ]
})

// "Guardia de Navegación"
// Se ejecuta antes de CADA cambio de página
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const rol = localStorage.getItem('rol');

  // Si la ruta (to) requiere ser Admin...
  if (to.meta.requiresAdmin) {
    if (token && rol === 'admin') {
      next(); // ...y eres admin, te dejamos pasar.
    } else {
      next('/'); // ...si no, te pateamos al Home.
    }
  } else {
    // Para todas las demás páginas
    next(); // Te dejamos pasar.
  }
})

export default router