<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">
      Browse Available
      <span style="color: #10B981;"> Board Games</span>
    </h1>
    <!-- Outer container with rounded edges -->
    <div class="rounded-3xl overflow-hidden border border-gray-300">
      <DataTable
        :value="boardGames"
        class="p-datatable-sm"
        responsiveLayout="scroll"
      >
        <!-- Row Numbering -->
        <Column header="#" style="width: 5%">
          <template #body="slotProps">
            {{ slotProps.index + 1 }}
          </template>
        </Column>

        <!-- Name -->
        <Column field="name" header="Name" style="width: 15%"></Column>

        <!-- Description Column with a "View" Button that navigates -->
        <Column header="Description" style="width: 10%">
          <template #body="slotProps">
            <Button
              label="View"
              class="bg-blue-500 hover:bg-blue-600 text-white"
              @click="viewInstances(slotProps.data)"
            />
          </template>
        </Column>

        <!-- Join Event Column with "View" Button that filters Events by board game name -->
        <Column header="Join Event" style="width: 10%">
          <template #body="slotProps">
            <Button
              label="View"
              class="bg-green-500 hover:bg-green-600 text-white"
              @click="joinEvent(slotProps.data)"
            />
          </template>
        </Column>

        <!-- Read Review -->
        <Column header="Read Review" style="width: 10%">
          <template #body="slotProps">
            <Button
              label="Read"
              class="bg-blue-500 hover:bg-blue-600 text-white"
              @click="readReview(slotProps.data)"
            />
          </template>
        </Column>

        <!-- Leave a Review -->
        <Column header="Leave a Review" style="width: 10%">
          <template #body="slotProps">
            <Button
              label="Review"
              class="bg-yellow-500 hover:bg-yellow-600 text-white"
              @click="leaveReview(slotProps.data)"
            />
          </template>
        </Column>

        <!-- Borrow a Game -->
        <Column header="Borrow a Game" style="width: 10%">
          <template #body="slotProps">
            <Button
              label="Borrow"
              class="bg-purple-500 hover:bg-purple-600 text-white"
              @click="borrowGame(slotProps.data)"
            />
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<script>
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import api from '../services/api'

export default {
  name: 'BoardGames',
  components: { DataTable, Column, Button },
  data() {
    return {
      boardGames: [],
    }
  },
  async created() {
    try {
      const response = await api.get('/boardgames')
      this.boardGames = response.data
    } catch (error) {
      this.$toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to load board games.',
        life: 3000,
      })
      console.error(error)
    }
  },
  methods: {
    // Navigates to the BoardGameInstances page (for the selected board game)
    viewInstances(boardGame) {
      this.$router.push({ name: 'BoardGameInstances', params: { boardGameId: boardGame.gameId } })
    },
    // Navigates to the Events page filtered by the board game's name
    joinEvent(boardGame) {
      // Passing the board game's name as a query parameter for filtering on the Events page.
      this.$router.push({ name: 'Events', query: { boardGameName: boardGame.name } })
    },
    readReview(boardGame) {
      alert(`Reading review for board game: ${boardGame.name}`)
    },
    leaveReview(boardGame) {
      alert(`Leaving a review for board game: ${boardGame.name}`)
    },
    borrowGame(boardGame) {
      this.$router.push({ name: 'BoardGameInstance', params: { boardGameId: boardGame.gameId } })
    },
  },
}
</script>

<style scoped>
:deep(.p-datatable) {
  border-radius: 1rem !important;
  overflow: hidden !important;
}

/* Style the header cells */
:deep(.p-datatable-sm thead > tr > th) {
  background-color: #10B981; /* Green matching bg-green-500 */
  color: #fff;
  font-family: 'Arial', sans-serif;
  font-weight: 600;
  font-size: 0.75rem !important;
}

/* Style the body cells to match header text size */
:deep(.p-datatable-sm tbody > tr > td) {
  font-size: 0.75rem !important;
}

/* Make all PrimeVue buttons smaller */
:deep(.p-button) {
  font-size: 0.75rem !important;
}

</style>