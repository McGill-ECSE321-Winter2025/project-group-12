<template>
    <Card class="w-72 shadow-md hover:shadow-lg transition-shadow">
      <template #title>
        <h3 class="text-lg font-semibold">{{ game.boardGameName }}</h3>
      </template>
      <template #subtitle>
        <p class="text-gray-600">Owned by: {{ gameOwnerName }}</p>
      </template>
      <template #content>
        <p><strong>Condition:</strong> {{ game.condition }}</p>
        <p><strong>Availability:</strong> {{ game.available ? 'Yes' : 'No' }}</p>
      </template>
      <template #footer>
        <Button
          v-if="game.available && !isOwner"
          label="Request to Borrow"
          icon="pi pi-arrow-right"
          class="bg-green-600 hover:bg-green-700"
          @click="requestBorrow"
        />
        <span v-else-if="!game.available" class="text-gray-500">Not Available</span>
        <span v-else class="text-gray-500">Your Game</span>
      </template>
    </Card>
  </template>
  
  <script>
  import Card from 'primevue/card'
  import Button from 'primevue/button'
  import api from '../services/api'
  
  export default {
    name: 'GameCard',
    components: { Card, Button },
    props: {
      game: {
        type: Object,
        required: true,
      },
    },
    computed: {
      gameOwnerName() {
        // Placeholder; fetch owner name via API or pass it as a prop in production
        return `User ${this.game.gameOwnerId}`
      },
      isOwner() {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        return user.gameOwnerRoleId === this.game.gameOwnerId
      },
    },
    methods: {
      async requestBorrow() {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user.userAccountId) {
          this.$toast.add({
            severity: 'warn',
            summary: 'Login Required',
            detail: 'Please log in to borrow a game.',
            life: 3000,
          })
          this.$router.push('/login')
          return
        }
        try {
          const borrowRequest = {
            userAccountId: user.userAccountId,
            boardGameInstanceId: this.game.individualGameId,
            requestDate: new Date().toISOString().split('T')[0],
            returnDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0], // 1 week later
          }
          await api.post('/borrowRequests', borrowRequest)
          this.$toast.add({
            severity: 'success',
            summary: 'Request Sent',
            detail: 'Borrow request submitted successfully!',
            life: 3000,
          })
        } catch (error) {
          this.$toast.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to send borrow request.',
            life: 3000,
          })
          console.error(error)
        }
      },
    },
  }
  </script>