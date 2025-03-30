<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">History</h1>
    
    <!-- Lending History Table (For Game Owners Only) -->
    <div v-if="user && user.gameOwnerRoleId">
      <h2 class="text-2xl font-semibold mb-4">Lending History</h2>
      <DataTable :value="lendingHistory" class="p-datatable-sm" responsiveLayout="scroll">
        <!-- GAME NAME COLUMN -->
        <Column header="Game" style="width: 15%;">
          <template #body="slotProps">
            {{ boardGameInstanceDetails[slotProps.data.boardGameInstanceId]?.boardGameName ||
               slotProps.data.boardGameInstanceId }}
          </template>
        </Column>
  
        <!-- BORROWER NAME COLUMN -->
        <Column header="Borrower" style="width: 15%;">
          <template #body="slotProps">
            {{ lendingDetails[slotProps.data.userAccountId]?.name || slotProps.data.userAccountId }}
          </template>
        </Column>
  
        <!-- BORROWER EMAIL COLUMN -->
        <Column header="Borrower Email" style="width: 20%;">
          <template #body="slotProps">
            <a v-if="lendingDetails[slotProps.data.userAccountId]?.email"
               :href="generateMailToLink(
                         lendingDetails[slotProps.data.userAccountId].email, 
                         boardGameInstanceDetails[slotProps.data.boardGameInstanceId]?.boardGameName
                       )">
              {{ lendingDetails[slotProps.data.userAccountId].email }}
            </a>
            <span v-else>N/A</span>
          </template>
        </Column>
  
        <!-- REQUEST DATE COLUMN -->
        <Column field="requestDate" header="Request Date" style="width: 15%;" />
        <!-- RETURN DATE COLUMN -->
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
            {{ boardGameInstanceDetails[slotProps.data.boardGameInstanceId]?.boardGameName ||
               slotProps.data.boardGameInstanceId }}
          </template>
        </Column>
  
        <!-- LENDER NAME COLUMN -->
        <Column header="Lender Name" style="width: 15%;">
          <template #body="slotProps">
            {{ boardGameInstanceDetails[slotProps.data.boardGameInstanceId]?.ownerName || 'N/A' }}
          </template>
        </Column>
  
        <!-- LENDER EMAIL COLUMN -->
        <Column header="Lender Email" style="width: 20%;">
          <template #body="slotProps">
            <a v-if="boardGameInstanceDetails[slotProps.data.boardGameInstanceId]?.ownerEmail"
               :href="generateMailToLink(
                         boardGameInstanceDetails[slotProps.data.boardGameInstanceId].ownerEmail,
                         boardGameInstanceDetails[slotProps.data.boardGameInstanceId].boardGameName
                       )">
              {{ boardGameInstanceDetails[slotProps.data.boardGameInstanceId].ownerEmail }}
            </a>
            <span v-else>N/A</span>
          </template>
        </Column>
  
        <!-- REQUEST DATE COLUMN -->
        <Column field="requestDate" header="Request Date" style="width: 15%;" />
        <!-- RETURN DATE COLUMN -->
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
      lendingDetails: {},           // Stores borrower info: { [userAccountId]: { name, email } }
      boardGameInstanceDetails: {}, // Stores instance details: { [boardGameInstanceId]: {...} }
      allUsersMap: {}               // { [gameOwnerRoleId]: userObject }
    }
  },
  async created() {
    // 1) Make sure we have a logged-in user
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.userAccountId) {
      this.$router.push('/login')
      return
    }
    this.user = user

    try {
      // 2) If the user is a game owner, fetch the lending history
      if (user.gameOwnerRoleId) {
        const lendingRes = await api.get(`/users/${user.gameOwnerRoleId}/lending-history`)
        this.lendingHistory = lendingRes.data
        this.fetchLendingNames()
      }

      // 3) Fetch the borrowing history
      const borrowingRes = await api.get(`/borrowRequests/${user.userAccountId}/borrowing-history`)
      this.borrowingHistory = borrowingRes.data

      // 4) Fetch all users once (needed to map gameOwnerId -> user)
      await this.fetchAllUsers()

      // 5) Gather all records and fetch board game instance details
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

    // 1) Fetch all users, build a map keyed by each user's gameOwnerRoleId => user
    async fetchAllUsers() {
      try {
        const usersRes = await api.get('/users')
        const userList = usersRes.data
        userList.forEach(u => {
          // We'll store each user by their 'gameOwnerRoleId'
          this.allUsersMap[u.gameOwnerRoleId] = u
        })
      } catch (error) {
        console.error('Error fetching all users:', error)
      }
    },

    // 2) Fetch instance details for all boardGameInstanceIds in both lending + borrowing
    async fetchBoardGameInstances(records) {
      const instanceIds = [...new Set(records.map(item => item.boardGameInstanceId))]
      try {
        const promises = instanceIds.map(id => api.get(`/boardgameinstances/${id}`))
        const responses = await Promise.all(promises)
        responses.forEach(response => {
          const instanceData = response.data
          // Match the gameOwnerId from the instance to the user who has that gameOwnerRoleId
          const ownerUser = this.allUsersMap[instanceData.gameOwnerId]
          const ownerName = ownerUser ? ownerUser.name : null
          const ownerEmail = ownerUser ? ownerUser.email : null

          this.boardGameInstanceDetails[instanceData.individualGameId] = {
            ...instanceData,
            ownerName,
            ownerEmail
          }
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
