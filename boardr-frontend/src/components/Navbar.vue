<template>
  <nav class="bg-[#181818] text-[#e0e0e0] p-4 sticky top-0 z-10 shadow-md w-full">
    <div class="flex items-center justify-between w-full px-4">
      <!-- Left Section -->
      <div class="flex items-center space-x-6">
        <!-- Boardr Logo with Icon -->
        <router-link to="/" class="flex items-center space-x-2">
          <span class="text-2xl font-bold tracking-tight hover:text-[#1ed760] transition-colors duration-200">Boardr</span>
          <i class="pi pi-table text-[#1db954] text-xl" />
        </router-link>
        <!-- Menu Items (including Login when logged out) -->
        <MenuBarItem :items="menuItems" :is-logged-in="isLoggedIn" />
      </div>

      <!-- Right Section (Logged-in actions only) -->
      <div class="flex items-center space-x-6">
        <!-- Theme Toggle -->
        <Button
          :icon="isDarkMode ? 'pi pi-sun' : 'pi pi-moon'"
          class="p-button-text p-button-rounded text-[#e0e0e0] hover:text-[#1ed760] transition-colors duration-200"
          @click="$emit('toggle-theme')"
          title="Toggle Theme"
        />
        <!-- Logged In Actions -->
        <div v-if="isLoggedIn" class="flex items-center space-x-6">
          <Button
            icon="pi pi-sign-out"
            class="p-button-text p-button-rounded text-[#e0e0e0] hover:text-[#1ed760] transition-colors duration-200"
            @click="logout"
            title="Log Out"
          />
          <router-link
            to="/settings"
            class="text-xl hover:text-[#1ed760] transition-colors duration-200"
            title="Settings"
          >
            <i class="pi pi-cog" />
          </router-link>
          <router-link
            to="/account"
            class="flex items-center space-x-1 text-sm font-medium hover:text-[#1ed760] transition-colors duration-200"
          >
            <span>Hi, {{ username }}!</span>
          </router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import Button from 'primevue/button'
import MenuBarItem from './MenuBarItem.vue'

export default {
  name: 'Navbar',
  components: { Button, MenuBarItem },
  props: {
    isDarkMode: { type: Boolean, default: true },
  },
  computed: {
    isLoggedIn() {
      return !!localStorage.getItem('user')
    },
    username() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      return user.name || 'User'
    },
    menuItems() {
      return [
        { label: 'Home', icon: 'pi pi-home', command: () => this.$router.push('/') },
        {
          label: 'Events',
          icon: 'pi pi-calendar',
          command: () => this.$router.push('/events'),
          subItems: [
            { label: 'All Events', icon: 'pi pi-calendar', command: () => this.$router.push('/events') },
            {
              label: 'My Events',
              icon: 'pi pi-user',
              command: () => this.isLoggedIn ? this.$router.push('/account') : this.$router.push('/login'),
            },
          ],
        },
        { label: 'Games', icon: 'pi pi-desktop', command: () => this.$router.push('/games') },
      ]
    },
  },
  methods: {
    logout() {
      localStorage.removeItem('user')
      this.$router.push('/')
      this.$toast.add({
        severity: 'info',
        summary: 'Logged Out',
        detail: 'See you soon!',
        life: 3000,
      })
    },
  },
}
</script>

<style scoped>
.w-full {
  width: 100%;
}
.px-4 {
  padding-left: 1rem;
  padding-right: 1rem;
}
.p-button.p-button-text {
  padding: 0.5rem;
  font-size: 1.25rem;
}
.space-x-6 > * + * {
  margin-left: 1.5rem;
}
</style>