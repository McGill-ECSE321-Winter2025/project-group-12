<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">
      Browse Available
      <span style="color: #10B981;"> Board Game</span>
      Titles
    </h1>
    <Button
        label="Add Board Game Title"
        icon="pi pi-plus"
        class="mb-6 bg-blue-600 hover:bg-blue-700"
        @click="showCreateGameDialog = true"
    />
    <div>
      <br>
    </div>
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
        <Column header="Read Reviews" style="width: 10%">
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

      <!-- Read Reviews Dialog -->
      <Dialog
        v-model:visible="showReviewDialog"
        :header="`${selectedGame} Reviews`"
        :style="{ width: '40rem' }"
      >
        <div v-if="loadingReviews" class="text-center p-4">
          Loading reviews...
        </div>
        <div v-else-if="error" class="text-red-500 p-4">
          {{ error }}
        </div>
        <div v-else-if="reviews.length === 0" class="p-4">
          No reviews yet.
        </div>
        <div v-else class="max-h-[70vh] overflow-y-auto p-2">
          <div
            v-for="(review, index) in reviews"
            :key="review.id"
          >
            <div class="mb-4 bg-white rounded-lg border-2 border-gray-300 shadow-sm">
              <div class="bg-gray-100 p-3 border-b-2 border-gray-300 flex justify-between items-center">
                <div class="font-medium text-gray-600">
                  User #{{ review.userAccountId }}
                </div>
                <div class="flex text-yellow-500">
                  <span v-for="i in 5" :key="i" class="text-lg">
                    <span v-if="i <= review.rating">★</span>
                    <span v-else>☆</span>
                  </span>
                </div>
              </div>
              
              <div class="p-4">
                <p class="text-gray-700">{{ review.comment }}</p>
              </div>
              
              <div class="px-4 py-2 bg-gray-50 border-t border-gray-300 text-right">
                <span class="text-sm text-gray-500">{{ review.reviewDate }}</span>
              </div>
            </div>
            
            <hr v-if="index < reviews.length - 1" class="border-t border-gray-200 my-4">
          </div>
        </div>
        
        <template #footer>
          <Button label="Close" @click="showReviewDialog = false" />
        </template>
      </Dialog>
      
       <!-- Create Review Dialog -->
      <Dialog v-model:visible="showCreateReviewDialog" header="Add a Review" :style="{ width: '30rem' }">
        <div class="space-y-4">
          <div>
            <label for="rating" class="block text-sm font-medium">Rating</label>
            <Dropdown
              id="rating"
              v-model="newReview.rating"
              :options="ratingOptions"
              placeholder="Select a rating"
              class="w-full"
            />
          </div>
          <div>
            <label for="comments" class="block text-sm font-medium">Comments</label>
            <InputText id="comments" v-model="newReview.comments" class="w-full" />
          </div>
        </div>
        <template #footer>
          <Button label="Cancel" class="p-button-text" @click="showCreateReviewDialog = false" />
          <Button label="Add" class="bg-blue-600 hover:bg-blue-700" @click="createReview" />
        </template>
      </Dialog>

    </div>

    <!-- Create Game Dialog -->
    <Dialog v-model:visible="showCreateGameDialog" header="Add New Board Game Title" :style="{ width: '30rem' }">
      <div class="space-y-4">
        <div>
          <label for="name" class="block text-sm font-medium">Game Title</label>
          <InputText id="name" v-model="newGame.name" class="w-full" />
        </div>
        <div>
          <label for="description" class="block text-sm font-medium">Description</label>
          <InputText id="description" v-model="newGame.description" class="w-full" />
        </div>
      </div>
      <template #footer>
        <Button label="Cancel" class="p-button-text" @click="showCreateGameDialog = false" />
        <Button label="Add Board Game Genre" class="bg-blue-600 hover:bg-blue-700" @click="createGame" />
      </template>
    </Dialog>
    
    
  </div>
</template>

<script>
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import api from '../services/api'
import Textarea from 'primevue/textarea'

export default {
  name: 'BoardGames',

  components: { DataTable, Column, Button, Dialog, InputText, Textarea, Dropdown },
  data() {
    return {
      boardGames: [],
      showCreateGameDialog: false,
      ratingOptions: [1, 2, 3, 4, 5],
      newReview: {userId: null, boardGameId: null, name: "", rating: null, comments: '' },
      showCreateReviewDialog: false,
      newGame: {
        name: '',
        description: ''
      }, 
      showReviewDialog: false,
      reviews: [],
      loadingReviews: false,
      error: null,
      selectedGame: "",
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
      this.fetchReviews(boardGame.gameId)
      this.selectedGame = boardGame.name
    },
    leaveReview(boardGame) {
      this.newReview.boardGameId = boardGame.gameId
      this.newReview.name = boardGame.name
      this.showCreateReviewDialog = true
    },
    async createReview() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.userAccountId) {
        this.$router.push('/login')
        return
      }
      try {
          const instance = {
            boardGameId: this.newReview.boardGameId,
            userAccountId: user.userAccountId,
            rating: this.newReview.rating,
            comment: this.newReview.comments,
          }
          await api.post('/reviews', instance)
          this.$toast.add({
            severity: 'success',
            summary: 'Review Created',
            detail: `Your review of ${this.newReview.name} has been added!`,
            life: 3000,
          })
          this.showCreateReviewDialog = false
          this.newReview = {userId: null, boardGameId: null, name: "", rating: null, comments: '' }
        } catch (error) {
          this.$toast.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to add review.',
            life: 3000,
          })
          console.error(error)
        }
    },
    async fetchReviews(gameId) {
      this.currentBoardGameId = gameId;
      try {
        this.loadingReviews = true;
        this.error = null;
        const response = await api.get(`/boardgame/${gameId}/reviews`);
        this.reviews = response.data;
        this.showReviewDialog = true;
      } catch (error) {
        this.error = 'Failed to load reviews. Please try again later.';
      } finally {
        this.loadingReviews = false;
      }
    },
    borrowGame(boardGame) {
      this.$router.push({ name: 'BoardGameInstance', params: { boardGameId: boardGame.gameId } })
    },

    async createGame() {
      // Check if user is logged in
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.userAccountId) {
        this.$router.push('/login')
        return
      }

      try {
        // Validate input fields
        if (!this.newGame.name || !this.newGame.description) {
          this.$toast.add({
            severity: 'error',
            summary: 'Validation Error',
            detail: 'Name and description are required',
            life: 3000,
          })
          return
        }

        // Create game data object matching the expected DTO
        const gameData = {
          name: this.newGame.name.trim(),
          description: this.newGame.description.trim()
        }

        // POST request to create the board game
        await api.post('/boardgames', gameData)
        
        // Success message
        this.$toast.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Board game added successfully!',
          life: 3000,
        })
        
        // Reset form and close dialog
        this.showCreateGameDialog = false
        this.newGame = {
          name: '',
          description: ''
        }
        
        // Refresh the board games list
        const response = await api.get('/boardgames')
        this.boardGames = response.data
      } catch (error) {
        console.error('Error creating board game:', error)
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: error.response?.data?.errors?.[0] || 'Failed to add board game.',
          life: 3000,
        })
      }
    }
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