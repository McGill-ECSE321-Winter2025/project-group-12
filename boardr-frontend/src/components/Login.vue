<template>
    <div class="flex justify-center items-center min-h-screen bg-gray-100">
      <Card class="w-full max-w-md p-6">
        <template #title>
          <h2 class="text-2xl font-bold text-center">Login to Boardr</h2>
        </template>
        <template #content>
          <form @submit.prevent="login" class="space-y-4">
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
              <InputText
                id="email"
                v-model="email"
                type="email"
                placeholder="Enter your email"
                class="w-full"
                required
              />
            </div>
            <div>
              <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
              <Password
                id="password"
                v-model="password"
                placeholder="Enter your password"
                class="w-full"
                toggleMask
                required
              />
            </div>
            <Button
              type="submit"
              label="Login"
              class="w-full bg-blue-600 hover:bg-blue-700"
              :loading="loading"
            />
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
          // Fetch user by email
          const response = await api.get(`/users/email/${this.email}`)
          const user = response.data
          if (user.password === this.password) {
            // Store user data (simplified; use a state management solution like Pinia in production)
            localStorage.setItem('user', JSON.stringify(user))
            this.$router.push('/account')
          } else {
            this.$toast.add({
              severity: 'error',
              summary: 'Login Failed',
              detail: 'Invalid credentials',
              life: 3000,
            })
          }
        } catch (error) {
          this.$toast.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Login failed. Please try again.',
            life: 3000,
          })
          console.error(error)
        } finally {
          this.loading = false
        }
      },
    },
  }
  </script>
  
  <style scoped>
  /* Tailwind handles most styling, but scoped CSS can be added here if needed */
  </style>