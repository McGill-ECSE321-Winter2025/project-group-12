<template>
  <div class="flex justify-center items-center min-h-screen bg-gray-100 dark:bg-[#121212]">
    <Card class="w-full max-w-md p-6 bg-[#181818] text-[#e0e0e0]">
      <template #title>
        <h2 class="text-2xl font-bold text-center">Create an account</h2>
      </template>
      <template #content>
        <form @submit.prevent="register" class="space-y-6">
          <div>
            <label for="name" class="block text-sm font-medium text-[#e0e0e0]">Name *</label>
            <InputText
              id="name"
              v-model="name"
              placeholder="Enter your name"
              class="w-full mt-1 text-[#1a1a1a] dark:text-[#e0e0e0]"
              required
            />
          </div>
          <div>
            <label for="email" class="block text-sm font-medium text-[#e0e0e0]">Email *</label>
            <InputText
              id="email"
              v-model="email"
              type="email"
              placeholder="Enter your email"
              class="w-full mt-1 text-[#1a1a1a] dark:text-[#e0e0e0]"
              required
            />
          </div>
          <div>
            <label for="password" class="block text-sm font-medium text-[#e0e0e0]">Password *</label>
            <Password
              id="password"
              v-model="password"
              placeholder="Create a password"
              class="w-full mt-1"
              toggleMask
              required
            />
          </div>
          <Button
            type="submit"
            label="Continue"
            class="w-full bg-[#1db954] hover:bg-[#1ed760] text-white"
            :loading="loading"
          />
          <p class="text-center text-sm">
            Already have an account?
            <router-link to="/login" class="text-[#1da1f2] hover:text-[#0d8aec] font-medium">
              Login
            </router-link>
          </p>
        </form>
      </template>
    </Card>
  </div>
</template>

<script>
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Button from 'primevue/button'
import api from '../services/api'

export default {
  name: 'Register',
  components: { Card, InputText, Password, Button },
  data() {
    return {
      name: '',
      email: '',
      password: '',
      loading: false,
    }
  },
  methods: {
    async register() {
      this.loading = true
      try {
        // Validate inputs
        if (!this.name || !this.email || !this.password) {
          this.$toast.add({
            severity: 'warn',
            summary: 'Validation Error',
            detail: 'All fields are required.',
            life: 3000,
          })
          return
        }

        // Register user via API
        const userData = {
          name: this.name,
          email: this.email,
          password: this.password,
        }
        const response = await api.post('/users', userData)
        const user = response.data

        // Auto-login after registration
        localStorage.setItem('user', JSON.stringify(user))
        this.$toast.add({
          severity: 'success',
          summary: 'Registration Successful',
          detail: 'Welcome to Boardr!',
          life: 3000,
        })
        this.$router.push('/account')
      } catch (error) {
        // Enhanced error handling
        let errorMessage = 'Registration failed. Please try again.'
        if (error.response) {
          // Server responded with a status other than 2xx
          errorMessage = error.response.data?.message || `Server error: ${error.response.status}`
        } else if (error.request) {
          // Request was made but no response received (network error)
          errorMessage = error
        }
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: errorMessage,
          life: 3000,
        })
        console.error('Registration error:', error)
      } finally {
        this.loading = false
      }
    },
  },
}
</script>

<style scoped>
/* Tailwind and PrimeVue handle styling */
</style>