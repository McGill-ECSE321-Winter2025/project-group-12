<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">History</h1>
    
    <!-- Lending History Table (For Game Owners Only) -->
    <div v-if="user && user.gameOwnerRoleId">
      <h2 class="text-2xl font-semibold mb-4">Lending History</h2>
      <DataTable :value="lendingHistory" class="p-datatable-sm" responsiveLayout="scroll">
        <!-- GAME NAME COLUMN -->
        <Column header="Game" style="width: 10%;">
          <template #body="slotProps">
            {{ boardGameInstanceDetails[slotProps.data.boardGameInstanceId]?.boardGameName 
               || slotProps.data.boardGameInstanceId }}
          </template>
        </Column>

        <Column header="Borrower" style="width: 15%;">
          <template #body="slotProps">
            {{ lendingDetails[slotProps.data.userAccountId]?.name || slotProps.data.userAccountId }}
          </template>
        </Column>

        <!-- CLICKABLE Borrower Email COLUMN -->
        <Column header="Borrower Email" style="width: 20%;">
          <template #body="slotProps">
            <!-- Only render a mailto link if we actually have an email -->
            <a v-if="lendingDetails[slotProps.data.userAccountId]?.email"
               :href="generateMailToLink(
                        lendingDetails[slotProps.data.userAccountId].email, 
                        boardGameInstanceDetails[slotProps.data.boardGameInstanceId]?.boardGameName
                      )"
            >
              {{ lendingDetails[slotProps.data.userAccountId].email }}
            </a>
            <span v-else>N/A</span>
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
        <!-- GAME NAME COLUMN -->
        <Column header="Game" style="width: 15%;">
          <template #body="slotProps">
            {{ boardGameInstanceDetails[slotProps.data.boardGameInstanceId]?.boardGameName 
               || slotProps.data.boardGameInstanceId }}
          </template>
        </Column>
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
      lendingDetails: {},           // Maps borrower ID to an object containing name and email
      boardGameInstanceDetails: {}  // Maps boardGameInstanceId to board game instance info
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
      // Fetch lending history if user is a game owner
      if (user.gameOwnerRoleId) {
        const lendingRes = await api.get(`/users/${user.gameOwnerRoleId}/lending-history`)
        this.lendingHistory = lendingRes.data
        this.fetchLendingNames()
      }

      // Fetch borrowing history
      const borrowingRes = await api.get(`/borrowRequests/${user.userAccountId}/borrowing-history`)
      this.borrowingHistory = borrowingRes.data

      // Gather all records and fetch board game instance details
      this.fetchBoardGameInstances([...this.lendingHistory, ...this.borrowingHistory])
    } catch (error) {
      console.error(error)
    }
  },
  methods: {
    // Fetch name & email for all borrowers in lendingHistory
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
    },

    // Fetch instance details for all boardGameInstanceIds in both lending + borrowing
    async fetchBoardGameInstances(records) {
      const instanceIds = [...new Set(records.map(item => item.boardGameInstanceId))]
      try {
        const promises = instanceIds.map(id => api.get(`/boardgameinstances/${id}`))
        const responses = await Promise.all(promises)
        responses.forEach(response => {
          const instanceData = response.data
          // Key by the same ID used in our data (boardGameInstanceId)
          this.boardGameInstanceDetails[instanceData.individualGameId] = instanceData
        })
      } catch (error) {
        console.error('Error fetching board game instances:', error)
      }
    },

    // Build a mailto link: "mailto:<email>?subject=<boardGameName>"
    generateMailToLink(email, boardGameName) {
      const subject = encodeURIComponent(boardGameName || '')
      return `mailto:${email}?subject=${subject}`
    }
  }
}
</script>
