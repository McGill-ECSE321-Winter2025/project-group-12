<template>
  <div class="flex justify-center items-center min-h-screen bg-gray-100 dark:bg-[#121212]">
    <Card class="w-full max-w-md p-6 bg-[#181818] text-[#e0e0e0]">
      <template #title>
        <h2 class="text-2xl font-bold text-center">Log in to your account</h2>
      </template>
      <template #subtitle>
        <p class="text-center text-sm text-[#e0e0e0] mb-6">Welcome back! Please enter your details</p>
      </template>
      <template #content>
        <form @submit.prevent="login" class="space-y-6">
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
              placeholder="Enter your password"
              class="w-full mt-1"
              toggleMask
              required
            />
          </div>
          <Button
            type="submit"
            label="Login"
            class="w-full bg-[#1db954] hover:bg-[#1ed760] text-white"
            :loading="loading"
          />
          <p class="text-center text-sm">
            Need an account?
            <router-link to="/register" class="text-[#1da1f2] hover:text-[#0d8aec] font-medium">
              Register
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
  name: 'Login',
  components: { Card, InputText, Password, Button },
  data() {
    return {
      email: '',
      password: '',
      loading: false,
    }
  },
  methods: {
    async login() {
      this.loading = true
      try {
        // Validate inputs
        if (!this.email || !this.password) {
          this.$toast.add({
            severity: 'warn',
            summary: 'Validation Error',
            detail: 'Email and password are required.',
            life: 3000,
          })
          return
        }

        // Fetch user by email via API
        const response = await api.get(`/users/email/${this.email}`)
        const user = response.data

        // Check password match
        if (user.password === this.password) {
          localStorage.setItem('user', JSON.stringify(user))
          this.$toast.add({
            severity: 'success',
            summary: 'Login Successful',
            detail: `Welcome back, ${user.name}!`,
            life: 3000,
          })
          this.$router.push('/account')
        } else {
          this.$toast.add({
            severity: 'error',
            summary: 'Login Failed',
            detail: 'Invalid email or password.',
            life: 3000,
          })
        }
      } catch (error) {
        // Enhanced error handling
        let errorMessage = 'Login failed. Please try again.'
        if (error.response) {
          // Server responded with a status other than 2xx
          errorMessage = error.response.data?.message || `Server error: ${error.response.status}`
          if (error.response.status === 404) {
            errorMessage = 'Email not found.'
          }
        } else if (error.request) {
          // Network error (no response)
          errorMessage = 'Network error: Could not connect to the server.'
          console.error('Request details:', error.request)
        }
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: errorMessage,
          life: 3000,
        })
        console.error('Login error:', error)
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