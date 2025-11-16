<template>
  <v-container class="my-8">
    <v-row justify="center">
      <v-col cols="12" md="10" lg="8">
        
        <v-card elevation="4" rounded="xl" class="mb-8"> <v-row no-gutters>
            <v-col
              cols="12"
              md="4"
              class="d-flex align-center justify-center pa-6"
              :style="{ backgroundColor: 'var(--v-theme-secondary)' }"
            >
              <v-img :src="logo" max-width="200" contain alt="Logo Uta-Box Kawaii"></v-img>
            </v-col>

            <v-col cols="12" md="8">
              <v-card-text class="pa-6">
                <h1 class="text-h4 mb-4" :style="{ color: 'var(--v-theme-primary)' }">
                  Sobre Uta-Box
                </h1>
                <p class="text-h6 font-weight-bold mb-4 text-justify" :style="{ color: '#3BB5F1' }">
                  ¡Tu escenario privado para brillar! ✨
                </p>
                <p class="text-body-1 mb-3 text-justify">
                  En <strong>Uta-Box Karaoke</strong>, creemos que todos tienen una estrella de pop
                  en su interior esperando a ser descubierta. 🎤
                </p>
                <p class="text-body-1 mb-3 text-justify">
                  Nacimos de la pasión por la cultura pop asiática y la alegría de compartir
                  momentos inolvidables. Nuestra misión es ofrecerte un espacio privado, cómodo y
                  con la mejor tecnología para vivir la misma experiencia de los karaokes asiaticos
                  cerca de tí.
                </p>
                <p class="text-body-1 mb-3 text-justify">
                  Desde los últimos éxitos del J-Pop y K-Pop hasta los clásicos del rock y pop que
                  todos amamos, nuestro catálogo está listo para ti. ¡Acompañado de los snacks y
                  bebidas más deliciosos para que la fiesta nunca termine!
                </p>
                <p
                  class="text-body-1 font-weight-bold mt-4 text-justify"
                  :style="{ color: '#868EFF' }"
                >
                  ¡Prepara tus cuerdas vocales y ven a vivir la experiencia Uta-Box!
                </p>
              </v-card-text>
              </v-col>
          </v-row>
        </v-card>
        <v-card elevation="4" rounded="xl">
          <v-row no-gutters>
            <v-col cols="12" md="7">
              <v-responsive :aspect-ratio="16 / 9" height="100%" min-height="300">
                <iframe
                  src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3329.131101887304!2d-70.64826808479998!3d-33.44507028077536!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x9662c5a0f0a0a0a9%3A0x6d0a0a0a0a0a0a0a!2sPlaza%20de%20Armas%20de%20Santiago!5e0!3m2!1ses!2scl!4v1678888888888!5m2!1ses!2scl"
                  style="border: 0; width: 100%; height: 100%"
                  allowfullscreen=""
                  loading="lazy"
                  referrerpolicy="no-referrer-when-downgrade"
                >
                </iframe>
              </v-responsive>
            </v-col>

            <v-col cols="12" md="5">
              <v-card-text class="pa-6">
                <h2 class="text-h5 mb-4 font-weight-bold text-center" :style="{ color: '#3BB5F1' }">
                  Visítanos
                </h2>
                <v-list density="compact" bg-color="transparent">
                  <v-list-item prepend-icon="mdi-clock-time-eight-outline">
                    <v-list-item-title>Lunes a Domingo</v-list-item-title>
                    <v-list-item-subtitle>11:00 - 20:00</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item prepend-icon="mdi-map-marker-outline">
                    <v-list-item-title>Av. Ficticia 123</v-list-item-title>
                    <v-list-item-subtitle>Santiago, Chile</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item prepend-icon="mdi-phone-outline">
                    <v-list-item-title>(+56) 9 1234 5678</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-col>
          </v-row>
        </v-card>
        </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import logo from '@/assets/logo_uta_box.png'
</script>

<style scoped>
/* Puedes añadir estilos específicos si lo necesitas */
</style>