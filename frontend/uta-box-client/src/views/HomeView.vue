<template>
  <v-container fluid class="pa-0">
    
    <!-- 1. SECCIÓN DE BIENVENIDA (HÉROE) -->
    <v-parallax
      height="400"
      src="https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80"
    >
      <div class="d-flex flex-column fill-height justify-center align-center text-white">
        <h1 class="text-h2 font-weight-thin mb-4">
          UTA-BOX
        </h1>
        <h4 class="subheading text-h5">
          Tu experiencia de Karaoke Privado
        </h4>
      </div>
    </v-parallax>

    <!-- 2. SECCIÓN DE "CÓMO FUNCIONA" -->
    <v-container class="my-10">
      <v-row>
        <v-col cols="12">
          <h2 class="text-h4 text-center mb-8">¿Cómo reservar?</h2>
        </v-col>

        <!-- Paso 1 -->
        <v-col cols="12" md="4">
          <v-card class="mx-auto" max-width="344" elevation="4">
            <v-card-item>
                <v-icon icon="mdi-account-circle" size="x-large" color="primary"></v-icon>
            </v-card-item>
            <v-card-title class="font-weight-bold">1. Inicia Sesión</v-card-title>
            <v-card-text>
              Regístrate o inicia sesión para acceder a nuestro catálogo de salas y promociones exclusivas.
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Paso 2 -->
        <v-col cols="12" md="4">
          <v-card class="mx-auto" max-width="344" elevation="4">
            <v-card-item>
                <v-icon icon="mdi-calendar-clock" size="x-large" color="primary"></v-icon>
            </v-card-item>
            <v-card-title class="font-weight-bold">2. Elige tu Sala y Hora</v-card-title>
            <v-card-text>
              Revisa la disponibilidad en tiempo real y selecciona la sala y el horario que más te acomode.
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Paso 3 -->
        <v-col cols="12" md="4">
          <v-card class="mx-auto" max-width="344" elevation="4">
            <v-card-item>
                <v-icon icon="mdi-food-fork-drink" size="x-large" color="primary"></v-icon>
            </v-card-item>
            <v-card-title class="font-weight-bold">3. Añade Snacks y Bebidas</v-card-title>
            <v-card-text>
              Completa tu reserva añadiendo tus consumibles favoritos. ¡Nosotros nos encargamos del resto!
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- 3. (PRÓXIMAMENTE) LISTADO DE SALAS -->
    <v-container class="my-10">
      <h2 class="text-h4 text-center mb-8">Nuestras Salas</h2>
      <!-- (Aquí iría el v-for para listar las salas de /api/productos/salas) -->
      <p class="text-center">¡Catálogo de salas próximamente!</p>
    </v-container>

  </v-container>
</template>

<script setup>
// En esta página puedes cargar las salas públicas
// import api from '../api'
// import { ref, onMounted } from 'vue'
// const salas = ref([])
// onMounted(async () => {
//   const response = await api.get('/productos/salas')
//   salas.value = response.data
// })
</script>