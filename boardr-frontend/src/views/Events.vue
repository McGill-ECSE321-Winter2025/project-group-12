<template>
    <div class="py-6">
      <h1 class="text-3xl font-bold mb-6">Events</h1>
      <Button
        label="Create Event"
        icon="pi pi-plus"
        class="mb-6 bg-blue-600 hover:bg-blue-700"
        @click="showCreateEventDialog = true"
      />
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
        <EventCard v-for="event in events" :key="event.eventId" :event="event" />
      </div>
  
      <!-- Create Event Dialog -->
      <Dialog v-model:visible="showCreateEventDialog" header="Create Event" :style="{ width: '30rem' }">
        <div class="space-y-4">
          <div>
            <label for="description" class="block text-sm font-medium">Description</label>
            <InputText id="description" v-model="newEvent.description" class="w-full" />
          </div>
          <div>
            <label for="date" class="block text-sm font-medium">Date (YYYYMMDD)</label>
            <InputText id="date" v-model="newEvent.eventDate" class="w-full" />
          </div>
          <div>
            <label for="time" class="block text-sm font-medium">Time (HHMM)</label>
            <InputText id="time" v-model="newEvent.eventTime" class="w-full" />
          </div>
          <div>
            <label for="location" class="block text-sm font-medium">Location</label>
            <InputText id="location" v-model="newEvent.location" class="w-full" />
          </div>
          <div>
            <label for="maxParticipants" class="block text-sm font-medium">Max Participants</label>
            <InputText id="maxParticipants" v-model="newEvent.maxParticipants" type="number" class="w-full" />
          </div>
          <div>
            <label for="gameId" class="block text-sm font-medium">Game Instance ID</label>
            <InputText id="gameId" v-model="newEvent.boardGameInstanceId" type="number" class="w-full" />
          </div>
        </div>
        <template #footer>
          <Button label="Cancel" class="p-button-text" @click="showCreateEventDialog = false" />
          <Button label="Create" class="bg-blue-600 hover:bg-blue-700" @click="createEvent" />
        </template>
      </Dialog>
    </div>
  </template>
  
  <script>
  import Button from 'primevue/button'
  import Dialog from 'primevue/dialog'
  import InputText from 'primevue/inputtext'
  import EventCard from '../components/EventCard.vue'
  import api from '../services/api'
  
  export default {
    name: 'Events',
    components: { Button, Dialog, InputText, EventCard },
    data() {
      return {
        events: [],
        showCreateEventDialog: false,
        newEvent: {
          description: '',
          eventDate: '',
          eventTime: '',
          location: '',
          maxParticipants: 1,
          boardGameInstanceId: null,
        },
      }
    },
    async created() {
      try {
        const response = await api.get('/events')
        this.events = response.data
      } catch (error) {
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load events.',
          life: 3000,
        })
        console.error(error)
      }
    },
    methods: {
      async createEvent() {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user.userAccountId) {
          this.$router.push('/login')
          return
        }
        try {
          const eventData = {
            ...this.newEvent,
            organizerId: user.userAccountId,
          }
          await api.post('/events', eventData)
          this.$toast.add({
            severity: 'success',
            summary: 'Event Created',
            detail: 'Your event has been created!',
            life: 3000,
          })
          this.showCreateEventDialog = false
          this.newEvent = { description: '', eventDate: '', eventTime: '', location: '', maxParticipants: 1, boardGameInstanceId: null }
          const response = await api.get('/events')
          this.events = response.data
        } catch (error) {
          this.$toast.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to create event.',
            life: 3000,
          })
          console.error(error)
        }
      },
    },
  }
  </script>