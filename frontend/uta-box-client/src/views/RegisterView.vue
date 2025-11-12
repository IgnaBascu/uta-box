<template>
  <v-container class="my-8">
    <v-row justify="center">
      <v-col cols="12" md="6">
        
        <h1 class="text-h4 mb-2 text-center" :style="{ color: 'var(--v-theme-primary)' }">
          Crear Cuenta
        </h1>
        <h2 class="text-h6 font-weight-regular text-center mb-4" :style="{ color: 'var(--color-text)' }">
          ¡Es rápido y fácil!
        </h2>
        
        <v-card elevation="4" rounded="lg">
          <v-card-text class="pa-6">
            
            <v-alert
              v-if="registerSuccess"
              type="success"
              variant="tonal"
              class="mb-4"
              density="compact"
            >
              ¡Registro exitoso! Serás redirigido al inicio para que inicies sesión.
            </v-alert>

            <v-alert
              v-if="registerError"
              type="error"
              variant="tonal"
              class="mb-4"
              density="compact"
            >
              {{ registerError }}
            </v-alert>

            <v-form @submit.prevent="handleRegister" :disabled="registerSuccess">
              <v-text-field
                v-model="nombre"
                label="Nombre Completo"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                density="compact"
                class="mb-2"
              ></v-text-field>

              <v-text-field
                v-model="email"
                label="Email"
                type="email"
                prepend-inner-icon="mdi-email"
                variant="outlined"
                density="compact"
                class="mb-2"
              ></v-text-field>

              <v-text-field
                v-model="password"
                label="Contraseña (mín. 6 caracteres)"
                type="password"
                prepend-inner-icon="mdi-lock"
                variant="outlined"
                density="compact"
              ></v-text-field>

              <v-btn 
                type="submit" 
                color="primary" 
                block 
                class="mt-4"
                :loading="loading"
                size="large"
              >
                Registrarme
              </v-btn>
            </v-form>

            <div class="text-center text-caption mt-4">
              ¿Ya tienes una cuenta? 
              <router-link to="/" @click="goToLogin">Inicia sesión aquí</router-link>
            </div>
            
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
// Usamos axios normal porque esta petición NO necesita token de autorización
import axios from 'axios' 

const router = useRouter()

// Estado del formulario
const nombre = ref('')
const email = ref('')
const password = ref('')

// Estado de la UI
const loading = ref(false)
const registerError = ref(null)
const registerSuccess = ref(false)

const handleRegister = async () => {
  loading.value = true
  registerError.value = null
  registerSuccess.value = false

  try {
    // 1. Llamamos a la API del Gateway
    // Esta es la misma URL que usa tu modal de login
    await axios.post('http://localhost:8080/api/auth/register', {
      nombre: nombre.value,
      email: email.value,
      password: password.value
    })

    // 2. Éxito
    registerSuccess.value = true
    
    // 3. Redirigimos al Home para que inicie sesión
    setTimeout(() => {
      goToLogin()
    }, 3000) // Espera 3 segundos

  } catch (error) {
    // 4. Error
    console.error("Error en registro:", error)
    if (error.response && error.response.data && error.response.data.message) {
      // Esto capturará los errores 409 ("El email ya está registrado")
      // y los errores 400 de validación (ej. "La contraseña debe...")
      registerError.value = error.response.data.message
    } else {
      registerError.value = "Error desconocido. Revisa la consola."
    }
  } finally {
    loading.value = false
  }
}

// Función para enviar al usuario al Home y abrir el modal de login
const goToLogin = () => {
  router.push({ path: '/', query: { login: 'true' } })
}
</script>

<style scoped>
/* Estilo para que el link se vea del color correcto */
a {
  color: var(--v-theme-primary);
  text-decoration: none;
  font-weight: bold;
}
</style>