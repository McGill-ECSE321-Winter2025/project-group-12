<template>
    <nav class="bg-blue-600 text-white p-4 sticky top-0 z-10">
      <div class="container mx-auto flex justify-between items-center">
        <router-link to="/" class="text-xl font-bold">Boardr</router-link>
        <div class="space-x-6">
          <router-link
            to="/games"
            class="hover:text-blue-200 transition-colors"
          >
            Games
          </router-link>
          <router-link
            to="/events"
            class="hover:text-blue-200 transition-colors"
          >
            Events
          </router-link>
          <router-link
            v-if="isLoggedIn"
            to="/account"
            class="hover:text-blue-200 transition-colors"
          >
            Account
          </router-link>
          <router-link
            v-else
            to="/login"
            class="hover:text-blue-200 transition-colors"
          >
            Login
          </router-link>
          <Button
            v-if="isLoggedIn"
            label="Logout"
            class="bg-red-600 hover:bg-red-700"
            @click="logout"
          />
        </div>
      </div>
    </nav>
  </template>
  
  <script>
  import Button from 'primevue/button'
  
  export default {
    name: 'Navbar',
    components: { Button },
    computed: {
      isLoggedIn() {
        return !!localStorage.getItem('user')
      },
    },
    methods: {
      logout() {
        localStorage.removeItem('user')
        this.$router.push('/login')
        this.$toast.add({
          severity: 'info',
          summary: 'Logged Out',
          detail: 'You have been logged out.',
          life: 3000,
        })
      },
    },
  }
  </script>