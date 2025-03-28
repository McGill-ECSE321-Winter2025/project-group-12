<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">History</h1>
    
    <!-- Lending History Table (For Game Owners Only) -->
    <div v-if="user && user.gameOwnerRoleId">
      <h2 class="text-2xl font-semibold mb-4">Lending History</h2>
      <DataTable :value="lendingHistory" class="p-datatable-sm" responsiveLayout="scroll">
        <Column field="boardGameInstanceId" header="Game ID" style="width: 10%;" />
        <Column field="userAccountId" header="Borrower ID" style="width: 15%;" />
        <Column field="requestDate" header="Request Date" style="width: 15%;" />
        <Column field="returnDate" header="Return Date" style="width: 15%;" />
        <!-- <Column field="requestStatus" header="Status" style="width: 10%;" /> -->
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
    }
  },
  async created() {
    // Load user data from localStorage just like Account.vue
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.userAccountId) {
      this.$router.push('/login')
      return
    }
    this.user = user

    try {
      // Fetch lending history if the user is a game owner
      if (user.gameOwnerRoleId) {
        const lendingRes = await api.get(`/users/${user.gameOwnerRoleId}/lending-history`)
        this.lendingHistory = lendingRes.data
      }
      // Fetch borrowing history for the logged in user using the new API endpoint
      const borrowingRes = await api.get(`/borrowRequests/${user.userAccountId}/borrowing-history`)
      this.borrowingHistory = borrowingRes.data
    } catch (error) {
      console.error(error)
    }
  },
}
</script>