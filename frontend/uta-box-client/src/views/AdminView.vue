<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4">Gestión de Productos</h1>
      </v-col>
    </v-row>

    <!-- TABLA DE DATOS -->
    <v-card elevation="4">
      <v-card-title class="d-flex align-center pe-2">
        <v-icon icon="mdi-video-input-component"></v-icon> &nbsp;
        Productos del Catálogo

        <v-spacer></v-spacer>

        <!-- DIÁLOGO (MODAL) PARA CREAR/EDITAR -->
        <v-dialog v-model="dialog" max-width="500px">
          <template v-slot:activator="{ props }">a
            <!-- Botón de "Nuevo Producto" -->
            <v-btn color="primary" v-bind="props" prepend-icon="mdi-plus">
              Nuevo Producto
            </v-btn>
          </template>

          <!-- Contenido del Modal -->
          <v-card>
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
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-select
                      v-model="editedItem.tipo"
                      label="Tipo"
                      :items="['sala', 'comida', 'bebida']"
                    ></v-select>
                  </v-col>
                  <v-col cols="12">
                    <v-textarea
                      v-model="editedItem.descripcion"
                      label="Descripción"
                      rows="2"
                    ></v-textarea>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model.number="editedItem.precio"
                      label="Precio (ej. 50000)"
                      type="number"
                      prefix="$"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model.number="editedItem.stock"
                      label="Stock"
                      type="number"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>

            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="grey" variant="text" @click="close">Cancelar</v-btn>
              <v-btn color="primary" variant="tonal" @click="save">Guardar</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog> <!-- Fin del Diálogo -->

        <!-- Diálogo de Borrar -->
        <v-dialog v-model="dialogDelete" max-width="500px">
          <v-card>
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
      
      <!-- La Tabla que muestra los productos -->
      <v-data-table
        :headers="headers"
        :items="productos"
        :loading="loading"
        density="compact"
      >
        <template v-slot:item.precio="{ value }">
          ${{ value.toLocaleString('es-CL') }}
        </template>
        <template v-slot:item.actions="{ item }">
          <v-icon
            icon="mdi-pencil"
            size="small"
            class="me-2"
            @click="editItem(item)"
          ></v-icon>
          <v-icon
            icon="mdi-delete"
            size="small"
            color="error"
            @click="deleteItem(item)"
          ></v-icon>
        </template>
      </v-data-table>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '../api' // ¡Usamos el helper de API!

// --- Estado de la Tabla ---
const productos = ref([])
const loading = ref(true)
const headers = ref([
  { title: 'ID', key: 'idProducto' },
  { title: 'Nombre', key: 'nombre' },
  { title: 'Tipo', key: 'tipo' },
  { title: 'Precio', key: 'precio' },
  { title: 'Stock', key: 'stock' },
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
    alert("Error: No se pudo cargar los productos. ¿Estás seguro que el backend está corriendo y estás logueado como admin?")
  } finally {
    loading.value = false
  }
}

// --- Lógica del Modal ---
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

// --- Lógica de API (CRUD) ---

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