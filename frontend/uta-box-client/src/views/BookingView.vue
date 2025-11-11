<template>
  <v-container class="my-8">
    <h1 class="text-h4 mb-6" :style="{ color: 'var(--v-theme-primary)' }">Proceso de Reserva</h1>

    <v-stepper
      v-model="step"
      :items="steps"
      bg-color="var(--v-theme-surface)"
      :flat="true"
      rounded="lg"
      elevation="4"
    >
      <template v-slot:item.1>
        <v-card flat>
          <v-card-title class="text-h6 mb-4">1. Elige tu Tipo de Sala</v-card-title>
          <v-card-text>
            <div v-if="loading" class="text-center py-5">
              <v-progress-circular indeterminate color="primary"></v-progress-circular>
              <p class="mt-2 text-caption">Cargando tipos de sala...</p>
            </div>
            <v-alert v-else-if="error" type="error" variant="tonal" class="mb-4">
              No se pudo cargar las salas.
            </v-alert>
            
            <v-row v-else>
              <v-col v-for="sala in salas" :key="sala.idProducto" cols="12" md="4">
                <v-card 
                  :class="{'selected-card': selectedSala && selectedSala.idProducto === sala.idProducto}"
                  @click="selectedSala = sala"
                  hover
                  elevation="2"
                  rounded="lg"
                  :style="{borderColor: selectedSala && selectedSala.idProducto === sala.idProducto ? 'var(--v-theme-primary)' : 'transparent'}"
                  class="card-option"
                >
                  <v-img
                    :src="`https://placehold.co/600x400/${sala.tipo === 'Kawaii' ? 'F9A8D4' : 'A7F3D0'}/4B5563?text=${sala.nombre.replace(' ', '+')}&font=mplus`"
                    height="150"
                    cover
                  ></v-img>
                  <v-card-title>{{ sala.nombre }}</v-card-title>
                  <v-card-subtitle>Precio/h: ${{ sala.precio.toLocaleString('es-CL') }}</v-card-subtitle>
                  <v-card-text class="text-caption">{{ sala.descripcion }}</v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>

          <v-card-actions class="px-6 pb-4">
            <v-btn 
              color="primary" 
              :disabled="!selectedSala" 
              @click="step = 2"
              prepend-icon="mdi-arrow-right"
              class="font-weight-bold"
            >
              Continuar
            </v-btn>
          </v-card-actions>
        </v-card>
      </template>
      
      <template v-slot:item.2>
        <v-card flat>
          <v-card-title class="text-h6 mb-4">2. Elige tu Sala y Horario</v-card-title>
          <v-card-text v-if="selectedSala">
            <p class="mb-4">
              Has seleccionado el tipo de sala: **{{ selectedSala.nombre }}**.
            </p>

            <v-select
              v-if="selectedSala.activos && selectedSala.activos.length > 1"
              label="Elige una sala específica"
              v-model="selectedActivoId"
              :items="selectedSala.activos"
              item-title="nombreSala"
              item-value="idActivo"
              variant="outlined"
              density="compact"
              class="mb-4"
              prepend-inner-icon="mdi-door-sliding"
            ></v-select>

            <v-alert
              v-else-if="selectedSala.activos && selectedSala.activos.length === 1"
              type="info"
              variant="tonal"
              density="compact"
              class="mb-4"
            >
              Se te ha asignado la sala: **{{ selectedSala.activos[0].nombreSala }}**
            </v-alert>

            <div v-if="selectedActivoId">
              <h3 class="text-subtitle-1 mt-6 mb-2">Agenda para esta sala</h3>
              <div v-if="agendaLoading" class="text-center">
                <v-progress-circular indeterminate color="primary" size="24"></v-progress-circular>
                <p class="text-caption">Cargando agenda...</p>
              </div>
              <v-alert v-else-if="agendaError" type="error" variant="tonal" density="compact">
                Error al cargar la agenda.
              </v-alert>
              <v-alert v-else-if="agenda.length === 0" type="success" variant="tonal" density="compact">
                ¡Esta sala no tiene reservas!
              </v-alert>
              <v-list v-else density="compact" lines="two">
                <v-list-item
                  v-for="reserva in agenda"
                  :key="reserva.idReserva"
                  prepend-icon="mdi-calendar-remove"
                >
                  <v-list-item-title>Reservado (ID: {{ reserva.idReserva }})</v-list-item-title>
                  <v-list-item-subtitle>
                    Inicio: {{ new Date(reserva.fechaInicio).toLocaleString() }} | 
                    Término: {{ new Date(reserva.fechaTermino).toLocaleString() }}
                  </v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </div>
            
            <v-alert type="warning" variant="tonal" density="compact" class="mt-4" v-if="selectedActivoId">
              PENDIENTE: Implementar el Calendario/Selector de Hora.
            </v-alert>

          </v-card-text>
          <v-card-actions class="px-6 pb-4">
            <v-btn variant="text" @click="step = 1">Atrás</v-btn>
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="step = 3" class="font-weight-bold" :disabled="!selectedActivoId">Siguiente</v-btn>
          </v-card-actions>
        </v-card>
      </template>

      <template v-slot:item.3>
        <v-card flat>
          <v-card-title class="text-h6 mb-4">3. Añade Snacks y Bebidas</v-card-title>
          <v-card-text>
             <v-alert type="warning" variant="tonal" density="compact">
              PENDIENTE: Implementar selección de Consumibles (usando la API `/api/productos?tipo=comida/bebida`).
            </v-alert>
          </v-card-text>
          <v-card-actions class="px-6 pb-4">
            <v-btn variant="text" @click="step = 2">Atrás</v-btn>
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="step = 4" class="font-weight-bold">Siguiente</v-btn>
          </v-card-actions>
        </v-card>
      </template>

      <template v-slot:item.4>
        </template>
      
    </v-stepper>
  </v-container>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue' // <-- Importa 'watch'
import { useRoute } from 'vue-router'
import api from '@/api'

const route = useRoute()
const step = ref(1)
const steps = ref([
  'Elegir Sala',
  'Agenda y Horario',
  'Consumibles',
  'Confirmar y Pagar'
])

// --- Paso 1: Selección de Tipo de Sala ---
const salas = ref([]) // Lista de Productos (tipos de sala)
const loading = ref(false)
const error = ref(false)
const selectedSala = ref(null) // El Producto (tipo de sala) seleccionado

// --- Paso 2: Selección de Sala Física y Agenda ---
const selectedActivoId = ref(null) // El ID de la sala física (Activo)
const agenda = ref([])
const agendaLoading = ref(false)
const agendaError = ref(false)


onMounted(() => {
  fetchSalas()
})

const fetchSalas = async () => {
  loading.value = true
  error.value = false
  try {
    // 1. Llama a la API (que ahora trae los 'activos' gracias al EAGER)
    const response = await api.get('/productos/salas')
    salas.value = response.data
    
    // 2. Revisa si la URL traía un ID (de HomeView)
    const preselectedId = route.params.id;
    if (preselectedId) {
      const foundSala = salas.value.find(s => s.idProducto.toString() === preselectedId);
      if (foundSala) {
        selectedSala.value = foundSala; // Preselecciona el TIPO de sala
      }
    }
  } catch (err) {
    console.error("Error al cargar salas:", err)
    error.value = true
  } finally {
    loading.value = false
  }
}

// --- ¡NUEVA LÓGICA PARA EL PASO 2! ---

// 1. Observa si el TIPO de sala cambia
watch(selectedSala, (newSala) => {
  // Limpiamos la selección anterior
  selectedActivoId.value = null
  agenda.value = []

  if (newSala && newSala.activos) {
    // Si el tipo de sala seleccionado (ej. VIP) solo tiene UNA sala física...
    if (newSala.activos.length === 1) {
      // ...la seleccionamos automáticamente.
      selectedActivoId.value = newSala.activos[0].idActivo
    }
    // Si tiene múltiples (ej. IDOL), selectedActivoId queda null, forzando
    // al usuario a elegir en el v-select.
  }
})

// 2. Observa si la SALA FÍSICA (Activo) cambia
watch(selectedActivoId, (newId) => {
  if (newId) {
    // Si se seleccionó una sala física, busca su agenda
    fetchAgenda(newId)
  } else {
    // Si se des-seleccionó, limpia la agenda
    agenda.value = []
  }
})

// 3. Función que llama a la API de Reservas
const fetchAgenda = async (activoId) => {
  agendaLoading.value = true
  agendaError.value = false
  try {
    // Llama al endpoint público que creaste
    const response = await api.get(`/reservas/activo/${activoId}`)
    agenda.value = response.data
  } catch (err) {
    console.error("Error cargando agenda:", err)
    agendaError.value = true
  } finally {
    agendaLoading.value = false
  }
}

// Lógica de confirmación final (sin cambios)
const confirmReservation = () => {
  alert("Reserva confirmada! (Lógica final pendiente)")
}

</script>

<style scoped>
/* (Tus estilos de 'selected-card' siguen igual) */
.card-option {
  transition: all 0.2s ease-in-out;
  cursor: pointer;
  border: 3px solid transparent;
}
.card-option:hover {
  transform: translateY(-2px);
}
.selected-card {
  border-color: var(--v-theme-primary) !important;
  box-shadow: 0 4px 20px rgba(249, 168, 212, 0.4) !important;
}
</style>