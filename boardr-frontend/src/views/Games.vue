<template>
    <div class="py-6">
      <h1 class="text-3xl font-bold mb-6">Browse Games</h1>
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
        <GameCard v-for="game in games" :key="game.individualGameId" :game="game" />
      </div>
    </div>
  </template>
  
  <script>
  import GameCard from '../components/GameCard.vue'
  import api from '../services/api'
  
  export default {
    name: 'Games',
    components: { GameCard },
    data() {
      return {
        games: [],
      }
    },
    async created() {
      try {
        const response = await api.get('/boardgameinstances')
        this.games = response.data
      } catch (error) {
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load games.',
          life: 3000,
        })
        console.error(error)
      }
    },
  }
  </script>