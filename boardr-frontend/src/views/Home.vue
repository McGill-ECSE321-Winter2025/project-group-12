<template>
  <div class="home-container flex flex-col h-full w-full p-4 gap-4">
    <!-- Title Section -->
    <div class="title-section flex-shrink-0">
      <h1 class="text-3xl font-bold">Welcome to Boardr</h1>
      <p class="text-lg text-[#e0e0e0] dark:text-[#999] mt-2">
        Connect with board game enthusiasts, share games, and join events!
      </p>
    </div>

    <!-- Content Grid -->
    <div class="content-grid flex-1 grid grid-cols-1 md:grid-cols-3 gap-4 overflow-hidden">
      <Card class="info-section">
        <template #title>About Boardr</template>
        <template #content>
          <p>A platform for board game lovers to connect and play.</p>
        </template>
      </Card>
      <Card class="events-section col-span-2">
        <template #title>Featured Games</template>
        <template #content>
          <DataTable :value="games" :rows="5" paginator class="h-full">
            <Column field="boardGameName" header="Game Name" />
            <Column field="condition" header="Condition" />
            <Column field="available" header="Available" :body="slot => slot.data.available ? 'Yes' : 'No'" />
          </DataTable>
        </template>
      </Card>
    </div>
  </div>
</template>

<script>
import Card from 'primevue/card'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import api from '../services/api'

export default {
  name: 'Home',
  components: { Card, DataTable, Column },
  data() {
    return {
      games: [],
    }
  },
  async created() {
    try {
      const response = await api.get('/boardgameinstances')
      this.games = response.data.slice(0, 5) // Limit to 5 for demo
    } catch (error) {
      this.$toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to load games', life: 3000 })
    }
  },
}
</script>

<style scoped>
.home-container {
  height: 100%;
  width: 100%;
}

.content-grid {
  min-height: 0;
}

:deep(.p-card) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.p-card-body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

:deep(.p-card-content) {
  flex: 1;
}
</style>