<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">History</h1>
    
    <!-- Lending History Table (For Game Owners Only) -->
    <div v-if="user && user.gameOwnerRoleId">
      <h2 class="text-2xl font-semibold mb-4">Lending History</h2>
      <DataTable :value="lendingHistory" class="p-datatable-sm" responsiveLayout="scroll">
        <Column field="boardGameInstanceId" header="Game ID" style="width: 10%;" />
        <Column header="Borrower" style="width: 15%;">
          <template #body="slotProps">
            {{ lendingDetails[slotProps.data.userAccountId]?.name || slotProps.data.userAccountId }}
          </template>
        </Column>
        <Column header="Borrower Email" style="width: 20%;">
          <template #body="slotProps">
            {{ lendingDetails[slotProps.data.userAccountId]?.email || 'N/A' }}
          </template>
        </Column>
        <Column field="requestDate" header="Request Date" style="width: 15%;" />
        <Column field="returnDate" header="Return Date" style="width: 15%;" />
      </DataTable>
    </div>

    <!-- Borrowing History Table -->
    <div>
      <h2 class="text-2xl font-semibold mb-4">Borrowing History</h2>
      <DataTable :value="borrowingHistory" class="p-datatable-sm" responsiveLayout="scroll">
        <Column field="boardGameInstanceId" header="Game ID" style="width: 15%;" />
        <Column field="requestDate" header="Request Date" style="width: 15%;" />
        <Column field="returnDate" header="Return Date" style="width: 15%;" />
      </DataTable>
    </div>
  </div>
</template>

<script>
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import api from '../services/api'

export default {
  name: 'History',
  components: { DataTable, Column },
  data() {
    return {
      user: null,
      lendingHistory: [],
      borrowingHistory: [],
      lendingDetails: {} // Maps borrower ID to an object containing name and email
    }
  },
  async created() {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.userAccountId) {
      this.$router.push('/login')
      return
    }
    this.user = user

    try {
      if (user.gameOwnerRoleId) {
        const lendingRes = await api.get(`/users/${user.gameOwnerRoleId}/lending-history`)
        this.lendingHistory = lendingRes.data
        this.fetchLendingNames()
      }
      const borrowingRes = await api.get(`/borrowRequests/${user.userAccountId}/borrowing-history`)
      this.borrowingHistory = borrowingRes.data
    } catch (error) {
      console.error(error)
    }
  },
  methods: {
    async fetchLendingNames() {
      const borrowerIds = [...new Set(this.lendingHistory.map(item => item.userAccountId))]
      try {
        const promises = borrowerIds.map(id => api.get(`/users/${id}`))
        const responses = await Promise.all(promises)
        responses.forEach(response => {
          const userData = response.data
          this.lendingDetails[userData.userAccountId] = {
            name: userData.name,
            email: userData.email
          }
        })
      } catch (error) {
        console.error('Error fetching lending names:', error)
      }
    }
  }
}
</script>