<template>
  <v-container class="my-8">
    <h1 class="text-h4 mb-2" :style="{ color: 'var(--v-theme-primary)' }">Proceso de Reserva</h1>

    <h2 class="text-h6 font-weight-regular mb-4" :style="{ color: 'var(--color-text)' }">
      Paso {{ step }} de 4: {{ steps[step - 1] }}
    </h2>

    <v-divider class="mb-6"></v-divider>

    <v-card elevation="4" rounded="lg">
      <div v-if="step === 1">
        <v-card-title class="text-h6">1. Elige tu Tipo de Sala</v-card-title>
        <v-card-text>
          <v-row v-if="!loading && !error">
            <v-col v-for="sala in salas" :key="sala.idProducto" cols="12" md="4">
              <v-card
                :class="{
                  'selected-card': selectedSala && selectedSala.idProducto === sala.idProducto,
                }"
                @click="selectedSala = sala"
                hover
                elevation="2"
                rounded="lg"
                :style="{
                  borderColor:
                    selectedSala && selectedSala.idProducto === sala.idProducto
                      ? 'var(--v-theme-primary)'
                      : 'transparent',
                }"
                class="card-option"
              >
                <v-img
                  :src="`https://placehold.co/600x400/${sala.idProducto === 1 ? 'BAE6FD' : sala.idProducto === 2 ? 'A7F3D0' : 'F9A8D4'}/4B5563?text=${sala.nombre.replace(' ', '+')}&font=mplus`"
                  height="150"
                  cover
                ></v-img>
                <v-card-title>{{ sala.nombre }}</v-card-title>
                <v-card-subtitle
                  >Precio/h: ${{ sala.precio.toLocaleString('es-CL') }}</v-card-subtitle
                >
                <v-card-text class="text-caption text-justify">{{ sala.descripcion }}</v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions class="px-6 pb-4">
          <v-spacer></v-spacer>
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
      </div>

      <div v-if="step === 2">
        <v-card-title class="text-h6">2. Elige tu Sala, Fecha y Hora</v-card-title>
        <v-card-text v-if="selectedSala">
          <p class="mb-4">Has seleccionado el tipo de sala: **{{ selectedSala.nombre }}**.</p>
          <v-select
            v-if="salasDisponibles.length > 1"
            label="Elige una sala específica"
            v-model="selectedActivoId"
            :items="salasDisponibles"
            item-title="nombreSala"
            item-value="idActivo"
            variant="outlined"
            density="compact"
            class="mb-4"
            prepend-inner-icon="mdi-door-sliding"
          ></v-select>
          <v-alert
            v-else-if="salasDisponibles.length === 1"
            type="info"
            variant="tonal"
            density="compact"
            class="mb-4"
          >
            Se te ha asignado la sala: **{{ salasDisponibles[0].nombreSala }}**
          </v-alert>
          <v-alert
            v-else-if="selectedSala.activos && salasDisponibles.length === 0"
            type="error"
            variant="tonal"
            density="compact"
            class="mb-4"
          >
            Lo sentimos, todas las salas de tipo "{{ selectedSala.nombre }}" están en mantenimiento.
          </v-alert>

          <div v-if="selectedActivoId">
            <v-divider class="my-4"></v-divider>

            <v-row>
              <v-col cols="12" md="5">
                <h3 class="text-subtitle-1 mb-2">Agenda (Reservas Existentes)</h3>
                <div v-if="agendaLoading" class="text-center">
                  <v-progress-circular
                    indeterminate
                    color="primary"
                    size="24"
                  ></v-progress-circular>
                </div>
                <v-alert
                  v-else-if="agenda.length === 0"
                  type="success"
                  variant="tonal"
                  density="compact"
                >
                  ¡Esta sala no tiene reservas!
                </v-alert>
                <v-list
                  v-else
                  density="compact"
                  lines="two"
                  style="background: transparent; max-height: 300px; overflow-y: auto"
                >
                  <v-list-item
                    v-for="reserva in agenda"
                    :key="reserva.idReserva"
                    prepend-icon="mdi-calendar-remove"
                  >
                    <v-list-item-title>Reservado (ID: {{ reserva.idReserva }})</v-list-item-title>
                    <v-list-item-subtitle>
                      {{ new Date(reserva.fechaInicio).toLocaleString('es-CL') }}
                    </v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>

              <v-col cols="12" md="7">
                <h3 class="text-subtitle-1 mb-2">Selecciona tu horario</h3>

                <v-text-field
                  v-model="selectedDate"
                  label="Fecha"
                  type="date"
                  variant="outlined"
                  density="compact"
                  prepend-inner-icon="mdi-calendar"
                  :min="minDate"
                  class="mb-2"
                ></v-text-field>

                <v-select
                  label="Hora de Inicio"
                  v-model="selectedStartTime"
                  :items="timeSlots"
                  variant="outlined"
                  density="compact"
                  prepend-inner-icon="mdi-clock-start"
                  class="mb-2"
                ></v-select>

                <v-select
                  label="Duración"
                  v-model="selectedDuration"
                  :items="durations"
                  item-title="title"
                  item-value="value"
                  variant="outlined"
                  density="compact"
                  prepend-inner-icon="mdi-timer"
                ></v-select>
              </v-col>
            </v-row>
          </div>
        </v-card-text>
        <v-card-actions class="px-6 pb-4">
          <v-btn variant="text" @click="step = 1">Atrás</v-btn>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            @click="step = 3"
            class="font-weight-bold"
            :disabled="!isStep2Complete"
            >Siguiente</v-btn
          >
        </v-card-actions>
      </div>

      <div v-if="step === 3">
        <v-card-title class="text-h6 d-flex justify-space-between align-center">
          <span>3. Añade Snacks y Bebidas</span>
          <span :style="{ color: 'var(--color-heading)' }" class="text-h6">
            Total: ${{ totalConsumibles.toLocaleString('es-CL') }}
          </span>
        </v-card-title>

        <v-card-text>
          <div v-if="consumiblesLoading" class="text-center py-10">
            <v-progress-circular indeterminate color="primary" size="40"></v-progress-circular>
            <p class="mt-2 text-caption">Cargando productos...</p>
          </div>

          <v-alert v-else-if="consumiblesError" type="error" variant="tonal" class="mb-4">
            No se pudieron cargar los consumibles. ¿Iniciaste sesión?
          </v-alert>

          <v-alert v-else-if="consumibles.length === 0" type="info" variant="tonal" class="mb-4">
            No hay snacks ni bebidas disponibles en el catálogo por el momento.
          </v-alert>

          <v-row v-else>
            <v-col v-for="item in consumibles" :key="item.idProducto" cols="12" md="6">
              <v-card class="d-flex" elevation="2" rounded="lg">
                <div>
                  <v-img
                    :src="
                      item.imagenUrl
                        ? item.imagenUrl
                        : `https://placehold.co/150x150/${item.tipo === 'comida' ? 'F9A8D4' : 'A7F3D0'}/4B5563?text=${item.nombre.split(' ')[0]}&font=mplus`
                    "
                    height="130"
                    width="130"
                    cover
                    class="rounded-s-lg"
                  ></v-img>
                </div>

                <div class="d-flex flex-column flex-grow-1 pa-3">
                  <v-card-title class="pa-0 mb-1" style="line-height: 1.2rem">
                    {{ item.nombre }}
                  </v-card-title>
                  <v-card-subtitle
                    class="pa-0 mb-2 font-weight-bold"
                    :style="{ color: 'var(--color-heading)' }"
                  >
                    ${{ item.precio.toLocaleString('es-CL') }}
                  </v-card-subtitle>
                  <v-card-text class="pa-0 text-caption text-justify mb-2" style="min-height: 30px">
                    {{ item.descripcion }}
                  </v-card-text>

                  <v-text-field
                    v-model.number="item.cantidad"
                    label="Cantidad"
                    type="number"
                    min="0"
                    :max="item.stock"
                    variant="outlined"
                    density="compact"
                    hide-details
                    class="mt-auto"
                  >
                    <template v-slot:append>
                      <span class="text-caption mr-2">/{{ item.stock }}</span>
                    </template>
                  </v-text-field>
                </div>
              </v-card>
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions class="px-6 pb-4">
          <v-btn variant="text" @click="step = 2">Atrás</v-btn>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="step = 4" class="font-weight-bold">
            Siguiente (Total: ${{ totalConsumibles.toLocaleString('es-CL') }})
          </v-btn>
        </v-card-actions>
      </div>

      <div v-if="step === 4">
        <v-card-title class="text-h6">4. Confirmación y Pago</v-card-title>

        <v-card-text>
          <v-row>
            <v-col cols="12" md="6">
              <h3 class="text-subtitle-1 mb-2" style="color: var(--v-theme-primary)">
                Detalle de Sala
              </h3>
              <v-list density="compact" style="background: transparent">
                <v-list-item prepend-icon="mdi-account-circle">
                  <v-list-item-title><b>Cliente:</b> {{ clienteNombre }}</v-list-item-title>
                </v-list-item>
                <v-list-item prepend-icon="mdi-door-sliding">
                  <v-list-item-title
                    ><b>Sala:</b>
                    {{
                      salasDisponibles.find((s) => s.idActivo === selectedActivoId)?.nombreSala ||
                      'N/A'
                    }}</v-list-item-title
                  >
                </v-list-item>
                <v-list-item prepend-icon="mdi-calendar">
                  <v-list-item-title
                    ><b>Fecha:</b>
                    {{ new Date(selectedDate).toLocaleDateString('es-CL') }}</v-list-item-title
                  >
                </v-list-item>
                <v-list-item prepend-icon="mdi-clock-start">
                  <v-list-item-title><b>Hora:</b> {{ selectedStartTime }}</v-list-item-title>
                </v-list-item>
                <v-list-item prepend-icon="mdi-timer">
                  <v-list-item-title
                    ><b>Duración:</b> {{ selectedDuration }} hora(s)</v-list-item-title
                  >
                </v-list-item>
                <v-list-item prepend-icon="mdi-cash">
                  <v-list-item-title
                    ><b>Precio Sala:</b> ${{ selectedSala.precio.toLocaleString('es-CL') }} /
                    hora</v-list-item-title
                  >
                </v-list-item>
              </v-list>

              <v-divider class="my-3"></v-divider>

              <div class="text-h6 font-weight-bold text-right px-4">
                TOTAL SALA: ${{ precioTotalSala.toLocaleString('es-CL') }}
              </div>
            </v-col>

            <v-col cols="12" md="6">
              <h3 class="text-subtitle-1 mb-2" style="color: var(--v-theme-primary)">
                Consumibles
              </h3>

              <v-list
                density="compact"
                style="background: transparent; max-height: 250px; overflow-y: auto"
              >
                <v-list-item v-if="selectedConsumibles.length === 0">
                  <v-list-item-title><i>Sin consumibles seleccionados.</i></v-list-item-title>
                </v-list-item>

                <v-list-item v-for="item in selectedConsumibles" :key="item.productoId">
                  <v-list-item-title>
                    {{ consumibles.find((c) => c.idProducto === item.productoId)?.nombre || 'N/A' }}
                  </v-list-item-title>
                  <v-list-item-subtitle>
                    {{ item.cantidad }} x ${{
                      (
                        consumibles.find((c) => c.idProducto === item.productoId)?.precio || 0
                      ).toLocaleString('es-CL')
                    }}
                  </v-list-item-subtitle>
                  <template v-slot:append>
                    <span class="font-weight-bold">
                      ${{
                        (
                          item.cantidad *
                          (consumibles.find((c) => c.idProducto === item.productoId)?.precio || 0)
                        ).toLocaleString('es-CL')
                      }}
                    </span>
                  </template>
                </v-list-item>
              </v-list>

              <v-divider class="my-3"></v-divider>

              <div class="text-h6 font-weight-bold text-right px-4">
                TOTAL CONSUMIBLES: ${{ totalConsumibles.toLocaleString('es-CL') }}
              </div>
            </v-col>
          </v-row>

          <v-divider class="my-4"></v-divider>
          <div
            class="text-h4 font-weight-black text-center"
            :style="{ color: 'var(--color-heading)' }"
          >
            GRAN TOTAL: ${{ granTotal.toLocaleString('es-CL') }}
          </div>

          <v-alert v-if="reservaError" type="error" variant="tonal" density="compact" class="mt-4">
            {{ reservaError }}
          </v-alert>
        </v-card-text>

        <v-card-actions class="px-6 pb-4">
          <v-btn variant="text" @click="step = 3" :disabled="reservaLoading">Atrás</v-btn>
          <v-spacer></v-spacer>
          <v-btn
            color="success"
            @click="confirmReservation"
            class="font-weight-bold"
            prepend-icon="mdi-credit-card-check-outline"
            :loading="reservaLoading"
            size="large"
          >
            Pagar y Confirmar
          </v-btn>
        </v-card-actions>
      </div>
    </v-card>

    <v-overlay v-model="showSuccessAnimation" persistent class="d-flex align-center justify-center">
      <v-card elevation="12" rounded="lg" class="pa-6 text-center" width="300">
        <v-img :src="logo" height="150" contain class="mb-4"></v-img>
        <h2 class="text-h5 font-weight-bold mb-2" style="color: var(--color-heading)">
          ¡Reserva Creada!
        </h2>
        <p class="text-body-1">Tu pago ha sido procesado con éxito.</p>
        <p class="text-caption mt-4">Serás redirigido al inicio en 5 segundos...</p>
        <v-progress-linear indeterminate color="primary" class="mt-4"></v-progress-linear>
      </v-card>
    </v-overlay>
  </v-container>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router' // <-- 1. Importa useRouter
import api from '@/api'
import logo from '@/assets/logo_uta_box.png' // <-- 2. Importa el logo

const route = useRoute()
const router = useRouter() // <-- 3. Inicializa el router
const step = ref(1)
const steps = ref(['Elegir Sala', 'Agenda y Horario', 'Consumibles', 'Confirmar y Pagar'])

const clienteNombre = ref(localStorage.getItem('nombre') || 'N/A')

// --- Paso 1 ---
const salas = ref([])
const loading = ref(false)
const error = ref(false)
const selectedSala = ref(null)

// --- Paso 2 ---
const selectedActivoId = ref(null)
const agenda = ref([])
const agendaLoading = ref(false)
const agendaError = ref(false)
const salasDisponibles = computed(() => {
  if (selectedSala.value && selectedSala.value.activos) {
    return selectedSala.value.activos.filter((activo) => activo.estado === 'disponible')
  }
  return []
})
const minDate = new Date().toISOString().split('T')[0]
const selectedDate = ref(null)
const selectedStartTime = ref(null)
const selectedDuration = ref(1)
const durations = ref([
  { title: '1 hora', value: 1 },
  { title: '2 horas', value: 2 },
  { title: '3 horas', value: 3 },
])
const timeSlots = computed(() => {
  const slots = []
  const closingTime = 20 // 8 PM

  // Si no hay fecha o duración seleccionada, no muestres horas
  if (!selectedDate.value || !selectedDuration.value) {
    return []
  }

  // 1. Obtener solo las reservas para el DÍA seleccionado
  //    (Importante para evitar comparar con reservas de otros días)
  const bookingsOnSelectedDate = agenda.value.filter((reserva) => {
    const bookingStartDate = new Date(reserva.fechaInicio)

    // Obtenemos una representación 'YYYY-MM-DD' de la fecha LOCAL de la reserva
    // para compararla con la fecha 'YYYY-MM-DD' del v-text-field
    const year = bookingStartDate.getFullYear()
    const month = (bookingStartDate.getMonth() + 1).toString().padStart(2, '0')
    const day = bookingStartDate.getDate().toString().padStart(2, '0')
    const localBookingDateString = `${year}-${month}-${day}`

    return localBookingDateString === selectedDate.value
  })

  // 2. Iterar sobre cada hora potencial del día
  const lastStartTime = closingTime - selectedDuration.value
  for (let h = 11; h <= lastStartTime; h++) {
    const potentialStartTimeString = `${h}:00`

    // 3. Crear el rango de tiempo (inicio y fin) que el usuario QUIERE reservar
    const userStart = new Date(`${selectedDate.value}T${potentialStartTimeString}:00`)
    const userEnd = new Date(userStart.getTime() + selectedDuration.value * 60 * 60 * 1000)

    // 4. Comprobar si este rango choca con ALGUNA reserva existente
    //    Usaremos la lógica de "NO CONFLICTO"
    let isAvailable = true // Asumimos que está disponible

    for (const booking of bookingsOnSelectedDate) {
      const bookingStart = new Date(booking.fechaInicio)
      const bookingEnd = new Date(booking.fechaTermino)

      // ¿Cuándo NO hay conflicto?
      // 1. Si el usuario termina ANTES de que la reserva comience
      const endsBefore = userEnd <= bookingStart
      // 2. Si el usuario comienza DESPUÉS de que la reserva termine
      const startsAfter = userStart >= bookingEnd

      // Si NO termina antes Y NO empieza después, ¡ENTONCES hay conflicto!
      if (!endsBefore && !startsAfter) {
        isAvailable = false // Hay un conflicto
        break // No necesitamos seguir revisando
      }
    }

    // 5. Si, después de revisar TODAS las reservas, sigue disponible...
    if (isAvailable) {
      slots.push(potentialStartTimeString)
    }
  }

  return slots
})

// Resetea la hora si la fecha cambia (ya que la disponibilidad es por día)
watch(selectedDate, () => {
  selectedStartTime.value = null
})

watch(selectedDuration, (newDuration) => {
  selectedStartTime.value = null // resetea
})
const isStep2Complete = computed(() => {
  return selectedActivoId.value && selectedDate.value && selectedStartTime.value
})

// --- Paso 3 ---
const consumibles = ref([])
const consumiblesLoading = ref(false)
const consumiblesError = ref(false)

const totalConsumibles = computed(() => {
  return consumibles.value.reduce((total, item) => {
    const cantidad = item.cantidad || 0
    return total + item.precio * cantidad
  }, 0)
})

const selectedConsumibles = computed(() => {
  return consumibles.value
    .filter((item) => item.cantidad && item.cantidad > 0)
    .map((item) => ({
      productoId: item.idProducto,
      cantidad: item.cantidad,
    }))
})

const reservaLoading = ref(false) // Para el spinner del botón "Pagar"
const reservaError = ref(null) // Para mostrar errores del backend
const showSuccessAnimation = ref(false) // Para mostrar el overlay de 5 seg

// Calcula el precio de la sala (Precio/h * Duración)
const precioTotalSala = computed(() => {
  if (selectedSala.value && selectedDuration.value) {
    return selectedSala.value.precio * selectedDuration.value
  }
  return 0
})

// Calcula el Gran Total (Sala + Consumibles)
const granTotal = computed(() => {
  return precioTotalSala.value + totalConsumibles.value
})
// =============================================

onMounted(() => {
  fetchSalas()
})

watch(step, (newStep) => {
  if (newStep === 3 && consumibles.value.length === 0) {
    fetchConsumibles()
  }
})

const fetchSalas = async () => {
  loading.value = true
  error.value = false
  try {
    const response = await api.get('/productos/salas')
    console.log('DATOS CRUDOS RECIBIDOS DEL BACKEND:', response.data)
    salas.value = response.data
    const preselectedId = route.params.id
    if (preselectedId) {
      const foundSala = salas.value.find((s) => s.idProducto.toString() === preselectedId)
      if (foundSala) {
        selectedSala.value = foundSala
      }
    }
  } catch (err) {
    console.error('Error al cargar salas:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

const fetchConsumibles = async () => {
  consumiblesLoading.value = true
  consumiblesError.value = false
  try {
    const response = await api.get('/productos')
    const items = response.data
      .filter((p) => p.tipo === 'comida' || p.tipo === 'bebida')
      .map((p) => ({ ...p, cantidad: 0 }))
    consumibles.value = items
  } catch (err) {
    console.error('Error al cargar consumibles:', err)
    consumiblesError.value = true
  } finally {
    consumiblesLoading.value = false
  }
}

watch(selectedSala, (newSala) => {
  selectedActivoId.value = null
  agenda.value = []
  selectedDate.value = null
  selectedStartTime.value = null
  selectedDuration.value = 1
  if (newSala && salasDisponibles.value.length === 1) {
    selectedActivoId.value = salasDisponibles.value[0].idActivo
  }
})

watch(selectedActivoId, (newId) => {
  selectedDate.value = null
  selectedStartTime.value = null
  selectedDuration.value = 1
  if (newId) {
    fetchAgenda(newId)
  } else {
    agenda.value = []
  }
})

const fetchAgenda = async (activoId) => {
  agendaLoading.value = true
  agendaError.value = false
  try {
    const response = await api.get(`/reservas/activo/${activoId}`)
    agenda.value = response.data
  } catch (err) {
    console.error('Error cargando agenda:', err)
    agendaError.value = true
  } finally {
    agendaLoading.value = false
  }
}

const confirmReservation = async () => {
  reservaLoading.value = true
  reservaError.value = null // --- 1. Formatear Fechas ---

  const localDateTimeString = `${selectedDate.value}T${selectedStartTime.value}:00`
  const fechaInicio = new Date(localDateTimeString) // Calcula la fecha de término sumando la duración en horas

  // Ya no necesitamos las líneas con .split() o setHours()
  const fechaTermino = new Date(fechaInicio.getTime() + selectedDuration.value * 60 * 60 * 1000)

  // --- 2. Crear el DTO de la Petición ---
  const reservaRequestDTO = {
    activoId: selectedActivoId.value,
    fechaInicio: fechaInicio.toISOString(), // Envía en formato ISO (UTC)
    fechaTermino: fechaTermino.toISOString(), // Envía en formato ISO (UTC)
    consumibles: selectedConsumibles.value, // La lista que ya teníamos
  }

  console.log('Enviando reserva:', reservaRequestDTO)

  // --- 3. Enviar al Backend ---
  try {
    // Usamos api.js, que ya inyecta el Token de Auth
    await api.post('/reservas/reservar', reservaRequestDTO) //

    // --- ¡ÉXITO! ---
    reservaLoading.value = false
    showSuccessAnimation.value = true // Activa el overlay

    // Espera 5 segundos y luego redirige al Home
    setTimeout(() => {
      showSuccessAnimation.value = false
      router.push('/') // Redirige al Home
    }, 5000)
  } catch (err) {
    // --- ¡ERROR! ---
    console.error('Error al crear la reserva:', err)
    reservaLoading.value = false

    // Muestra el error específico del backend si existe
    if (err.response && err.response.data && err.response.data.message) {
      reservaError.value = err.response.data.message
    } else {
      reservaError.value = 'Error desconocido. Intenta de nuevo.'
    }
  }
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
