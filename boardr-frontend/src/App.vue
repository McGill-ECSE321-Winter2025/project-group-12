<template>
  <div class="app-container flex flex-col min-h-screen" :class="themeClass">
    <!-- Global Loading Indicator -->
    <ProgressBar v-if="isLoading" mode="indeterminate" class="fixed top-0 left-0 w-full z-50" style="height: 4px" />

    <!-- Navbar -->
    <Navbar @toggle-theme="toggleTheme" class="flex-shrink-0" />

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
      isDarkMode: true,
      isLoading: false,
    }
  },
  computed: {
    themeClass() {
      return this.isDarkMode
        ? 'bg-gradient-to-b from-[#121212] to-[#1a1a1a] text-[#e0e0e0]'
        : 'bg-gradient-to-b from-[#f5f5f5] to-[#e0e0e0] text-[#1a1a1a]'
    },
  },
  methods: {
    toggleTheme() {
      this.isDarkMode = !this.isDarkMode
      localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light')
    },
    startLoading() {
      this.isLoading = true
    },
    stopLoading() {
      this.isLoading = false
    },
  },
  created() {
    const savedTheme = localStorage.getItem('theme')
    this.isDarkMode = savedTheme ? savedTheme === 'dark' : true
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