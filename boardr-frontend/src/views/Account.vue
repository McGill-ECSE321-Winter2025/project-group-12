<template>
    <div class="py-6">
      <h1 class="text-3xl font-bold mb-6">My Account</h1>
      <div class="flex justify-end mb-4">
        <Button
          label="History"
          icon="pi pi-history"
          class="mb-6 bg-blue-600 hover:bg-blue-700"
          @click="goToHistory"
        />
        <Button
          label="Add Game"
          icon="pi pi-plus"
          class="mt-4 bg-blue-600 hover:bg-blue-700"
          @click="showAddGameDialog = true"
        />
        <Button
          :label="isGameOwner ? 'Switch to Player' : 'Switch to Game Owner'"
          :icon="isGameOwner ? 'pi pi-user' : 'pi pi-briefcase'"
          class="bg-purple-600 hover:bg-purple-700"
          @click="toggleView"
        />
      </div>
      <Card v-if="user" class="mb-6">
        <template #title>
          <h2 class="text-xl font-semibold">{{ user.name }}</h2>
        </template>
        <template #content>
          <p><strong>Email:</strong> {{ user.email }}</p>
          <p><strong>Account Type:</strong> {{ user.gameOwnerRoleId ? 'Game Owner' : 'Player' }}</p>
          <p><strong>Account ID:</strong> {{ user.userAccountId }}</p>
          <p v-if="user.gameOwnerRoleId"><strong>Game Owner ID:</strong> {{ user.gameOwnerRoleId }}</p>
        </template>
      </Card>
  
      <!-- Owned Games (Game Owners Only) -->
      <div v-if="isGameOwner && user?.gameOwnerRoleId" class="mb-6">
        <h2 class="text-2xl font-semibold mb-4">My Games</h2>
        <DataTable :value="ownedGames" class="p-datatable-sm">
          <Column field="individualGameId" header="Game ID" />
          <Column field="boardGameName" header="Name" />
          <Column field="condition" header="Condition" />
          <Column field="available" header="Status">
            <template #body="slotProps">
              {{ slotProps.data.available ? 'Available' : 'Not Available' }}
            </template>
          </Column>
          <Column header="View Requests">
            <template #body>
              <Button icon="pi pi-eye" class="p-button-text" />
            </template>
          </Column>
        </DataTable>
      </div>
  
      <!-- Created Event History -->
      <div class="mb-6">
        <h2 class="text-2xl font-semibold mb-4">Created Events</h2>
        <DataTable :value="participatedEvents" class="p-datatable-sm">
          <Column field="eventId" header="Event ID" />
          <Column field="description" header="Description" />
          <Column field="eventDate" header="Date" :body="row => formatDate(row.eventDate)" />
          <Column field="eventTime" header="Time" :body="row => formatTime(row.eventTime)" />
          <Column field="location" header="Location" />
          <Column header="View Details">
            <template #body>
              <Button icon="pi pi-info-circle" class="p-button-text" />
            </template>
          </Column>
        </DataTable>
      </div>
  
      <!-- Registered Events -->
      <div class="mb-6">
        <h2 class="text-2xl font-semibold mb-4">Registered Events</h2>
        <DataTable :value="registeredEvents" class="p-datatable-sm">
          <Column field="eventId" header="Event ID" />
          <Column field="description" header="Description" />
          <Column field="eventDate" header="Date" :body="row => formatDate(row.eventDate)" />
          <Column field="eventTime" header="Time" :body="row => formatTime(row.eventTime)" />
          <Column field="location" header="Location" />
          <Column header="View Details">
            <template #body>
              <Button icon="pi pi-info-circle" class="p-button-text" />
            </template>
          </Column>
        </DataTable>
      </div>
  
      <!-- Add Game Dialog -->
      <Dialog v-model:visible="showAddGameDialog" header="Add a Game" :style="{ width: '30rem' }">
        <div class="space-y-4">
          <div>
            <label for="gameName" class="block text-sm font-medium">Game Name</label>
            <InputText id="gameName" v-model="newGame.name" class="w-full" />
          </div>
          <div>
            <label for="condition" class="block text-sm font-medium">Condition</label>
            <InputText id="condition" v-model="newGame.condition" class="w-full" />
          </div>
        </div>
        <template #footer>
          <Button label="Cancel" class="p-button-text" @click="showAddGameDialog = false" />
          <Button label="Add" class="bg-blue-600 hover:bg-blue-700" @click="addGame" />
        </template>
      </Dialog>
    </div>
  </template>
  
  <script>
  import Card from 'primevue/card'
  import Button from 'primevue/button'
  import DataTable from 'primevue/datatable'
  import Column from 'primevue/column'
  import Dialog from 'primevue/dialog'
  import InputText from 'primevue/inputtext'
  import GameCard from '../components/GameCard.vue'
  import api from '../services/api'
  
  export default {
    name: 'Account',
    components: { Card, Button, DataTable, Column, Dialog, InputText, GameCard },
    data() {
      return {
        user: null,
        ownedGames: [],
        participatedEvents: [],
        registeredEvents: [],
        lendingHistory: [],
        showAddGameDialog: false,
        newGame: { name: '', condition: '' },
        isGameOwner: true, // Default to Game Owner view
      }
    },
    created() {
      this.loadUserData()
    },
    methods: {
      async loadUserData() {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user.userAccountId) {
          this.$router.push('/login')
          return
        }
        this.user = user
  
        if (user.gameOwnerRoleId) {
          const [ownedRes, lendingRes] = await Promise.all([
            api.get(`/users/${user.gameOwnerRoleId}/owned-games`),
            api.get(`/users/${user.gameOwnerRoleId}/lending-history`),
          ])
          this.ownedGames = ownedRes.data
          this.lendingHistory = lendingRes.data
        }
  
        const [eventsRes, registrationsRes] = await Promise.all([
          api.get('/events'),
          api.get('/registrations')
        ])
        
        // Filter events where user is the organizer
        this.participatedEvents = eventsRes.data.filter(e => e.organizerId === user.userAccountId)
        
        // Get registered events by filtering registrations for this user
        const userRegistrations = registrationsRes.data.filter(r => r.userId === user.userAccountId)
        // Map registration data to event data
        this.registeredEvents = userRegistrations.map(reg => {
          const event = eventsRes.data.find(e => e.eventId === reg.eventId)
          return event
        }).filter(Boolean) // Remove any undefined events
      },
      formatDate(date) {
        const str = date.toString()
        return `${str.slice(0, 4)}-${str.slice(4, 6)}-${str.slice(6, 8)}`
      },
      formatTime(time) {
        const str = time.toString().padStart(4, '0')
        return `${str.slice(0, 2)}:${str.slice(2)}`
      },
      async addGame() {
        try {
          const boardGame = { name: this.newGame.name, description: 'User-added game' }
          const boardGameRes = await api.post('/boardgames', boardGame)
          const instance = {
            condition: this.newGame.condition,
            boardGameId: boardGameRes.data.gameId,
            gameOwnerId: this.user.gameOwnerRoleId,
          }
          await api.post('/boardgameinstances', instance)
          this.$toast.add({
            severity: 'success',
            summary: 'Game Added',
            detail: `${this.newGame.name} added to your collection!`,
            life: 3000,
          })
          this.showAddGameDialog = false
          this.newGame = { name: '', condition: '' }
          this.loadUserData() // Refresh owned games
        } catch (error) {
          this.$toast.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to add game.',
            life: 3000,
          })
          console.error(error)
        }
      },
      goToHistory() {
        this.$router.push('/history')
      },
      toggleView() {
        this.isGameOwner = !this.isGameOwner
      },
    },
  }
  </script>