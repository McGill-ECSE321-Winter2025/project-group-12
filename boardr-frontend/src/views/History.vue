<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">History</h1>

    <!-- Lending History Table (For Game Owners Only) -->
    <h2 class="text-2xl font-semibold mb-4">Lending History</h2>
    <DataTable :value="lendingHistory" class="p-datatable-sm" responsiveLayout="scroll">
      <Column field="boardGameInstanceId" header="Game ID" style="width: 10%;" />
      <Column field="userAccountId" header="Borrower ID" style="width: 15%;" />
      <Column field="requestDate" header="Request Date" style="width: 15%;" />
      <Column field="returnDate" header="Return Date" style="width: 15%;" />
      <Column field="requestStatus" header="Status" style="width: 10%;" />
    </DataTable>

    <!-- Borrowing History Table -->
    <h2 class="text-2xl font-semibold mb-4">Borrowing History</h2>
    <DataTable :value="borrowingHistory" class="p-datatable-sm" responsiveLayout="scroll">
      <Column field="boardGameInstanceId" header="Game ID" style="width: 10%;" />
      <Column field="lenderId" header="Lender ID" style="width: 15%;" />
      <Column field="borrowDate" header="Borrow Date" style="width: 15%;" />
      <Column field="returnDate" header="Return Date" style="width: 15%;" />
      <Column field="borrowStatus" header="Status" style="width: 10%;" />
    </DataTable>
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
      lendingHistory: [],
      borrowingHistory: [],
    }
  },
  async created() {
    try {
      // Fetch lending history for game owners
      const lendingRes = await api.get(`/users/${this.$root.user.gameOwnerRoleId}/lending-history`)
      this.lendingHistory = lendingRes.data

      // Fetch borrowing history
      const borrowingRes = await api.get(`/users/${this.$root.user.userAccountId}/borrowing-history`)
      this.borrowingHistory = borrowingRes.data
    } catch (error) {
      console.error(error)
    }
  },
}
</script>