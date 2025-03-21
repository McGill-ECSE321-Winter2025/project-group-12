<template>
  <div class="min-h-screen bg-gray-100">
    <Navbar />
    <Toast />
    <router-view class="container mx-auto p-4" />
  </div>
</template>

<script>
import Navbar from './components/Navbar.vue'
import Toast from 'primevue/toast'

export default {
  components: { Navbar, Toast },
}
</script>