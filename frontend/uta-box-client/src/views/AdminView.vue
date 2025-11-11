<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <!-- Título con color Primary para el look Kawaii -->
        <h1 class="text-h4 mb-4" :style="{ color: 'var(--v-theme-primary)' }">Gestión de Productos</h1>
      </v-col>
    </v-row>

    <!-- TABLA DE DATOS (Usando v-table simple para evitar errores de importación) -->
    <v-card elevation="4" rounded="lg">
      <v-card-title class="d-flex align-center pe-2" :style="{ color: 'var(--v-theme-on-surface)' }">
        <v-icon icon="mdi-video-input-component"></v-icon> &nbsp;
        Productos del Catálogo

        <v-spacer></v-spacer>

        <!-- DIÁLOGO (MODAL) PARA CREAR/EDITAR -->
        <v-dialog v-model="dialog" max-width="500px">
          <template v-slot:activator="{ props }">
            <!-- Botón de "Nuevo Producto" con color primario -->
            <v-btn color="primary" variant="tonal" v-bind="props" prepend-icon="mdi-plus">
              Nuevo Producto
            </v-btn>
          </template>

          <!-- Contenido del Modal -->
          <v-card rounded="lg">
            <v-card-title>
              <span class="text-h5">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
              <v-container>
                <v-row>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model="editedItem.nombre"
                      label="Nombre"
                      variant="outlined"
                      density="compact"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-select
                      v-model="editedItem.tipo"
                      label="Tipo"
                      :items="['sala', 'comida', 'bebida']"
                      variant="outlined"
                      density="compact"
                    ></v-select>
                  </v-col>
                  <v-col cols="12">
                    <v-textarea
                      v-model="editedItem.descripcion"
                      label="Descripción"
                      rows="2"
                      variant="outlined"
                      density="compact"
                    ></v-textarea>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model.number="editedItem.precio"
                      label="Precio (ej. 50000)"
                      type="number"
                      prefix="$"
                      variant="outlined"
                      density="compact"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model.number="editedItem.stock"
                      label="Stock"
                      type="number"
                      variant="outlined"
                      density="compact"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>

            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="grey" variant="text" @click="close">Cancelar</v-btn>
              <!-- Botón Guardar usa Primary/tonal -->
              <v-btn color="primary" variant="tonal" @click="save">Guardar</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog> <!-- Fin del Diálogo -->

        <!-- Diálogo de Borrar -->
        <v-dialog v-model="dialogDelete" max-width="500px">
          <v-card rounded="lg">
            <v-card-title class="text-h5">¿Estás seguro que quieres borrar este item?</v-card-title>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="grey" variant="text" @click="closeDelete">Cancelar</v-btn>
              <v-btn color="error" variant="tonal" @click="deleteItemConfirm">SÍ, Borrar</v-btn>
              <v-spacer></v-spacer>
            </v-card-actions>
          </v-card>
        </v-dialog>

      </v-card-title>
      
      <!-- Tabla con diseño de Vuetify simple -->
      <v-table density="compact" class="elevation-0">
        <!-- Cabecera de la tabla -->
        <thead>
          <tr>
            <th class="text-left font-weight-bold" v-for="header in headers" :key="header.key">
              {{ header.title }}
            </th>
          </tr>
        </thead>
        
        <!-- Cuerpo de la tabla con los datos -->
        <tbody>
          <tr v-if="loading">
            <td :colspan="headers.length" class="text-center py-4">
              <v-progress-circular indeterminate color="primary"></v-progress-circular>
              <div class="mt-2 text-caption">Cargando productos...</div>
            </td>
          </tr>
          
          <tr v-else-if="productos.length === 0">
            <td :colspan="headers.length" class="text-center py-4 text-medium-emphasis">
              No hay productos en el catálogo. ¡Crea uno!
            </td>
          </tr>

          <tr v-else v-for="item in productos" :key="item.idProducto">
            <td>{{ item.idProducto }}</td>
            <td>{{ item.nombre }}</td>
            <td>{{ item.tipo }}</td>
            <td>${{ item.precio.toLocaleString('es-CL') }}</td>
            <td>{{ item.stock }}</td>
            <td>
              <!-- Botones de Acción -->
              <v-icon
                icon="mdi-pencil"
                size="large"
                class="me-2 text-primary"
                @click="editItem(item)"
              ></v-icon>
              <v-icon
                icon="mdi-delete"
                size="large"
                color="error"
                @click="deleteItem(item)"
              ></v-icon>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '../api' // ¡Usamos el helper de API!

// --- Estado de la Tabla ---
const productos = ref([])
const loading = ref(true)

// NOTA: Los headers aquí se usan solo como referencia para la tabla HTML simple
const headers = ref([
  { title: 'ID', key: 'idProducto', sortable: true },
  { title: 'Nombre', key: 'nombre', sortable: true },
  { title: 'Tipo', key: 'tipo', sortable: true },
  { title: 'Precio', key: 'precio', sortable: true },
  { title: 'Stock', key: 'stock', sortable: true },
  { title: 'Acciones', key: 'actions', sortable: false },
])

// --- Estado del Modal (Diálogo) ---
const dialog = ref(false)
const dialogDelete = ref(false)
const editedIndex = ref(-1) // -1 = Nuevo, >-1 = Editando
const editedItem = ref({
  idProducto: null,
  nombre: '',
  descripcion: '',
  precio: 0,
  stock: 0,
  tipo: 'sala',
})
const defaultItem = { ...editedItem.value }
const formTitle = computed(() => (editedIndex.value === -1 ? 'Nuevo Producto' : 'Editar Producto'))

// --- Cargar Datos ---
onMounted(() => {
  fetchProductos()
})

const fetchProductos = async () => {
  loading.value = true
  try {
    // Usamos el 'api.js' helper, ya incluye el token
    const response = await api.get('/productos')
    productos.value = response.data
  } catch (error) {
    console.error("Error cargando productos:", error)
    // El alert es un placeholder para errores de red/seguridad
    alert("Error: No se pudo cargar los productos. ¿Estás seguro que el backend está corriendo y estás logueado como admin?")
  } finally {
    loading.value = false
  }
}

// --- Lógica del Modal y CRUD (sin cambios) ---
const close = () => {
  dialog.value = false
  editedItem.value = { ...defaultItem }
  editedIndex.value = -1
}

const closeDelete = () => {
  dialogDelete.value = false
  editedItem.value = { ...defaultItem }
  editedIndex.value = -1
}

const editItem = (item) => {
  editedIndex.value = productos.value.indexOf(item)
  editedItem.value = { ...item }
  dialog.value = true
}

const deleteItem = (item) => {
  editedIndex.value = productos.value.indexOf(item)
  editedItem.value = { ...item }
  dialogDelete.value = true
}

const save = async () => {
  loading.value = true
  try {
    if (editedIndex.value > -1) {
      // --- ACTUALIZAR (PUT) ---
      const id = editedItem.value.idProducto
      await api.put(`/productos/${id}`, editedItem.value)
    } else {
      // --- CREAR (POST) ---
      await api.post('/productos', editedItem.value)
    }
    // Recargamos los datos y cerramos
    fetchProductos()
    close()
  } catch (error) {
    console.error("Error guardando:", error)
    alert("Error al guardar el producto.")
  } finally {
    loading.value = false
  }
}

const deleteItemConfirm = async () => {
  loading.value = true
  try {
    // --- BORRAR (DELETE) ---
    const id = editedItem.value.idProducto
    await api.delete(`/productos/${id}`)
    fetchProductos()
    closeDelete()
  } catch (error) {
    console.error("Error borrando:", error)
    alert("Error al borrar. (Asegúrate que no esté en uso por una reserva)")
  } finally {
    loading.value = false
  }
}
</script>