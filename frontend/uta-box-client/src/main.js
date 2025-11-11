import { createApp } from 'vue'
import App from './App.vue'
import router from './router'


// Importaciones de Vuetify (La "Plantilla")
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css' // Importar iconos

// IMPORTACIÓN ADICIONAL PARA COMPONENTES MÁS AVANZADOS (COMO v-data-table)
// Asegura que todos los componentes sean reconocidos.

// Configurar colores pasteles kawaii :3

const kawaiiLightTheme = {
  dark: false,
  colors: {
    background: '#FFFBF5', // Fondo crema suave
    surface: '#FFFFFF', // Fondo de las tarjetas blanco
    
    // Colores más saturados (del paso anterior)
    primary: '#F9A8D4', // Rosa pastel
    secondary: '#A7F3D0', // Verde menta
    info: '#BAE6FD', // Azul cielo vibrante
    // ---------------------------
    
    error: '#E11D48',
    success: '#16A34A',
    warning: '#F59E0B',
    // Texto
    'on-background': '#4B5563', // Texto sobre el fondo
    'on-surface': '#333333', // Texto sobre las tarjetas
    'on-primary': '#4B5563', // Texto oscuro sobre el primary (ajustado para legibilidad en el frontend)
    'on-secondary': '#4B5563', // Texto oscuro sobre el secondary
    'on-info': '#4B5563', // Texto oscuro sobre el info
  },
}

// Configura Vuetify
const vuetify = createVuetify({
  theme: {
    defaultTheme: 'kawaiiLightTheme',
    themes: {
      kawaiiLightTheme,
    },
  },
  // Usamos el spread operator para asegurarnos de que todos los componentes se registren
  components: {
    ...components,
  },
  directives,
  icons: {
    defaultSet: 'mdi', // Habilita los iconos de Material Design
  },
})

// Crea la App
const app = createApp(App)

app.use(router) // Usa el Router
app.use(vuetify) // Usa la plantilla Vuetify

app.mount('#app')