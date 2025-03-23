<template>
  <nav class="bg-[#181818] text-[#e0e0e0] p-4 sticky top-0 z-10 shadow-md w-full">
    <div class="flex items-center w-full px-4">
      <!-- All Menu Items -->
      <MenuBarItem :items="menuItems" :is-logged-in="isLoggedIn" />
    </div>
  </nav>
</template>

<script>
import MenuBarItem from './MenuBarItem.vue'

export default {
  name: 'Navbar',
  components: { MenuBarItem },
  data() {
    return {
      isDarkMode: false, // Theme state
      user: null, // Reactive user state
    }
  },
  computed: {
    isLoggedIn() {
      return !!this.user // Use reactive user instead of direct localStorage check
    },
    username() {
      return this.user?.name || 'User'
    },
    menuItems() {
      const baseItems = [
      {
          label: 'Boardr',
          icon: 'boardr-logo', // Custom class for the JPG
          command: () => this.$router.push('/'),
        },
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
        {
          label: 'Toggle Theme',
          icon: this.isDarkMode ? 'pi pi-sun' : 'pi pi-moon',
          command: () => this.toggleDarkMode(),
        },
      ]

      if (this.isLoggedIn) {
        baseItems.push(
          {
            label: `Hi, ${this.username}!`,
            icon: 'pi pi-user',
            command: () => this.$router.push('/account'),
          },
          {
            label: 'Settings',
            icon: 'pi pi-cog',
            command: () => this.$router.push('/settings'),
          },
          {
            label: 'Logout',
            icon: 'pi pi-sign-out',
            command: () => this.logout(),
          }
        )
      }

      return baseItems
    },
  },
  methods: {
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode
      document.documentElement.classList.toggle('my-app-dark')
      localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light')
    },
    logout() {
      localStorage.removeItem('user')
      this.user = null // Clear reactive user state
      this.$router.push('/')
      this.$toast.add({
        severity: 'info',
        summary: 'Logged Out',
        detail: 'See you soon!',
        life: 3000,
      })
    },
    updateUserState() {
      const userData = localStorage.getItem('user')
      this.user = userData ? JSON.parse(userData) : null
    },
  },
  created() {
    // Initial state setup
    this.updateUserState()
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme) {
      this.isDarkMode = savedTheme === 'dark'
      if (this.isDarkMode) {
        document.documentElement.classList.add('my-app-dark')
      }
    } else {
      this.isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches
      if (this.isDarkMode) {
        document.documentElement.classList.add('my-app-dark')
      }
    }

    // Watch for route changes to refresh user state
    this.$router.afterEach(() => {
      this.updateUserState()
    })
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
</style>