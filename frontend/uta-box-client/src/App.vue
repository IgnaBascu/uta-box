<template>
  <v-app>
    <!-- 1. BARRA DE NAVEGACIÓN -->
    <v-app-bar app color="grey-darken-4" density="compact" elevation="2">
      <v-toolbar-title class="font-weight-bold">
        <v-icon>mdi-microphone-variant</v-icon>
        UTA-BOX
      </v-toolbar-title>

      <v-spacer></v-spacer>

      <!-- Botón de Home (siempre visible) -->
      <v-btn to="/" prepend-icon="mdi-home">Home</v-btn>

      <!-- Botón de Admin (SÓLO si eres admin) -->
      <v-btn v-if="isAdmin" to="/admin" prepend-icon="mdi-security">
        Panel Admin
      </v-btn>

      <!-- Botón de Login (SÓLO si NO estás logueado) -->
      <v-btn v-if="!isLoggedIn" @click="loginModal = true" prepend-icon="mdi-login">
        Login
      </v-btn>

      <!-- Botón de Logout (SÓLO si SÍ estás logueado) -->
      <v-btn v-if="isLoggedIn" @click="handleLogout" prepend-icon="mdi-logout">
        Logout
      </v-btn>
    </v-app-bar>

    <!-- 2. CONTENIDO DE LA PÁGINA (aquí se cargan las vistas del router) -->
    <v-main>
      <router-view />
    </v-main>

    <!-- 3. FOOTER -->
    <v-footer app class="d-flex justify-center" color="grey-darken-4" height="40">
      <div class="text-caption">
        Uta-Box Karaoke &copy; {{ new Date().getFullYear() }}
      </div>
    </v-footer>


    <!-- 4. DIÁLOGO (MODAL) DE LOGIN -->
    <v-dialog v-model="loginModal" max-width="400">
      <v-card class="pa-4" rounded="lg">
        <v-card-title class="text-h5 text-center">
          Iniciar Sesión
        </v-card-title>
        <v-card-text>
          
          <!-- Mensaje de Error -->
          <v-alert
            v-if="loginError"
            type="error"
            variant="tonal"
            class="mb-4"
            density="compact"
          >
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

            <v-btn 
              type="submit" 
              color="primary" 
              block 
              class="mt-2"
              :loading="loading"
            >
              Ingresar
            </v-btn>
          </v-form>

        </v-card-text>
      </v-card>
    </v-dialog>
  </v-app>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios' // Usamos axios normal SÓLO para el login

const router = useRouter()

// --- Estado de Autenticación ---
const isLoggedIn = ref(false)
const isAdmin = ref(false)

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

  if (token && rol) {
    isLoggedIn.value = true
    isAdmin.value = (rol === 'admin')
  } else {
    isLoggedIn.value = false
    isAdmin.value = false
  }
}

// Maneja el LOGIN
const handleLogin = async () => {
  loading.value = true
  loginError.value = null
  try {
    // Llamamos a tu API de Gateway (sin 'api.js' porque aún no tenemos token)
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      email: email.value,
      password: password.value
    });

    // Guardamos los datos
    localStorage.setItem('token', response.data.token)
    localStorage.setItem('rol', response.data.rol)

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
    console.error("Error en login:", error)
    loginError.value = "Email o contraseña incorrectos."
  }
}

// Maneja el LOGOUT
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('rol')
  checkAuthStatus()
  // Lo mandamos al Home
  router.push('/')
}
</script>