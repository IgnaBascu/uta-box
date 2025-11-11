// Importaciones de Vue
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Importaciones de Vuetify (La "Plantilla")
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css' // Importar iconos

// Configura Vuetify
const vuetify = createVuetify({
  theme: {
    defaultTheme: 'dark', // Tema oscuro para karaoke!
  },
  components,
  directives,
  icons: {
    defaultSet: 'mdi', // Habilita los iconos de Material Design
  },
})

// Crea la App
const app = createApp(App)

app.use(router)   // Usa el Router
app.use(vuetify)  // Usa la plantilla Vuetify

app.mount('#app')