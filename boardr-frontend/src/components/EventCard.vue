<template>
    <Card class="w-72 shadow-md hover:shadow-lg transition-shadow">
      <template #title>
        <h3 class="text-lg font-semibold">{{ event.description }}</h3>
      </template>
      <template #subtitle>
        <p class="text-gray-600">{{ formatDate(event.eventDate) }} at {{ formatTime(event.eventTime) }}</p>
      </template>
      <template #content>
        <p><strong>Location:</strong> {{ event.location }}</p>
        <p><strong>Game:</strong> {{ gameName }}</p>
        <p><strong>Slots:</strong> {{ event.maxParticipants - registeredCount }} / {{ event.maxParticipants }}</p>
      </template>
      <template #footer>
        <Button
          v-if="slotsAvailable && !isOrganizer"
          label="Register"
          icon="pi pi-plus"
          class="bg-blue-600 hover:bg-blue-700"
          @click="register"
        />
        <span v-else-if="!slotsAvailable" class="text-gray-500">Fully Booked</span>
        <span v-else class="text-gray-500">Your Event</span>
      </template>
    </Card>
  </template>
  
  <script>
  import Card from 'primevue/card'
  import Button from 'primevue/button'
  import api from '../services/api'
  
  export default {
    name: 'EventCard',
    components: { Card, Button },
    props: {
      event: {
        type: Object,
        required: true,
      },
    },
    data() {
      return {
        registeredCount: 0, // Placeholder; fetch actual count in production
      }
    },
    computed: {
      gameName() {
        // Placeholder; fetch game name via API or pass as prop
        return `Game ${this.event.boardGameInstanceId}`
      },
      slotsAvailable() {
        return this.event.maxParticipants > this.registeredCount
      },
      isOrganizer() {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        return user.userAccountId === this.event.organizerId
      },
    },
    methods: {
      formatDate(date) {
        const str = date.toString()
        return `${str.slice(0, 4)}-${str.slice(4, 6)}-${str.slice(6, 8)}`
      },
      formatTime(time) {
        const str = time.toString().padStart(4, '0')
        return `${str.slice(0, 2)}:${str.slice(2)}`
      },
      async register() {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user.userAccountId) {
          this.$toast.add({
            severity: 'warn',
            summary: 'Login Required',
            detail: 'Please log in to register.',
            life: 3000,
          })
          this.$router.push('/login')
          return
        }
        try {
          const registration = {
            userId: user.userAccountId,
            eventId: this.event.eventId,
          }
          await api.post('/registrations', registration)
          this.$toast.add({
            severity: 'success',
            summary: 'Registered',
            detail: 'You’ve successfully registered for the event!',
            life: 3000,
          })
          this.registeredCount++ // Update locally; fetch real count in production
        } catch (error) {
          this.$toast.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to register for the event.',
            life: 3000,
          })
          console.error(error)
        }
      },
    },
  }
  </script>