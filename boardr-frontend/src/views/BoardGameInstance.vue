<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">
      Browse Board Game Instances
    </h1>
    <!-- Outer container with rounded edges -->
    <div class="rounded-3xl overflow-hidden border border-gray-300">
      <DataTable
        :value="boardGameInstances"
        class="p-datatable-sm"
        responsiveLayout="scroll"
      >
        <!-- Row Numbering -->
        <Column header="#" style="width: 5%">
          <template #body="slotProps">
            {{ slotProps.index + 1 }}
          </template>
        </Column>
  
        <!-- Board Game Name -->
        <Column field="boardGameName" header="Game" style="width: 20%"></Column>
  
        <!-- Owner Name -->
        <Column header="Owner" style="width: 20%">
          <template #body="slotProps">
            {{ slotProps.data.gameOwnerName }}
          </template>
        </Column>
  
        <!-- Condition -->
        <Column field="condition" header="Condition" style="width: 15%"></Column>
  
        <!-- Availability -->
        <Column header="Availability" style="width: 15%">
          <template #body="slotProps">
            <span v-if="slotProps.data.available" class="text-green-500 font-bold">
              Available
            </span>
            <span v-else class="text-red-500 font-bold">
              Unavailable
            </span>
          </template>
        </Column>
  
        <!-- Request Button Column -->
        <Column header="Borrow" style="width: 15%">
          <template #body="slotProps">
            <Button
              label="Request"
              class="bg-indigo-500 hover:bg-indigo-600 text-white"
              @click="openRequestForm(slotProps.data)"
            />
          </template>
        </Column>
      </DataTable>
    </div>
  
    <!-- Request Form Dialog -->
    <Dialog
      v-model:visible="showRequestFormDialog"
      header="Request Game"
      :style="{ width: '30rem' }"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium">Board Game: </label>
          <label><strong>{{ selectedInstance.boardGameName }}</strong></label>
        </div>
        <div>
          <label class="block text-sm font-medium">Borrow Date </label>
          <InputText
            v-model="borrowDate"
            placeholder="Enter date: (YYYYMMDD)"
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm font-medium">Return Date </label>
          <InputText
            v-model="returnDate"
            placeholder="Enter date: (YYYYMMDD)"
            class="w-full"
          />
        </div>
      </div>
      <template #footer>
        <Button
          label="Cancel"
          class="p-button-text"
          @click="showRequestFormDialog = false"
        />
        <Button
          label="Confirm"
          class="bg-green-600 hover:bg-green-700"
          @click="createBorrowRequest"
        />
      </template>
    </Dialog>
  </div>
</template>
  
<script>
import { ref, onMounted } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import api from '../services/api'
import { useToast } from 'primevue/usetoast'
  
export default {
  name: 'BoardGameInstances',
  components: { DataTable, Column, Button, Dialog, InputText },
  props: {
    boardGameId: {
      type: [Number, String],
      required: true,
    },
  },
  setup(props) {
    const toast = useToast()
    const boardGameInstances = ref([])
    const showRequestFormDialog = ref(false)
    const selectedInstance = ref({})
    const borrowDate = ref('')
    const returnDate = ref('')
  
    // Opens the dialog for the selected board game instance
    const openRequestForm = (instance) => {
      selectedInstance.value = instance
      showRequestFormDialog.value = true
    }
  
    // Utility function: Convert "YYYYMMDD" string to "YYYY-MM-DD"
    const formatDate = (dateStr) => {
      if (dateStr.length !== 8) return dateStr
      return `${dateStr.slice(0, 4)}-${dateStr.slice(4, 6)}-${dateStr.slice(6, 8)}`
    }
  
    // Creates a borrow request using the logged-in user's ID automatically
    const createBorrowRequest = async () => {
      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user || !user.userAccountId) {
          toast.add({
            severity: 'error',
            summary: 'User not logged in',
            detail: 'Please log in to request a game.',
            life: 3000,
          })
          return
        }
  
        const formattedBorrowDate = new Date(formatDate(borrowDate.value))
        const formattedReturnDate = new Date(formatDate(returnDate.value))
  
        const borrowRequest = {
          userAccountId: user.userAccountId,
          boardGameInstanceId: selectedInstance.value.individualGameId,
          requestDate: formattedBorrowDate,
          returnDate: formattedReturnDate,
        }
  
        const response = await api.post('/borrowRequests', borrowRequest)
        console.log('Borrow request created:', response.data)
  
        toast.add({
          severity: 'success',
          summary: 'Request Created',
          detail: 'Your borrow request has been submitted!',
          life: 3000,
        })
  
        showRequestFormDialog.value = false
      } catch (error) {
        console.error('Failed to create borrow request:', error)
        let errorMessage = 'Failed to create borrow request.'
        if (
          error.response &&
          error.response.data &&
          error.response.data.message &&
          error.response.data.message.includes('Board game instance is not available')
        ) {
          errorMessage = 'BoardGame is not available.'
        }
        toast.add({
          severity: 'error',
          summary: 'Error',
          detail: errorMessage,
          life: 3000,
        })
      }
    }
  
    onMounted(async () => {
      try {
        const response = await api.get(`/boardgameinstances/boardgame/${props.boardGameId}`)
        boardGameInstances.value = response.data
      } catch (error) {
        console.error('Failed to load board game instances:', error)
        toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load board game instances.',
          life: 3000,
        })
      }
    })
  
    return {
      boardGameInstances,
      showRequestFormDialog,
      selectedInstance,
      borrowDate,
      returnDate,
      openRequestForm,
      createBorrowRequest,
    }
  },
}
</script>
  
<style scoped>
:deep(.p-datatable) {
  border-radius: 1rem !important;
  overflow: hidden !important;
}
  
:deep(.p-datatable-sm thead > tr > th) {
  background-color: #10B981;
  color: #fff;
  font-family: 'Arial', sans-serif;
  font-weight: 600;
  font-size: 0.75rem !important;
}
  
:deep(.p-datatable-sm tbody > tr > td) {
  font-size: 0.75rem !important;
}
  
/* Dialog styling (if needed) */
</style>
