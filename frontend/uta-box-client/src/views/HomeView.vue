<template>
  <v-container fluid class="pa-0">
    <!-- Banner Parallax -->
    <v-parallax
      height="400"
      src="https://static.vecteezy.com/system/resources/previews/028/215/635/large_2x/music-karaoke-party-vivid-background-free-photo.jpg"
    >
      <v-row class="fill-height ma-0" align="center" justify="center">
        <v-col class="text-center pa-6 pa-md-12" cols="12" md="8" lg="7">
          <div>
            <div>
              <div class="banner-line-item text-h5 text-md-h3 font-weight-bold">
                🎤 ¡Reserva tu sala de Karaoke!
              </div>
            </div>

            <div>
              <div class="banner-line-item text-h6 text-md-h4 font-weight-regular">
                ⏰ Hasta 3 horas
              </div>
            </div>

            <div>
              <div class="banner-line-item text-h6 text-md-h4 font-weight-regular">
                🍿 Snacks y Bebidas
              </div>
            </div>

            <div>
              <div class="banner-line-item text-h6 text-md-h4 font-weight-regular">
                🎨 ¡Salas temáticas!
              </div>
            </div>
          </div>
        </v-col>
      </v-row>
    </v-parallax>
    <!-- Fin Banner Parallax -->

    <v-container class="my-10">
      <v-row>
        <v-col cols="12">
          <h2 class="text-h4 text-center mb-8" style="color: #e07ab9">¿Cómo reservar?</h2>
        </v-col>
      </v-row>
      <v-row class="mt-4" justify="center" align="center">
        <v-col cols="12" md="5" class="d-flex align-center justify-center pa-4">
          <v-img :src="miLogo" max-width="400" alt="Logo Uta-Box Kawaii" contain />
        </v-col>
        <v-col cols="12" md="7">
          <v-card class="mx-auto mb-2" max-width="500" elevation="4" rounded="lg">
            <v-card-text class="pa-4">
              <div class="d-flex align-center">
                <v-icon
                  icon="mdi-account-circle"
                  size="x-large"
                  color="primary"
                  class="mr-4"
                ></v-icon>
                <div>
                  <div class="text-h6 font-weight-bold" style="color: #4b5563">Inicia sesión</div>
                  <div class="text-body-1">
                    Regístrate o inicia sesión para acceder a nuestro catálogo de salas y
                    promociones exclusivas.
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
                <v-icon
                  icon="mdi-calendar-clock"
                  size="x-large"
                  color="primary"
                  class="mr-4"
                ></v-icon>
                <div>
                  <div class="text-h6 font-weight-bold" style="color: #4b5563">
                    Elige tu sala y hora
                  </div>
                  <div class="text-body-1">
                    Revisa la disponibilidad en tiempo real y selecciona la sala y el horario que
                    más te acomode.
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
                <v-icon
                  icon="mdi-food-fork-drink"
                  size="x-large"
                  color="primary"
                  class="mr-4"
                ></v-icon>
                <div>
                  <div class="text-h6 font-weight-bold" style="color: #4b5563">
                    Añade snacks y bebidas
                  </div>
                  <div class="text-body-1">
                    Completa tu reserva añadiendo tus consumibles favoritos. ¡Nosotros nos
                    encargamos del resto!
                  </div>
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <v-container class="my-10">
      <h2 class="text-h4 text-center mb-8" style="color: #e07ab9">Nuestras Salas</h2>

      <v-row v-if="loading" class="justify-center">
        <v-progress-circular indeterminate color="primary"></v-progress-circular>
      </v-row>

      <v-row v-else-if="error" class="justify-center">
        <v-alert type="error" variant="tonal">No se pudieron cargar las salas.</v-alert>
      </v-row>

      <v-row v-else>
        <v-col v-for="sala in salas" :key="sala.idProducto" cols="12" md="4">
          <v-card class="mx-auto" elevation="4" rounded="lg">
            <v-carousel
              height="200px"
              cycle
              hide-delimiter-background
              :show-arrows="false"
              delimiter-icon="mdi-circle-small"
            >
              <v-carousel-item
                v-for="(img, i) in sala.imagenes"
                :key="i"
                :src="img"
                cover
              ></v-carousel-item>
            </v-carousel>
            <v-card-title
              class="d-flex align-center justify-space-between font-weight-bold"
              style="color: #4b5563"
            >
              <div>
                <v-icon
                  :color="
                    sala.idProducto === 1 ? 'info' : sala.idProducto === 2 ? 'secondary' : 'primary'
                  "
                  left
                  class="mr-2"
                >
                  {{
                    sala.idProducto === 1
                      ? 'mdi-crown'
                      : sala.idProducto === 2
                        ? 'mdi-account-group'
                        : 'mdi-star-four-points-outline'
                  }}
                </v-icon>
                {{ sala.nombre }}
              </div>
            </v-card-title>
            <v-card-text>
              <div class="text-body-1 mb-2 text-justify" style="min-height: 60px">
                {{ sala.descripcion }}
              </div>
            <v-list-item
              prepend-icon="mdi-account-group"
              title="Capacidad"
              subtitle="Hasta 10 personas"
            ></v-list-item>
            <v-list-item
              prepend-icon="mdi-television-classic"
              title="Equipamiento"
              subtitle='Pantalla 65\" 4K, Micrófonos Pro, Luces DMX, '
              lines="three"
            ></v-list-item>
            <v-list-item
              prepend-icon="mdi-sofa"
              title="Temática"
              subtitle="Estilo estadio Budokan para conciertos multitudinarios"
            ></v-list-item>
                          <div class="text-h6 font-weight-bold text-center mt-2" style="color: #d946ef">
                ${{ sala.precio.toLocaleString('es-CL') }} / hora
              </div>
            </v-card-text>

            <v-card-actions class="pa-3">
              <v-btn
                :to="`/reservar/${sala.idProducto}`"
                :style="{
                  backgroundColor:
                    sala.idProducto === 1
                      ? '#BAE6FD'
                      : sala.idProducto === 2
                        ? '#A7F3D0'
                        : '#F9A8D4',
                  color: '#4B5563',
                }"
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

// --- Variables para el Modal ---
const dialog = ref(false)
const selectedSala = ref(null)

// --- Función para abrir el Modal ---
const showSalaDetails = (sala) => {
  selectedSala.value = sala
  dialog.value = true
}

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
    salas.value = response.data.map((sala) => {
      // Creamos un array de imágenes
      const imagenes = []
      // 1. Añadimos la imagen principal (si existe)
      if (sala.imagenUrl) {
        imagenes.push(sala.imagenUrl)
      }
      // 2. Añadimos la imagen de placeholder (como segunda imagen de ejemplo)
      const placeholder = `https://placehold.co/600x400/${sala.idProducto === 1 ? 'BAE6FD' : sala.idProducto === 2 ? 'A7F3D0' : 'F9A8D4'}/4B5563?text=${sala.nombre.replace(' ', '+')}+2&font=mplus`
      imagenes.push(placeholder)
      // 3. Añadimos la foto del banner como tercera imagen de ejemplo
      imagenes.push(
        'https://static.vecteezy.com/system/resources/previews/028/215/635/large_2x/music-karaoke-party-vivid-background-free-photo.jpg',
      )
      // Devolvemos la sala original + el nuevo array de imágenes
      return { ...sala, imagenes: imagenes }
    })
  } catch (err) {
    console.error('Error al cargar salas:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.banner-title,
.banner-subtitle {
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

.banner-line-item {
  font-family: 'M PLUS Rounded 1c', sans-serif;
  color: white;
  /* Sombra más fuerte para mejor contraste */
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.7);
  line-height: 1.3;
  background-color: rgba(0, 0, 0, 0.25); /* Fondo negro semitransparente */
  padding: 4px 12px; /* Relleno para que el texto respire */
  border-radius: 8px; /* Bordes redondeados "kawaii" */
  display: inline-block; /* Clave: hace que el fondo se ajuste al texto */
  margin-bottom: 12px; /* Espacio entre las líneas */
}

.banner-title,
.banner-subtitle {
  font-family: 'M PLUS Rounded 1c', sans-serif;
}
</style>
