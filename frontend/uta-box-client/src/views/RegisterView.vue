<template>
  <v-container class="my-8">
    <v-row justify="center">
      <v-col cols="12" md="6">
        <h1 class="text-h3 mb-2 text-center title-kawaii titulo">
          ¡Vamos a cantar!
        </h1>
        <h2 class="text-subtitle-1 font-weight-regular text-center mb-4"
          :style="{ color: 'var(--color-text)' }"
        >
          🎤 Crea tu cuenta ✨
        </h2>

        <v-card elevation="4" rounded="xl">
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

            <!-- Formulario de registro -->
            <v-form ref="form" @submit.prevent="handleRegister" :disabled="registerSuccess" lazy-validation>
              <!-- Input nombre -->
              <v-text-field
                v-model="nombre"
                label="Nombre completo"
                :rules="nameRules"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                density="compact"
                class="mb-2"
                rounded="lg"
              ></v-text-field>
              <!-- Input email -->
              <v-text-field
                v-model="email"
                :rules="emailRules"
                label="Correo electrónico"
                type="email"
                prepend-inner-icon="mdi-email"
                variant="outlined"
                density="compact"
                class="mb-2"
                rounded="lg"
                @blur="email = email.toLowerCase()"
              ></v-text-field>
              <!-- Input password -->
              <v-text-field
                v-model="password"
                :rules="passwordRules"
                label="Contraseña"                
                :type="isPasswordVisible ? 'text' : 'password'"           
                :append-inner-icon="isPasswordVisible ? 'mdi-eye-off' : 'mdi-eye'" @click:append-inner="isPasswordVisible = !isPasswordVisible"
                prepend-inner-icon="mdi-lock"
                variant="outlined"
                density="compact"
                hint="La contraseña debe cumplir los siguientes requisitos:"
                persistent-hint
                rounded="lg"

              ></v-text-field>
              <!-- Validaciones password -->
              <v-list density="compact" class="pa-0 mt-2" bg-color="transparent">
                <v-list-item
                  :class="{ 'text-success': passwordRulesStatus.length }"
                  class="pa-0"
                  density="tight"
                >
                  <template v-slot:prepend>
                    <v-icon
                      :icon="passwordRulesStatus.length ? 'mdi-check-circle' : 'mdi-circle-small'"
                      :color="passwordRulesStatus.length ? 'success' : 'grey'"
                      size="small"
                      class="mr-2"
                    ></v-icon>
                  </template>
                  <v-list-item-title class="text-caption">
                    Al menos 8 caracteres
                  </v-list-item-title>
                </v-list-item>

                <v-list-item
                  :class="{ 'text-success': passwordRulesStatus.upper }"
                  class="pa-0"
                  density="tight"
                >
                  <template v-slot:prepend>
                    <v-icon
                      :icon="passwordRulesStatus.upper ? 'mdi-check-circle' : 'mdi-circle-small'"
                      :color="passwordRulesStatus.upper ? 'success' : 'grey'"
                      size="small"
                      class="mr-2"
                    ></v-icon>
                  </template>
                  <v-list-item-title class="text-caption">
                    Al menos una mayúscula (A-Z)
                  </v-list-item-title>
                </v-list-item>

                <v-list-item
                  :class="{ 'text-success': passwordRulesStatus.lower }"
                  class="pa-0"
                  density="tight"
                >
                  <template v-slot:prepend>
                    <v-icon
                      :icon="passwordRulesStatus.lower ? 'mdi-check-circle' : 'mdi-circle-small'"
                      :color="passwordRulesStatus.lower ? 'success' : 'grey'"
                      size="small"
                      class="mr-2"
                    ></v-icon>
                  </template>
                  <v-list-item-title class="text-caption">
                    Al menos una minúscula (a-z)
                  </v-list-item-title>
                </v-list-item>

                <v-list-item
                  :class="{ 'text-success': passwordRulesStatus.number }"
                  class="pa-0"
                  density="tight"
                >
                  <template v-slot:prepend>
                    <v-icon
                      :icon="passwordRulesStatus.number ? 'mdi-check-circle' : 'mdi-circle-small'"
                      :color="passwordRulesStatus.number ? 'success' : 'grey'"
                      size="small"
                      class="mr-2"
                    ></v-icon>
                  </template>
                  <v-list-item-title class="text-caption">
                    Al menos un número (0-9)
                  </v-list-item-title>
                </v-list-item>
              </v-list>

              <v-btn
                type="submit"                
                block
                class="mt-4 gradient-btn"
                :loading="loading"
                size="large"
              >
                Crear cuenta
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
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
// Usamos axios normal porque esta petición NO necesita token de autorización
import axios from 'axios'
// Importar reglas de validación
import * as rules from '@/utils/validationRules'

const router = useRouter()

// Estado del formulario
const form = ref(null)
const nombre = ref('')
const email = ref('')
const password = ref('')
const isPasswordVisible = ref(false)

// Reglas de validación
const nameRules = [rules.required, rules.minLength(3)]
const emailRules = [rules.required, rules.email]
const passwordRules = [rules.required, rules.passwordStrength]

const passwordRulesStatus = ref({
  length: false,
  upper: false,
  lower: false,
  number: false,
})

watch(password, (newValue) => {
  // En cada tecleo, actualizamos el estado del checklist
  passwordRulesStatus.value.length = newValue && newValue.length >= 8
  passwordRulesStatus.value.upper = /[A-Z]/.test(newValue)
  passwordRulesStatus.value.lower = /[a-z]/.test(newValue)
  passwordRulesStatus.value.number = /[0-9]/.test(newValue)
})

// Estado de la UI
const loading = ref(false)
const registerError = ref(null)
const registerSuccess = ref(false)

const handleRegister = async () => {
  const { valid } = await form.value.validate()
  if (!valid) {
    return // Si no es válido, detiene la función
  }

  loading.value = true
  registerError.value = null
  registerSuccess.value = false

  try {
    // 1. Llamamos a la API del Gateway
    // Esta es la misma URL que usa tu modal de login
    await axios.post('http://localhost:8080/api/auth/register', {
      nombre: nombre.value,
      email: email.value.toLowerCase(),
      password: password.value,
    })

    // 2. Éxito
    registerSuccess.value = true

    // 3. Redirigimos al Home para que inicie sesión
    setTimeout(() => {
      goToLogin()
    }, 3000) // Espera 3 segundos
  } catch (error) {
    // 4. Error
    console.error('Error en registro:', error)
    if (error.response && error.response.data && error.response.data.message) {
      // Esto capturará los errores 409 ("El email ya está registrado")
      // y los errores 400 de validación (ej. "La contraseña debe...")
      registerError.value = error.response.data.message
    } else {
      // AÑADIDO: Mensaje de error mejorado (del feedback)
      registerError.value = 'Ocurrió un error inesperado. Intenta más tarde.'
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

.gradient-btn {
  background: linear-gradient(90deg, #ff9ad5, #ff7acb);
  color: white !important;
  border-radius: 12px;
}

.titulo {
  font-weight: 700;
}

</style>
