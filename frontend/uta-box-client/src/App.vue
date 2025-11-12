<template>
  <v-app>
    <!-- 1. BARRA DE NAVEGACIÓN -->
    <v-app-bar app color="primary" density="compact" elevation="2">
      <!-- ¡AQUÍ ESTÁ EL ARREGLO! -->
      <v-toolbar-title class="font-weight-bold">
        <!-- 1. Quité 'd-flex align-center' de aquí... -->

        <!-- 2. ...y lo puse en un <div> interno -->
        <div class="d-flex align-center">
          <v-img
            :src="appIcon"
            alt="Uta-Box Icon"
            contain
            max-height="36"
            max-width="36"
            class="mr-2"
          />
          <!-- 3. Envolví el texto en un <span> para más seguridad -->
          <span>UTA-BOX</span>
        </div>
      </v-toolbar-title>
      <!-- FIN DEL ARREGLO -->

      <v-spacer></v-spacer>

      <!-- Botón de Home (siempre visible) -->
      <v-btn to="/" prepend-icon="mdi-home">Home</v-btn>

      <!-- Botón de Reservar (siempre visible) -->
      <v-btn to="/reservar" prepend-icon="mdi-calendar-check">Reservar</v-btn>

      <!-- Botón de Admin (SÓLO si eres admin) -->
      <v-btn v-if="isAdmin" to="/admin" prepend-icon="mdi-security"> Panel Admin </v-btn>

      <v-btn v-if="isLoggedIn" @click="handleLogout" prepend-icon="mdi-logout"> Logout </v-btn>

      <v-chip v-if="isLoggedIn" color="surface" variant="flat" class="font-weight-bold mr-3">
        <v-icon start icon="mdi-account-circle"></v-icon>
        {{ userName }} ({{ userRol }})
      </v-chip>

      <v-btn v-if="!isLoggedIn" @click="loginModal = true" prepend-icon="mdi-login"> Login </v-btn>

      <v-btn
        v-if="!isLoggedIn"
        to="/register"
        prepend-icon="mdi-account-plus-outline"
        variant="text"
      >
        Registrarse
      </v-btn>
  
    </v-app-bar>

    <!-- 2. CONTENIDO DE LA PÁGINA (aquí se cargan las vistas del router) -->
    <v-main>
      <router-view />
    </v-main>

    <!-- 3. FOOTER -->
    <v-footer app class="d-flex justify-center" color="primary" height="40">
      <div class="text-caption">Uta-Box Karaoke &copy; {{ new Date().getFullYear() }}</div>
    </v-footer>

    <!-- 4. DIÁLOGO (MODAL) DE LOGIN -->
    <v-dialog v-model="loginModal" max-width="400">
      <v-card class="pa-4" rounded="lg">
        <v-card-title class="text-h5 text-center"> Iniciar Sesión </v-card-title>
        <v-card-text>
          <!-- Modal debes iniciar sesión para reservar -->
          <v-alert
            v-if="route.query.login === 'true'"
            type="info"
            variant="tonal"
            class="mb-4"
            density="compact"
            icon="mdi-alert-circle-outline"
          >
            Debes iniciar sesión para poder reservar.
          </v-alert>
          <!-- Mensaje de Error -->
          <v-alert v-if="loginError" type="error" variant="tonal" class="mb-4" density="compact">
            {{ loginError }}
          </v-alert>

          <!-- Formulario -->
          <v-form @submit.prevent="handleLogin">
            <v-text-field
              v-model="email"
              label="Email"
              type="email"
              prepend-inner-icon="mdi-email"
              variant="outlined"
              density="compact"
            ></v-text-field>

            <v-text-field
              v-model="password"
              label="Contraseña"
              type="password"
              prepend-inner-icon="mdi-lock"
              variant="outlined"
              density="compact"
            ></v-text-field>

            <v-btn type="submit" color="primary" block class="mt-2" :loading="loading">
              Ingresar
            </v-btn>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-app>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios' // Usamos axios normal SÓLO para el login

import appIcon from '@/assets/uta_box_36.png'

const router = useRouter()
const route = useRoute()

// --- Estado de Autenticación ---
const isLoggedIn = ref(false)
const isAdmin = ref(false)
const userName = ref('')
const userRol = ref('')

// --- Estado del Modal ---
const loginModal = ref(false)
const loading = ref(false)
const email = ref('')
const password = ref('')
const loginError = ref(null)

// --- Lógica ---

// Al cargar la app, revisa si ya estamos logueados
onMounted(() => {
  checkAuthStatus()
})

// Revisa el localStorage
const checkAuthStatus = () => {
  const token = localStorage.getItem('token')
  const rol = localStorage.getItem('rol')
  const nombre = localStorage.getItem('nombre')

  if (token && rol && nombre) {
    isLoggedIn.value = true
    isAdmin.value = rol === 'admin'
    userName.value = nombre
    userRol.value = rol
  } else {
    isLoggedIn.value = false
    isAdmin.value = false
    userName.value = ''
    userRol.value = ''
  }
}

watch(
  () => route.query.login,
  (newVal) => {
    if (newVal === 'true') {
      loginModal.value = true

      // Limpia la query de la URL (para que si cierras el modal, no vuelva a abrirse)
      // Usamos 'replace' para no añadir una nueva entrada al historial del navegador
      router.replace({ query: { ...route.query, login: undefined } })
    }
  },
  { immediate: true },
) // 'immediate: true' hace que se ejecute al cargar la página por primera vez

// Maneja el LOGIN
const handleLogin = async () => {
  loading.value = true
  loginError.value = null
  try {
    // Llamamos a API de Gateway (sin 'api.js' porque aún no tenemos token)
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      email: email.value,
      password: password.value,
    })

    // Guardamos los datos
    localStorage.setItem('token', response.data.token)
    localStorage.setItem('rol', response.data.rol)
    localStorage.setItem('nombre', response.data.nombre)

    // Actualizamos el estado de la app
    checkAuthStatus()

    // Cerramos el modal
    loginModal.value = false
    loading.value = false

    // Si es admin, lo mandamos al panel
    if (response.data.rol === 'admin') {
      router.push('/admin')
    }
  } catch (error) {
    loading.value = false
    console.error('Error en login:', error)
    loginError.value = 'Email o contraseña incorrectos.'
  }
}

// Maneja el LOGOUT
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('rol')
  localStorage.removeItem('nombre')
  checkAuthStatus()
  // Lo mandamos al Home
  router.push('/')
}
</script>
