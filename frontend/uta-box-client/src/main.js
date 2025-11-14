import { createApp } from 'vue'
import App from './App.vue'
import router from './router'


// Importaciones de Vuetify (La "Plantilla")
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css' // Importar iconos

// Configurar colores pasteles kawaii :3

const kawaiiLightTheme = {
  dark: false,
  colors: {

    // Fondo y superficies
    background: '#FDF9FF', // Blanco-lavanda suave
    surface: '#FFFFFF', // Fondo de las tarjetas blanco

    // Texto
    'on-background': '#3A3A48', // Gris oscuro con matiz azul
    'on-surface': '#3A3A48', 
    'on-primary': '#FFFFFF', // Texto blanco sobre el primary 
    'on-secondary': '#1A1A22',
    'on-tertiary': '#1A1A22',
    'on-accent': '#1A1A22',

    // Colores principales kawaii
    primary: '#FF86CA',   // Rosado neon pastel (Kawaii)
    secondary: '#9DE3FF', // Celeste digital brillante estilo J-pop
    tertiary: '#C8A7FF',  // Lavanda anime   

    // Acentos */
    accent: '#FFE679', // Amarillo pastel vibrante
    
    // Estados del sistema 
    success: '#4A9F71',   // Verde jade oscuro
    error:   '#FF6E8A',   // Rosa rojo brillante
    warning: '#FFD36D',   // Amarillo suave
    info:    '#9DE3FF',   // Azul celeste kawaii
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
  // Usamos el spread operator (...) para asegurarnos de que todos los componentes se registren
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