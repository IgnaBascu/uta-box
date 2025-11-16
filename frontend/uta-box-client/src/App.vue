<template>
  <v-app>
    <v-app-bar app color="primary" density="compact" elevation="2">
      <v-spacer></v-spacer>
      <v-toolbar-title class="font-weight-bold">
        <div class="d-flex align-center">
          <v-img
            :src="appIcon"
            alt="Uta-Box Icon"
            contain
            max-height="36"
            max-width="36"
            class="mr-2"
          />
          <span>UTA-BOX</span>
        </div>
      </v-toolbar-title>

      <v-btn to="/" prepend-icon="mdi-home" class="app-bar-btn">Inicio</v-btn>
      <v-btn to="/nosotros" prepend-icon="mdi-information-outline" class="app-bar-btn"
        >Nosotros</v-btn
      >
      <v-btn v-if="isLoggedIn" to="/reservar" prepend-icon="mdi-calendar-check" class="app-bar-btn">
        Reservar
      </v-btn>
      <v-btn v-if="isAdmin" to="/admin" prepend-icon="mdi-security" class="app-bar-btn">
        Panel Admin
      </v-btn>

      <div class="mr-3">
        <v-menu v-if="isLoggedIn" offset-y>
          <template v-slot:activator="{ props }">
            <v-chip
              color="surface"
              variant="flat"
              class="font-weight-bold"
              v-bind="props"
              style="cursor: pointer"
            >
              <v-icon start icon="mdi-account-circle"></v-icon>
              {{ userName }}
              <v-icon end icon="mdi-chevron-down"></v-icon>
            </v-chip>
          </template>
          <v-list density="compact">
            <v-list-item
              @click="logoutDialog = true"
              prepend-icon="mdi-logout"
              class="menu-item-hover"
            >
              <v-list-item-title>Cerrar Sesión</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu v-if="!isLoggedIn" offset-y>
          <template v-slot:activator="{ props }">
            <v-chip
              v-bind="props"
              color="accent"
              variant="flat"
              class="font-weight-bold"
              style="cursor: pointer"
            >
              Hola, ingresa acá!
              <v-icon end icon="mdi-chevron-down"></v-icon>
            </v-chip>
          </template>
          <v-list density="compact">
            <v-list-item @click="loginModal = true" class="menu-item-hover">
              <v-list-item-title>Inicia sesión</v-list-item-title>
            </v-list-item>
            <v-list-item to="/register" class="menu-item-hover">
              <v-list-item-title>Regístrate</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
      <v-spacer></v-spacer>
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>

    <!-- Footer -->
    <v-footer app class="pa-4" color="primary" height="auto">
      <v-container>
        <v-row justify="center">
          <v-col cols="12" class="text-center">
            <div class="text-caption footer-text">
              <span>
                <v-icon size="small" left>mdi-map-marker</v-icon>
                Av. Ficticia 123, Santiago
              </span>
              <span class="mx-3 d-none d-sm-inline">|</span>
              <span class="d-block d-sm-inline mt-1 mt-sm-0">
                <v-icon size="small" left>mdi-clock</v-icon>
                Lunes a Domingo, 11:00 - 20:00
              </span>
              <span class="mx-3 d-none d-sm-inline">|</span>
              <span class="d-block d-sm-inline mt-1 mt-sm-0">
                Uta-Box Karaoke &copy; {{ new Date().getFullYear() }}
              </span>
            </div>
          </v-col>
        </v-row>
      </v-container>
    </v-footer>
    <!-- Fin Footer -->

    <v-dialog v-model="loginModal" max-width="400">
      <v-card class="pa-4" rounded="lg">
        <v-card-title class="text-h5 text-center"> Iniciar Sesión </v-card-title>
        <v-card-text>
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
          <v-alert v-if="loginError" type="error" variant="tonal" class="mb-4" density="compact">
            {{ loginError }}
          </v-alert>

          <v-form @submit.prevent="handleLogin">
            <v-text-field
              v-model="email"
              label="Email"
              type="email"
              prepend-inner-icon="mdi-email"
              variant="outlined"
              density="compact"
              @blur="email = email.toLowerCase()"
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

    <v-dialog v-model="logoutDialog" max-width="400" persistent>
      <v-card class="pa-4" rounded="lg">
        <v-card-title class="text-h5 text-center"> Cerrar Sesión </v-card-title>
        <v-card-text class="text-center"> ¿Estás seguro de que quieres salir? </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="tonal" @click="logoutDialog = false"> Cancelar </v-btn>
          <v-btn color="primary" variant="flat" @click="handleLogout"> Sí, Salir </v-btn>
          <v-spacer></v-spacer>
        </v-card-actions>
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
const logoutDialog = ref(false)

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
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      email: email.value.toLowerCase(),
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
  logoutDialog.value = false
  // Lo mandamos al Home
  router.push('/')
}
</script>

<style scoped>
/* Ajuste para los botones de la barra (Deslogueado) */
.app-bar-btn {
  /* Usa 'surface' (blanco) para que contraste con la barra rosa */
  color: var(--v-theme-surface) !important;
  /* Esto hace que el borde del botón 'outlined' también sea blanco */
  border-color: var(--v-theme-surface);
  text-shadow: 0px 1px 3px rgba(0, 0, 0, 0.4);
}

/* (Puedes añadir más estilos aquí si quieres) */
a {
  color: var(--v-theme-primary);
  text-decoration: none;
  font-weight: bold;
}

/* Sombra para el texto del Logo (UTA-BOX) y el botón "Inicio" */
.v-toolbar-title span,
.v-btn {
  text-shadow: 0px 1px 3px rgba(0, 0, 0, 0.4);
}

.menu-item-hover {
  cursor: pointer !important;
  transition: all 0.2s ease;
}

.menu-item-hover:hover {
  background-color: rgba(0, 0, 0, 0.08) !important;
}

.banner-extension {
  width: 100vw !important; /* fuerza el 100% del ancho de la ventana */
  margin-left: calc(50% - 50vw); /* elimina padding del app-bar */
  margin-right: calc(50% - 50vw);
}

.footer-text {
  /* Sombra negra suave para que el texto blanco resalte */
  text-shadow: 0px 1px 3px rgba(0, 0, 0, 0.5);
  color: white !important; /* Fuerza el color blanco */
}
</style>
