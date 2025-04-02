<template>
    <div class="py-6">
      <h1 class="text-3xl font-bold mb-6">My Account</h1>
      <table class="mb-4" style="width: auto; margin-left: auto;">
        <tbody>
          <tr>
            <td style="padding-right: 24px;">
              <Button
                label="History"
                icon="pi pi-history"
                class="bg-blue-600 hover:bg-blue-700"
                @click="goToHistory"
              />
            </td>
            <td style="padding-right: 24px;">
              <Button
                label="Add Game Instance"
                icon="pi pi-plus"
                class="bg-blue-600 hover:bg-blue-700"
                @click="showAddGameDialog = true"
              />
            </td>
            <td>
              <Button
                :label="isGameOwner ? 'Switch to Player' : 'Switch to Game Owner'"
                :icon="isGameOwner ? 'pi pi-user' : 'pi pi-briefcase'"
                class="bg-purple-600 hover:bg-purple-700"
                @click="toggleView"
              />
            </td>
          </tr>
        </tbody>
      </table>
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
        <h2 class="text-2xl font-semibold mb-4">My Game Instances</h2>
        <DataTable :value="ownedGames" class="p-datatable-sm">
          <Column field="individualGameId" header="Game Instance ID" />
          <Column field="boardGameName" header="Title" />
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
          <Column field="eventDate" header="Date">
            <template #body="slotProps">
              {{ formatDate(slotProps.data.eventDate) }}
            </template>
          </Column>
          <Column header="Date" style="width: 15%">
            <template #body="slotProps">
              {{ formatDate(slotProps.data.eventDate) }}
            </template>
          </Column>
          <Column header="Time" style="width: 10%">
            <template #body="slotProps">
              {{ formatTime(slotProps.data.eventTime) }}
            </template>
          </Column>
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
          <Column field="eventDate" header="Date">
            <template #body="slotProps">
              {{ formatDate(slotProps.data.eventDate) }}
            </template>
          </Column>
          <Column header="Date" style="width: 15%">
            <template #body="slotProps">
              {{ formatDate(slotProps.data.eventDate) }}
            </template>
          </Column>
          <Column header="Time" style="width: 10%">
            <template #body="slotProps">
              {{ formatTime(slotProps.data.eventTime) }}
            </template>
          </Column>
          <Column field="location" header="Location" />
          <Column header="View Details">
            <template #body>
              <Button icon="pi pi-info-circle" class="p-button-text" />
            </template>
          </Column>
        </DataTable>
      </div>
  
      <!-- Add Game Dialog -->
      <Dialog v-model:visible="showAddGameDialog" header="Add a Game Instance" :style="{ width: '30rem' }">
        <div class="space-y-4">
          <div>
            <label for="gameName" class="block text-sm font-medium">Game Title</label>
            <Dropdown
              id="gameName"
              v-model="selectedGame"
              :options="boardGames"
              optionLabel="name"
              placeholder="Select a title"
              class="w-full"
              :filter="true"
            />
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
  import Dropdown from 'primevue/dropdown'
  import GameCard from '../components/GameCard.vue'
  import api from '../services/api'
  
  export default {
    name: 'Account',
    components: { Card, Button, DataTable, Column, Dialog, InputText, Dropdown, GameCard },
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
        boardGames: [], // List of available board games
      }
    },
    created() {
      this.loadUserData()
      this.loadBoardGames()
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
      
      async addGame() {
        try {
          // Check if a game is selected
          if (!this.selectedGame) {
            this.$toast.add({
              severity: 'error',
              summary: 'Error',
              detail: 'Please select a game from the dropdown.',
              life: 3000,
            })
            return
          }

          const instance = {
            condition: this.newGame.condition,
            boardGameId: this.selectedGame.gameId, // Use the ID directly from the selected object
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
      async loadBoardGames() {
        try {
          const response = await api.get('/boardgames')
          this.boardGames = response.data
        } catch (error) {
          console.error('Failed to load board games:', error)
        }
      },
    },
  }
  </script>