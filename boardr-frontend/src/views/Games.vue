<template>
    <div class="py-6">
      <h1 class="text-3xl font-bold mb-6">Browse Games</h1>
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
        <GameCard v-for="game in games" :key="game.individualGameId" :game="game" @request="openRequestForm" />
      </div>

    <!-- Request Form Dialog -->
    <Dialog v-model:visible="showRequestFormDialog" header="Request Game" :style="{ width: '30rem' }">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium">Board Game: </label>
          <label ><strong> {{ selectedGame.boardGameName }}</strong> </label>
        </div>
        <div>
          <label class="block text-sm font-medium">Borrow Date*</label>
          <InputText v-model="borrowDate" placeholder="Enter date: (YYYYMMDD)" class="w-full" />
        </div>
        <div>
          <label class="block text-sm font-medium">Return Date*</label>
          <InputText v-model="returnDate" placeholder="Enter Date: (YYYYMMDD)" class="w-full" />
        </div>
      </div>
      <template #footer>
        <Button label="Cancel" class="p-button-text" @click="showRequestFormDialog = false" />
        <Button label="Confirm" class="bg-green-600 hover:bg-green-700" @click="createBorrowRequest" />
      </template>
    </Dialog>

    </div>
  </template>
  
  <script>
import { ref, onMounted } from 'vue'
import GameCard from '../components/GameCard.vue'
import api from '../services/api'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import { useToast } from 'primevue/usetoast'

export default {
  name: 'Games',
  components: { GameCard, Dialog, InputText, Button },
  setup() {
    const toast = useToast()
    const games = ref([])
    const showRequestFormDialog = ref(false)
    const selectedGame = ref({})
    const borrowDate = ref('')
    const returnDate = ref('')

    const openRequestForm = (game) => {
      selectedGame.value = game
      showRequestFormDialog.value = true
    }

    const formatDate = (date) => {
      const str = date.toString()
      return `${str.slice(0, 4)}-${str.slice(4, 6)}-${str.slice(6, 8)}`
    }

    const createBorrowRequest = async () => {

      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        const formattedBorrowDate = new Date(formatDate(borrowDate.value))
        const formattedReturnDate = new Date(formatDate(returnDate.value))

        const borrowRequest = {
          userAccountId: user.userAccountId, // Replace with actual user ID
          boardGameInstanceId: selectedGame.value.individualGameId,
          requestDate: formattedBorrowDate,
          returnDate: formattedReturnDate,
        }

        const response = await api.post('/borrowRequests', borrowRequest)
        console.log('Borrow request created:', response.data)

        // Show success message
        toast.add({
          severity: 'success',
          summary: 'Request Created',
          detail: 'Your borrow request has been submitted!',
          life: 3000,
        })

        showRequestFormDialog.value = false
      } catch (error) {
        console.error('Failed to create borrow request:', error)
        toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to create borrow request.',
          life: 3000,
        })
      }
    }

    onMounted(async () => {
      try {
        const response = await api.get('/boardgameinstances')
        games.value = response.data
      } catch (error) {
        console.error('Failed to load games:', error)
      }
    })

    return {
      games,
      showRequestFormDialog,
      selectedGame,
      borrowDate,
      returnDate,
      openRequestForm,
      createBorrowRequest,
    }
  },
}
</script>