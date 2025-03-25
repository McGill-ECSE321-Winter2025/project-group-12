<template>
  <div class="app-container flex flex-col min-h-screen">
    <!-- Global Loading Indicator -->
    <!-- TO SEE IF WE WANT TO USE IT?--<ProgressBar v-if="isLoading" mode="indeterminate" class="fixed top-0 left-0 w-full z-50" style="height: 4px" /> >

    <!-- Navbar -->
    <Navbar class="flex-shrink-0" />

    <!-- Main Content -->
    <div class="main-content flex-grow w-full overflow-hidden">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>

    <!-- Footer -->
    <footer class="bg-[#181818] text-[#e0e0e0] p-4 text-center text-sm flex-shrink-0 w-full">
      <p>© 2025 Boardr. Built with <i class="pi pi-heart text-[#1db954]"></i> by Group 12.</p>
    </footer>

    <!-- Global Toast -->
    <Toast />
  </div>
</template>

<script>
import Navbar from './components/Navbar.vue'
import Toast from 'primevue/toast'
import ProgressBar from 'primevue/progressbar'

export default {
  components: { Navbar, Toast, ProgressBar },
  data() {
    return {
      isLoading: false,
    }
  },
  methods: {
    startLoading() {
      this.isLoading = true
    },
    stopLoading() {
      this.isLoading = false
    },
  },
  created() {
    this.$router.beforeEach((to, from, next) => {
      this.startLoading()
      next()
    })
    this.$router.afterEach(() => {
      setTimeout(() => this.stopLoading(), 500)
    })
  },
}
</script>

<style scoped>
.app-container {
  width: 100vw;
  max-width: 100%;
  overflow-x: hidden;
}

.main-content {
  width: 100%;
  padding: 1rem;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>