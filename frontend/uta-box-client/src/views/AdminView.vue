<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4 mt-4" :style="{ color: 'var(--v-theme-primary)' }">
          Panel de Administración
        </h1>
      </v-col>
    </v-row>

    <v-tabs v-model="tab" align-tabs="center" color="primary" grow>
      <v-tab value="consumibles">Consumibles (Comida/Bebida)</v-tab>
      <v-tab value="salas">Salas</v-tab>
    </v-tabs>

    <v-window v-model="tab">
      <v-window-item value="consumibles">
        <v-card elevation="4" rounded="lg" class="mt-4">
          <v-card-title
            class="d-flex align-center pe-2"
            :style="{ color: 'var(--v-theme-on-surface)' }"
          >
            <v-icon icon="mdi-food-fork-drink"></v-icon> &nbsp; Productos Consumibles

            <v-spacer></v-spacer>

            <v-dialog v-model="dialogProducto" max-width="500px">
              <template v-slot:activator="{ props }">
                <v-btn color="primary" variant="tonal" v-bind="props" prepend-icon="mdi-plus">
                  Nuevo Consumible
                </v-btn>
              </template>

              <v-card rounded="lg">
                <v-card-title>
                  <span class="text-h5">{{ formTitleProducto }}</span>
                </v-card-title>

                <v-card-text>
                  <v-alert
                    v-if="saveProductoError"
                    type="error"
                    variant="tonal"
                    density="compact"
                    class="mb-4"
                  >
                    {{ saveProductoError }}
                  </v-alert>
                  <v-container>
                    <v-row>
                      <v-col cols="12" sm="6">
                        <v-text-field
                          v-model="editedProducto.nombre"
                          label="Nombre"
                          variant="outlined"
                          density="compact"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-select
                          v-model="editedProducto.tipo"
                          label="Tipo"
                          :items="['comida', 'bebida']"
                          variant="outlined"
                          density="compact"
                        ></v-select>
                      </v-col>
                      <v-col cols="12">
                        <v-textarea
                          v-model="editedProducto.descripcion"
                          label="Descripción"
                          rows="2"
                          variant="outlined"
                          density="compact"
                        ></v-textarea>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-text-field
                          v-model.number="editedProducto.precio"
                          label="Precio (ej. 5000)"
                          type="number"
                          prefix="$"
                          variant="outlined"
                          density="compact"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-text-field
                          v-model.number="editedProducto.stock"
                          label="Stock"
                          type="number"
                          variant="outlined"
                          density="compact"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12">
                        <v-text-field
                          v-model="editedProducto.imagenUrl"
                          label="URL de la Imagen"
                          prepend-inner-icon="mdi-image"
                          variant="outlined"
                          density="compact"
                        ></v-text-field>
                      </v-col>
                    </v-row>
                  </v-container>
                </v-card-text>

                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="grey" variant="text" @click="closeProducto">Cancelar</v-btn>
                  <v-btn color="primary" variant="tonal" @click="saveProducto">Guardar</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
            <v-dialog v-model="dialogDeleteProducto" max-width="500px">
              <v-card rounded="lg">
                <v-card-title class="text-h5 text-center"
                  >¿Estás seguro de borrarlo?</v-card-title
                >
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="grey" variant="text" @click="closeDeleteProducto">Cancelar</v-btn>
                  <v-btn color="error" variant="tonal" @click="deleteProductoConfirm"
                    >SÍ, Borrar</v-btn
                  >
                  <v-spacer></v-spacer>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-card-title>

          <v-table density="compact" class="elevation-0">
            <thead>
              <tr>
                <th class="text-left font-weight-bold">ID</th>
                <th class="text-left font-weight-bold">Nombre</th>
                <th class="text-left font-weight-bold">Tipo</th>
                <th class="text-left font-weight-bold">Precio</th>
                <th class="text-left font-weight-bold">Stock</th>
                <th class="text-left font-weight-bold">Acciones</th>
              </tr>
            </thead>

            <tbody>
              <tr v-if="loadingProductos">
                <td :colspan="6" class="text-center py-4">
                  <v-progress-circular indeterminate color="primary"></v-progress-circular>
                  <div class="mt-2 text-caption">Cargando consumibles...</div>
                </td>
              </tr>

              <tr v-else-if="productos.length === 0">
                <td :colspan="6" class="text-center py-4 text-medium-emphasis">
                  No hay consumibles en el catálogo. ¡Crea uno!
                </td>
              </tr>

              <tr v-else v-for="item in productos" :key="item.idProducto">
                <td>{{ item.idProducto }}</td>
                <td>{{ item.nombre }}</td>
                <td>{{ item.tipo }}</td>
                <td>${{ item.precio.toLocaleString('es-CL') }}</td>
                <td>{{ item.stock }}</td>
                <td>
                  <v-icon
                    icon="mdi-pencil"
                    size="large"
                    class="me-2 text-primary"
                    @click="editProducto(item)"
                  ></v-icon>
                  <v-icon
                    icon="mdi-delete"
                    size="large"
                    color="error"
                    @click="deleteProducto(item)"
                  ></v-icon>
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card>
      </v-window-item>
      <v-window-item value="salas">
        <v-card elevation="4" rounded="lg" class="mt-4">
          <v-card-title
            class="d-flex align-center pe-2"
            :style="{ color: 'var(--v-theme-on-surface)' }"
          >
            <v-icon icon="mdi-door-sliding"></v-icon> &nbsp; Tipos de Sala
            <v-spacer></v-spacer>

            <v-dialog v-model="dialogSala" max-width="600px">
              <template v-slot:activator="{ props }">
                <v-btn color="secondary" variant="tonal" v-bind="props" prepend-icon="mdi-plus">
                  Nueva Sala
                </v-btn>
              </template>

              <v-card rounded="lg">
                <v-card-title>
                  <span class="text-h5">{{ formTitleSala }}</span>
                </v-card-title>
                <v-card-text>
                  <v-alert
                    v-if="saveSalaError"
                    type="error"
                    variant="tonal"
                    density="compact"
                    class="mb-4"
                  >
                    {{ saveSalaError }}
                  </v-alert>
                  <v-container>
                    <v-row>
                      <v-col cols="12" sm="6">
                        <v-text-field
                          v-model="editedSala.nombre"
                          label="Nombre Sala"
                          variant="outlined"
                          density="compact"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-text-field
                          v-model.number="editedSala.capacidad"
                          label="Capacidad"
                          type="number"
                          variant="outlined"
                          density="compact"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12">
                        <v-textarea
                          v-model="editedSala.descripcion"
                          label="Descripción"
                          rows="2"
                          variant="outlined"
                          density="compact"
                        ></v-textarea>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-text-field
                          v-model="editedSala.tematica"
                          label="Temática (ej. J-Pop)"
                          variant="outlined"
                          density="compact"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12" sm="6">
                        <v-text-field
                          v-model.number="editedSala.precioHora"
                          label="Precio / Hora"
                          type="number"
                          prefix="$"
                          variant="outlined"
                          density="compact"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12">
                        <v-text-field
                          v-model="editedSala.equipamiento"
                          label='Equipamiento (ej. Pantalla 65\", Micrófonos Pro)'
                          variant="outlined"
                          density="compact"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12">
                        <v-text-field
                          v-model="editedSala.imagenUrl"
                          label="URL Imagen (opcional)"
                          prepend-inner-icon="mdi-image"
                          variant="outlined"
                          density="compact"
                        ></v-text-field>
                      </v-col>
                    </v-row>
                  </v-container>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="grey" variant="text" @click="closeSala">Cancelar</v-btn>
                  <v-btn color="secondary" variant="tonal" @click="saveSala">Guardar Sala</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>

            <v-dialog v-model="dialogDeleteSala" max-width="500px">
              <v-card rounded="lg">
                <v-card-title class="text-h5 text-center"
                  >¿Estás seguro de borrarla?</v-card-title
                >
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="grey" variant="text" @click="closeDeleteSala">Cancelar</v-btn>
                  <v-btn color="error" variant="tonal" @click="deleteSalaConfirm">SÍ, Borrar</v-btn>
                  <v-spacer></v-spacer>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-card-title>

          <v-table density="compact" class="elevation-0">
            <thead>
              <tr>
                <th class="text-left font-weight-bold">ID</th>
                <th class="text-left font-weight-bold">Nombre</th>
                <th class="text-left font-weight-bold">Capacidad</th>
                <th class="text-left font-weight-bold">Precio/Hora</th>
                <th class="text-left font-weight-bold">Temática</th>
                <th class="text-left font-weight-bold">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loadingSalas">
                <td :colspan="6" class="text-center py-4">
                  <v-progress-circular indeterminate color="secondary"></v-progress-circular>
                  <div class="mt-2 text-caption">Cargando salas...</div>
                </td>
              </tr>
              <tr v-else-if="salas.length === 0">
                <td :colspan="6" class="text-center py-4 text-medium-emphasis">
                  No hay salas creadas. ¡Crea una!
                </td>
              </tr>
              <tr v-else v-for="item in salas" :key="item.idTipoSala">
                <td>{{ item.idTipoSala }}</td>
                <td>{{ item.nombre }}</td>
                <td>{{ item.capacidad }}</td>
                <td>${{ item.precioHora.toLocaleString('es-CL') }}</td>
                <td>{{ item.tematica }}</td>
                <td>
                  <v-icon
                    icon="mdi-pencil"
                    size="large"
                    class="me-2 text-secondary"
                    @click="editSala(item)"
                  ></v-icon>
                  <v-icon
                    icon="mdi-delete"
                    size="large"
                    color="error"
                    @click="deleteSala(item)"
                  ></v-icon>
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card>
      </v-window-item>
    </v-window>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '../api'

// Variable para controlar la pestaña
const tab = ref('consumibles')

// --- LÓGICA PARA CONSUMIBLES (La que ya tenías, renombrada) ---
const productos = ref([])
const loadingProductos = ref(true)
const dialogProducto = ref(false)
const dialogDeleteProducto = ref(false)
const editedProductoIndex = ref(-1)
const editedProducto = ref({
  idProducto: null,
  nombre: '',
  descripcion: '',
  precio: 0,
  stock: 0,
  tipo: 'comida',
  imagenUrl: '',
})
const defaultProducto = { ...editedProducto.value }
const formTitleProducto = computed(() =>
  editedProductoIndex.value === -1 ? 'Nuevo Consumible' : 'Editar Consumible',
)
const saveProductoError = ref(null)

// --- LÓGICA NUEVA PARA SALAS (Copiada y adaptada) ---
const salas = ref([])
const loadingSalas = ref(true)
const dialogSala = ref(false)
const dialogDeleteSala = ref(false)
const editedSalaIndex = ref(-1)
const editedSala = ref({
  idTipoSala: null,
  nombre: '',
  descripcion: '',
  tematica: '',
  equipamiento: '',
  capacidad: 0,
  precioHora: 0,
  imagenUrl: '',
})
const defaultSala = { ...editedSala.value }
const formTitleSala = computed(() =>
  editedSalaIndex.value === -1 ? 'Nueva Plantilla de Sala' : 'Editar Plantilla de Sala',
)
const saveSalaError = ref(null)

// --- Cargar Datos ---
onMounted(() => {
  fetchProductos()
  fetchSalas() // Carga ambas listas al montar
})

// --- FUNCIONES CRUD DE CONSUMIBLES ---

const fetchProductos = async () => {
  loadingProductos.value = true
  try {
    // Usamos el 'api.js' helper, ya incluye el token
    const response = await api.get('/productos')
    // Filtramos para NO incluir el tipo 'sala' en esta pestaña
    productos.value = response.data.filter((p) => p.tipo === 'comida' || p.tipo === 'bebida')
  } catch (error) {
    console.error('Error cargando productos:', error)
    alert('Error: No se pudo cargar los productos.')
  } finally {
    loadingProductos.value = false
  }
}

const closeProducto = () => {
  dialogProducto.value = false
  editedProducto.value = { ...defaultProducto }
  editedProductoIndex.value = -1
  saveProductoError.value = null
}

const closeDeleteProducto = () => {
  dialogDeleteProducto.value = false
  editedProducto.value = { ...defaultProducto }
  editedProductoIndex.value = -1
}

const editProducto = (item) => {
  editedProductoIndex.value = productos.value.indexOf(item)
  editedProducto.value = { ...item }
  dialogProducto.value = true
}

const deleteProducto = (item) => {
  editedProductoIndex.value = productos.value.indexOf(item)
  editedProducto.value = { ...item }
  dialogDeleteProducto.value = true
}

const saveProducto = async () => {
  loadingProductos.value = true
  saveProductoError.value = null
  try {
    if (editedProductoIndex.value > -1) {
      // --- ACTUALIZAR (PUT) ---
      const id = editedProducto.value.idProducto
      await api.put(`/productos/${id}`, editedProducto.value)
    } else {
      // --- CREAR (POST) ---
      await api.post('/productos', editedProducto.value)
    }
    // Recargamos los datos y cerramos
    fetchProductos()
    closeProducto()
  } catch (error) {
    console.error('Error guardando consumible:', error)
    if (error.response && error.response.data && error.response.data.message) {
      saveProductoError.value = error.response.data.message
    } else {
      saveProductoError.value = 'Error desconocido. Intenta de nuevo.'
    }
  } finally {
    loadingProductos.value = false
  }
}

const deleteProductoConfirm = async () => {
  loadingProductos.value = true
  try {
    // --- BORRAR (DELETE) ---
    const id = editedProducto.value.idProducto
    await api.delete(`/productos/${id}`)
    fetchProductos()
    closeDeleteProducto()
  } catch (error) {
    console.error('Error borrando consumible:', error)
    alert('Error al borrar. (Asegúrate que no esté en uso por una reserva)')
  } finally {
    loadingProductos.value = false
  }
}

// --- FUNCIONES CRUD DE SALAS ---

const fetchSalas = async () => {
  loadingSalas.value = true
  try {
    const response = await api.get('/productos/salas') // Endpoint de Salas
    salas.value = response.data
  } catch (error) {
    console.error('Error cargando salas:', error)
  } finally {
    loadingSalas.value = false
  }
}

const closeSala = () => {
  dialogSala.value = false
  editedSala.value = { ...defaultSala }
  editedSalaIndex.value = -1
  saveSalaError.value = null
}

const closeDeleteSala = () => {
  dialogDeleteSala.value = false
  editedSala.value = { ...defaultSala }
  editedSalaIndex.value = -1
}

const editSala = (item) => {
  editedSalaIndex.value = salas.value.indexOf(item)
  // OJO: idTipoSala es el ID de la plantilla de sala
  editedSala.value = { ...item }
  dialogSala.value = true
}

const deleteSala = (item) => {
  editedSalaIndex.value = salas.value.indexOf(item)
  editedSala.value = { ...item }
  dialogDeleteSala.value = true
}

const saveSala = async () => {
  loadingSalas.value = true
  saveSalaError.value = null
  try {
    if (editedSalaIndex.value > -1) {
      // --- ACTUALIZAR (PUT) ---
      const id = editedSala.value.idTipoSala
      await api.put(`/productos/salas/${id}`, editedSala.value)
    } else {
      // --- CREAR (POST) ---
      await api.post('/productos/salas', editedSala.value)
    }
    fetchSalas() // Recarga la lista de salas
    closeSala()
  } catch (error) {
    console.error('Error guardando sala:', error)
    if (error.response && error.response.data && error.response.data.message) {
      saveSalaError.value = error.response.data.message
    } else {
      saveSalaError.value = 'Error desconocido. Intenta de nuevo.'
    }
  } finally {
    loadingSalas.value = false
  }
}

const deleteSalaConfirm = async () => {
  loadingSalas.value = true
  try {
    // --- BORRAR (DELETE) ---
    const id = editedSala.value.idTipoSala
    await api.delete(`/productos/salas/${id}`)
    fetchSalas()
    closeDeleteSala()
  } catch (error) {
    console.error('Error borrando sala:', error)
    alert('Error al borrar. (Asegúrate que no haya salas físicas asociadas)')
  } finally {
    loadingSalas.value = false
  }
}
</script>
