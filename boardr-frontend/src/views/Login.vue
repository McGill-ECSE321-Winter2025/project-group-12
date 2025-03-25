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
        <form @submit.prevent="handleLogin" class="space-y-6">
          <div>
            <label for="email" class="block text-sm font-medium text-[#e0e0e0]">Email *</label>
            <div></div>
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
            <div></div>
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
import { loginUser } from '../services/loginService'

export default {
  name: 'LoginView',
  components: { Card, InputText, Password, Button },
  data() {
    return {
      email: '',
      password: '',
      loading: false,
    }
  },
  methods: {
    async handleLogin() {
      this.loading = true
      try {
        const user = await loginUser(this.email, this.password)
        this.$toast.add({
          severity: 'success',
          summary: 'Login Successful',
          detail: `Welcome back, ${user.name}!`,
          life: 3000,
        })
        this.$router.push('/account')
      } catch (error) {
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: error.message,
          life: 3000,
        })
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