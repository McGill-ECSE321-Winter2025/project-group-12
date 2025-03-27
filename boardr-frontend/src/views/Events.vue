<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">Browse Available Events
      <Button
        label="Create Event"
        icon="pi pi-plus"
        class="mb-6 bg-blue-600 hover:bg-blue-700"
        @click="showCreateEventDialog = true"
      />
    </h1>

    <!-- Search Bar -->
    <div class="mb-4 flex items-center">
      <h2>
        <InputText v-model="searchQuery" placeholder="Search events by name" class="w-3/4" />
        <Button label="Search" class="ml-2" @click="searchEvents" />
      </h2>
    </div>

    <!-- DataTable for events -->
    <DataTable :value="events" class="p-datatable-sm" responsiveLayout="scroll">
      <Column field="eventId" header="#" align="center" style="width: 5%"></Column>
      <Column field="description" header="Name" style="width: 20%"></Column>
      <!-- Format the eventDate -->
      <Column header="Date" style="width: 10%">
        <template #body="slotProps">
          {{ formatDate(slotProps.data.eventDate) }}
        </template>
      </Column>
      <!-- Format the eventTime -->
      <Column header="Time" style="width: 10%">
        <template #body="slotProps">
          {{ formatTime(slotProps.data.eventTime) }}
        </template>
      </Column>
      <Column field="location" header="Location" style="width: 20%"></Column>
      <Column header="Organizer" style="width: 20%">
        <template #body="slotProps">
          {{ slotProps.data.organizerId }}
        </template>
      </Column>
      <Column header="Participate" style="width: 15%">
        <template #body="slotProps">
          <Button label="Register" @click="openRegisterDialog(slotProps.data)" class="bg-green-500 hover:bg-green-600 text-white" />
        </template>
      </Column>
    </DataTable>

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

    <!-- Registration Confirmation Dialog -->
    <Dialog v-model:visible="showRegisterConfirmDialog" header="Confirm Registration" :style="{ width: '20rem' }">
      <p class="mb-4">Are you sure?</p>
      <template #footer>
        <Button label="Cancel" class="p-button-text" @click="showRegisterConfirmDialog = false" />
        <Button label="Confirm" class="bg-green-600 hover:bg-green-700" @click="registerForEvent" />
      </template>
    </Dialog>
  </div>
</template>

<script>
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import api from '../services/api'

export default {
  name: 'Events',
  components: { Button, Dialog, InputText, DataTable, Column },
  data() {
    return {
      events: [],
      originalEvents: [],   // Store the original events for filtering
      searchQuery: '',    // Search query for filtering events
      showCreateEventDialog: false,
      showRegisterConfirmDialog: false,
      selectedEvent: null,
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
      const eventsResponse = await api.get('/events')
      this.events = eventsResponse.data
      this.originalEvents = eventsResponse.data   // Store the original events for filtering
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
    formatDate(date) {
      const dateStr = String(date);
      if (dateStr.length === 8) {
        return `${dateStr.slice(0, 4)}-${dateStr.slice(4, 6)}-${dateStr.slice(6, 8)}`;
      }
      return date;
    },
    formatTime(time) {
      const timeStr = String(time).padStart(4, '0');
      if (timeStr.length === 4) {
        return `${timeStr.slice(0, 2)}:${timeStr.slice(2, 4)}`;
      }
      return time;
    },
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
        this.newEvent = {
          description: '',
          eventDate: '',
          eventTime: '',
          location: '',
          maxParticipants: 1,
          boardGameInstanceId: null,
        }
        const response = await api.get('/events')
        this.events = response.data
        this.originalEvents = response.data   // Update the original events for filtering
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
    openRegisterDialog(event) {
      this.selectedEvent = event
      this.showRegisterConfirmDialog = true
    },
    async registerForEvent() {
      try {
        // Simulate registration action
        this.$toast.add({
          severity: 'success',
          summary: 'Registered',
          detail: 'You have been registered for the event!',
          life: 3000,
        })
      } catch (error) {
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Registration failed.',
          life: 3000,
        })
        console.error(error)
      }
      this.showRegisterConfirmDialog = false
      this.selectedEvent = null
    },
    // New method to filter events by searchQuery
    searchEvents() {
      if (!this.searchQuery.trim()) {
        this.events = this.originalEvents
      } else {
        const query = this.searchQuery.toLowerCase();
        this.events = this.originalEvents.filter(event =>
          event.description.toLowerCase().includes(query)
        );
      }
    },
  },
}
</script>