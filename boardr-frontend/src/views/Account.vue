<template>
    <div class="py-6">
      <h1 class="text-3xl font-bold mb-6">My Account
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
      </h1>
      <Card v-if="user" class="mb-6">
        <template #title>
          <h2 class="text-xl font-semibold">{{ user.name }}</h2>
        </template>
        <template #content>
          <p><strong>Email:</strong> {{ user.email }}</p>
          <p><strong>Account Type:</strong> {{ user.gameOwnerRoleId ? 'Game Owner' : 'Player' }}</p>
        </template>
      </Card>
  
      <!-- Owned Games (Game Owners Only) -->
      <div v-if="user?.gameOwnerRoleId" class="mb-6">
        <h2 class="text-2xl font-semibold mb-4">My Games</h2>
        <DataTable :value="participatedEvents" class="p-datatable-sm">
          <Column field="gameId" header="Id" />
          <Column field="name" header="Name" :body="row => formatDate(row.eventDate)" />
          <Column field="description" header="Description" :body="row => formatTime(row.eventTime)" />
          <Column field="status" header="Status" />
          <Column field="requests" header="View Requests" />
        </DataTable>
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
          <GameCard v-for="game in ownedGames" :key="game.individualGameId" :game="game" />
        </div>
      </div>
  
      <!-- Event History -->
      <div class="mb-6">
        <h2 class="text-2xl font-semibold mb-4">My Events</h2>
        <DataTable :value="participatedEvents" class="p-datatable-sm">
          <Column field="description" header="Description" />
          <Column field="eventDate" header="Date" :body="row => formatDate(row.eventDate)" />
          <Column field="eventTime" header="Time" :body="row => formatTime(row.eventTime)" />
          <Column field="location" header="Location" />
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
        lendingHistory: [],
        showAddGameDialog: false,
        newGame: { name: '', condition: '' },
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
  
        const eventsRes = await api.get('/events') // Simplified; filter by user registrations in production
        this.participatedEvents = eventsRes.data.filter(e => e.organizerId === user.userAccountId)
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
      }
    },
  }
  </script>