<template>
  <v-container fluid class="pa-0">
    
    <v-parallax
      height="400"
      src="https://static.vecteezy.com/system/resources/previews/028/215/635/large_2x/music-karaoke-party-vivid-background-free-photo.jpg"
    >
      <v-row class="fill-height ma-0" align="center" justify="center">
        <v-col class="text-center" cols="12">
          <div class="banner-text-container">
            <h1 class="font-weight-black banner-title">UTA-BOX</h1>
            <h2 class="font-weight-regular banner-subtitle">KARAOKE</h2>
          </div>
        </v-col>
      </v-row>
    </v-parallax>

    <v-container class="my-10">
      <v-row>
        <v-col cols="12">
          <h2 class="text-h4 text-center mb-8" style="color: #F9A8D4;">¿Cómo reservar?</h2>
        </v-col>
      </v-row>
      <v-row class="mt-4" justify="center" align="center">
        <v-col cols="12" md="5" class="d-none d-md-flex align-center justify-center pa-4">
          <v-img
            :src="miLogo"
            max-width="400"
            alt="Logo Uta-Box Kawaii"
            contain 
          />
        </v-col>
        <v-col cols="12" md="7">
          <v-card class="mx-auto mb-2" max-width="500" elevation="4" rounded="lg">
            <v-card-text class="pa-4">
              <div class="d-flex align-center">
                <v-icon icon="mdi-account-circle" size="x-large" color="primary" class="mr-4"></v-icon>
                <div>
                  <div class="text-h6 font-weight-bold" style="color: #4B5563;">1. Inicia Sesión</div>
                  <div class="text-body-1">
                    Regístrate o inicia sesión para acceder a nuestro catálogo de salas y promociones exclusivas.
                  </div>
                </div>
              </div>
            </v-card-text>
          </v-card>
          <div class="text-center my-1">
            <v-icon color="primary" size="36">mdi-arrow-down-bold</v-icon>
          </div>
          <v-card class="mx-auto mb-2" max-width="500" elevation="4" rounded="lg">
            <v-card-text class="pa-4">
              <div class="d-flex align-center">
                <v-icon icon="mdi-calendar-clock" size="x-large" color="primary" class="mr-4"></v-icon>
                <div>
                  <div class="text-h6 font-weight-bold" style="color: #4B5563;">2. Elige tu Sala y Hora</div>
                  <div class="text-body-1">
                    Revisa la disponibilidad en tiempo real y selecciona la sala y el horario que más te acomode.
                  </div>
                </div>
              </div>
            </v-card-text>
          </v-card>
          <div class="text-center my-1">
            <v-icon color="primary" size="36">mdi-arrow-down-bold</v-icon>
          </div>
          <v-card class="mx-auto mb-2" max-width="500" elevation="4" rounded="lg">
            <v-card-text class="pa-4">
              <div class="d-flex align-center">
                <v-icon icon="mdi-food-fork-drink" size="x-large" color="primary" class="mr-4"></v-icon>
                <div>
                  <div class="text-h6 font-weight-bold" style="color: #4B5563;">3. Añade Snacks y Bebidas</div>
                  <div class="text-body-1">
                    Completa tu reserva añadiendo tus consumibles favoritos. ¡Nosotros nos encargamos del resto!
                  </div>
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <v-container class="my-10">
      <h2 class="text-h4 text-center mb-8" style="color: #F9A8D4;">Nuestras Salas</h2>
      
      <v-row v-if="loading" class="justify-center">
        <v-progress-circular indeterminate color="primary"></v-progress-circular>
      </v-row>
      
      <v-row v-else-if="error" class="justify-center">
        <v-alert type="error" variant="tonal">No se pudieron cargar las salas.</v-alert>
      </v-row>

      <v-row v-else>
        <v-col v-for="sala in salas" :key="sala.idProducto" cols="12" md="4">
          <v-card class="mx-auto" elevation="4" rounded="lg">
            <v-img
              :src="`https://placehold.co/600x400/${sala.idProducto === 1 ? 'BAE6FD' : (sala.idProducto === 2 ? 'A7F3D0' : 'F9A8D4')}/4B5563?text=${sala.nombre.replace(' ', '+')}&font=mplus`"
              height="200px"
              cover
            ></v-img>
            
            <v-card-title class="font-weight-bold text-center " style="color: #4B5563;">
              <v-icon :color="sala.idProducto === 1 ? 'info' : (sala.idProducto === 2 ? 'secondary' : 'primary')" left class="mr-2">
                {{ sala.idProducto === 1 ? 'mdi-crown' : (sala.idProducto === 2 ? 'mdi-account-group' : 'mdi-star-four-points-outline') }}
              </v-icon>
              {{ sala.nombre }}
            </v-card-title>
            
            <v-card-text>
              <div class="text-body-1 mb-2 text-justify" style="min-height: 60px;">
                {{ sala.descripcion }}
              </div>
              <div class="text-h6 font-weight-bold text-center" style="color: #D946EF;">
                ${{ sala.precio.toLocaleString('es-CL') }} / hora
              </div>
            </v-card-text>
            
            <v-card-actions class="pa-3">              
              <v-btn 
                :to="`/reservar/${sala.idProducto}`"
                :style="{backgroundColor: sala.idProducto === 1 ? '#BAE6FD' : (sala.idProducto === 2 ? '#A7F3D0' : '#F9A8D4'), color: '#4B5563'}"
                block 
                rounded="lg" 
                size="large" 
                class="font-weight-bold"
              >
                Reservar Ahora
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
      </v-container>

  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import miLogo from '@/assets/logo_uta_box.png' // Para la sección "Cómo reservar"
import api from '@/api' // Importamos el API helper

// --- Lógica para cargar las salas ---
const salas = ref([])
const loading = ref(false)
const error = ref(false)

onMounted(() => {
  fetchSalas()
})

// Llama al backend para obtener solo los productos de tipo 'sala'
const fetchSalas = async () => {
  loading.value = true
  error.value = false
  try {
    // Usamos el endpoint GET /api/productos/salas (público)
    const response = await api.get('/productos/salas') //
    salas.value = response.data
  } catch (err) {
    console.error("Error al cargar salas:", err)
    error.value = true
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* (Tus estilos de 'banner' van aquí) */
.banner-text-container {
  background-color: rgba(171, 222, 237, 0.85); 
  padding: 15px 30px; 
  border-radius: 15px; 
  display: inline-block; 
  margin-top: 20px; 
}

.banner-title, .banner-subtitle {
  font-family: 'M PLUS Rounded 1c', sans-serif;
  color: white; 
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.4); 
  margin: 0; 
  line-height: 1.2; 
}

.banner-title {
  font-size: 4.5rem !important; 
  letter-spacing: 0.1em; 
}

.banner-subtitle {
  font-size: 2.2rem !important;
  letter-spacing: 0.15em; 
  margin-top: -5px; 
}

@media (max-width: 600px) {
  .banner-title {
    font-size: 3rem !important;
  }
  .banner-subtitle {
    font-size: 1.5rem !important;
  }
  .banner-text-container {
    padding: 10px 20px;
  }
}
</style>