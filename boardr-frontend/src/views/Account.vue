<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">My Account</h1>
    <table class="mb-4" style="width: auto; margin-left: auto;">
      <tbody>
        <tr>
          <td style="padding-right: 24px;">
            <Button
              label="History"
              icon="pi pi-history"
              class="bg-blue-600 hover:bg-blue-700"
              @click="goToHistory"
            />
          </td>
          <td style="padding-right: 24px;">
            <Button
              label="Add Game Instance"
              icon="pi pi-plus"
              class="bg-blue-600 hover:bg-blue-700"
              @click="showAddGameDialog = true"
            />
          </td>
          <td>
            <Button
              :label="isGameOwner ? 'Switch to Player' : 'Switch to Game Owner'"
              :icon="isGameOwner ? 'pi pi-user' : 'pi pi-briefcase'"
              class="bg-purple-600 hover:bg-purple-700"
              @click="toggleView"
            />
          </td>
        </tr>
      </tbody>
    </table>
    <Card v-if="user" class="mb-6">
      <template #title>
        <h2 class="text-xl font-semibold">{{ user.name }}</h2>
      </template>
      <template #content>
        <p><strong>Email:</strong> {{ user.email }}</p>
        <p><strong>Account Type:</strong> {{ user.gameOwnerRoleId ? 'Game Owner' : 'Player' }}</p>
        <p><strong>Account ID:</strong> {{ user.userAccountId }}</p>
        <p v-if="user.gameOwnerRoleId"><strong>Game Owner ID:</strong> {{ user.gameOwnerRoleId }}</p>
      </template>
    </Card>

    <!-- Owned Games (Game Owners Only) -->
    <div v-if="isGameOwner && user?.gameOwnerRoleId" class="mb-6">
      <h2 class="text-2xl font-semibold mb-4">My Game Instances</h2>
      <DataTable :value="ownedGames" class="p-datatable-sm">
        <Column field="individualGameId" header="Game Instance ID" />
        <Column field="boardGameName" header="Title" />
        <Column field="condition" header="Condition" />
        <Column field="available" header="Status">
          <template #body="slotProps">
            {{ slotProps.data.available ? 'Available' : 'Not Available' }}
          </template>
        </Column>
        <!-- New column for update action -->
        <Column header="Update Condition">
          <template #body="slotProps">
            <Button
              label="Update"
              icon="pi pi-pencil"
              class="p-button-sm"
              @click="openUpdateModal(slotProps.data)"
            />
          </template>
        </Column>
        <Column header="View Requests">
          <template #body="slotProps">
            <Button
              icon="pi pi-eye"
              class="p-button-text"
              @click="openRequestsModal(slotProps.data.individualGameId)"
            />
          </template>
        </Column>
        <!-- New column for delete action -->
        <Column header="Delete">
          <template #body="slotProps">
            <Button
              label="Delete"
              icon="pi pi-trash"
              class="p-button-sm p-button-danger"
              @click="deleteGameInstance(slotProps.data)"
            />
          </template>
        </Column>

      </DataTable>
    </div>

    <!-- Created Event History -->
    <div class="mb-6">
      <h2 class="text-2xl font-semibold mb-4">Created Events</h2>
      <DataTable :value="participatedEvents" class="p-datatable-sm">
        <Column field="eventId" header="Event ID" />
        <Column field="description" header="Description" />
        <Column field="eventDate" header="Date">
          <template #body="slotProps">
            {{ formatDate(slotProps.data.eventDate) }}
          </template>
        </Column>
        <Column header="Date" style="width: 15%">
          <template #body="slotProps">
            {{ formatDate(slotProps.data.eventDate) }}
          </template>
        </Column>
        <Column header="Time" style="width: 10%">
          <template #body="slotProps">
            {{ formatTime(slotProps.data.eventTime) }}
          </template>
        </Column>
        <Column field="location" header="Location" />
        <Column header="View Details">
          <template #body="slotProps">
            <Button
              icon="pi pi-info-circle"
              class="p-button-text"
              @click="openEventDetailsModal(slotProps.data)"
            />
          </template>
        </Column>
        <!-- NEW: Update Event Column -->
        <Column header="Update" style="width: 10%">
          <template #body="slotProps">
            <Button
              label="Update"
              icon="pi pi-pencil"
              class="p-button-sm"
              @click="openEventUpdateModal(slotProps.data)"
            />
          </template>
        </Column>
      </DataTable>
    </div>

    <!-- Registered Events -->
    <div class="mb-6">
      <h2 class="text-2xl font-semibold mb-4">Registered Events</h2>
      <DataTable :value="registeredEvents" class="p-datatable-sm">
        <Column field="eventId" header="Event ID" />
        <Column field="description" header="Description" />
        <Column field="eventDate" header="Date">
          <template #body="slotProps">
            {{ formatDate(slotProps.data.eventDate) }}
          </template>
        </Column>
        <Column header="Date" style="width: 15%">
          <template #body="slotProps">
            {{ formatDate(slotProps.data.eventDate) }}
          </template>
        </Column>
        <Column header="Time" style="width: 10%">
          <template #body="slotProps">
            {{ formatTime(slotProps.data.eventTime) }}
          </template>
        </Column>
        <Column field="location" header="Location" />
        <Column header="View Details">
          <template #body="slotProps">
            <Button
              icon="pi pi-info-circle"
              class="p-button-text"
              @click="openEventDetailsModal(slotProps.data)"
            />
          </template>
        </Column>
      </DataTable>
    </div>

    <!-- Add Game Dialog -->
    <Dialog v-model:visible="showAddGameDialog" header="Add a Game Instance" :style="{ width: '30rem' }">
      <div class="space-y-4">
        <div>
          <label for="gameName" class="block text-sm font-medium">Game Title</label>
          <Dropdown
            id="gameName"
            v-model="selectedGame"
            :options="boardGames"
            optionLabel="name"
            placeholder="Select a title"
            class="w-full"
            :filter="true"
          />
        </div>
        <div>
          <label for="condition" class="block text-sm font-medium">Condition</label>
          <InputText id="condition" v-model="newGame.condition" class="w-full" />
        </div>
      </div>
      <template #footer>
        <Button label="Cancel" class="p-button-text" @click="showAddGameDialog = false" />
        <Button label="Add" class="bg-blue-600 hover:bg-blue-700" @click="addGame" />
      </template>
    </Dialog>

    <!-- Update Game Condition Dialog -->
    <Dialog v-model:visible="showUpdateModal" header="Update Game Condition" :style="{ width: '30rem' }">
      <div class="space-y-4">
        <div>
          <label for="updateCondition" class="block text-sm font-medium">New Condition</label>
          <InputText id="updateCondition" v-model="updateCondition" class="w-full" />
        </div>
      </div>
      <template #footer>
        <Button label="Cancel" class="p-button-text" @click="closeUpdateModal" />
        <Button label="Update" class="bg-blue-600 hover:bg-blue-700" @click="updateGameCondition" />
      </template>
    </Dialog>

    <!-- Requests Modal -->
    <Dialog
      v-model:visible="showRequestsModal"
      header="Requests"
      :style="{ width: '30rem' }"
      :modal="true"
    >
      <div v-if="borrowRequests.length === 0" class="p-4">
        <p>No pending requests for this game.</p>
      </div>
      <div v-else class="p-4">
        <p class="mb-4">The following users have made requests:</p>
        <div v-for="request in borrowRequests" :key="request.id" class="flex justify-between items-center mb-2">
          <div>
            <p>{{ request.borrowerName }}</p>
            <p>
              {{ formatDate(request.requestDate) }} - {{ formatDate(request.returnDate) }}
            </p>
          </div>
          <div>
            <Button
              label="Approve"
              class="p-button-sm"
              @click="approveRequest(request.id)"
            />
            <Button
              label="Remove"
              class="p-button-sm remove-button"
              @click="removeRequest(request.id)"
            />
          </div>
        </div>
      </div>
    </Dialog>

    <!-- Event Details Modal -->
    <Dialog
      v-model:visible="showEventDetailsModal"
      header="Event Details"
      :style="{ width: '30rem' }"
      :modal="true"
    >
      <div v-if="selectedEvent" class="p-4">
        <!-- Board Game -->
        <div class="mb-4">
          <label class="event-details-label">Board Game</label>
          <div class="event-details-value">
            {{ selectedEvent.gameName || 'N/A' }}
          </div>
        </div>

        <!-- Date and Time (Side by Side) -->
        <div class="flex justify-between mb-4">
          <div class="w-1/2 mr-2">
            <label class="event-details-label">Date</label>
            <div class="event-details-value">
              {{ formatEventDate(selectedEvent.eventDate) }}
            </div>
          </div>
          <div class="w-1/2 ml-2">
            <label class="event-details-label">Time</label>
            <div class="event-details-value">
              {{ formatEventTime(selectedEvent.eventTime) }}
            </div>
          </div>
        </div>

        <!-- Location and Max Participants (Side by Side) -->
        <div class="flex justify-between mb-4">
          <div class="w-1/2 mr-2">
            <label class="event-details-label">Location</label>
            <div class="event-details-value">
              {{ selectedEvent.location || 'N/A' }}
            </div>
          </div>
          <div class="w-1/2 ml-2">
            <label class="event-details-label">Max Participants</label>
            <div class="event-details-value">
              {{ selectedEvent.maxParticipants || 'N/A' }}
            </div>
          </div>
        </div>

        <!-- Description -->
        <div class="mb-4">
          <label class="event-details-label">Description*</label>
          <div class="event-details-value event-details-description">
            {{ selectedEvent.description || 'N/A' }}
          </div>
        </div>
      </div>
    </Dialog>

    <!-- NEW: Event Update Dialog -->
    <Dialog v-model:visible="showEventUpdateDialog" header="Update Event Details" :style="{ width: '30rem' }">
      <div class="space-y-4">
        <div>
          <label for="updateDate" class="block text-sm font-medium">Date (YYYYMMDD)</label>
          <InputText id="updateDate" v-model="eventUpdate.eventDate" class="w-full" />
        </div>
        <div>
          <label for="updateTime" class="block text-sm font-medium">Time (HHMM)</label>
          <InputText id="updateTime" v-model="eventUpdate.eventTime" class="w-full" />
        </div>
        <div>
          <label for="updateLocation" class="block text-sm font-medium">Location</label>
          <InputText id="updateLocation" v-model="eventUpdate.location" class="w-full" />
        </div>
        <div>
          <label for="updateDescription" class="block text-sm font-medium">Description</label>
          <InputText id="updateDescription" v-model="eventUpdate.description" class="w-full" />
        </div>
      </div>
      <template #footer>
        <Button label="Cancel" class="p-button-text" @click="closeEventUpdateDialog" />
        <Button label="Update" class="bg-blue-600 hover:bg-blue-700" @click="updateEventDetails" />
      </template>
    </Dialog>
  </div>
</template>

<script>
import Card from 'primevue/card'
import Button from 'primevue/button'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Dropdown from 'primevue/dropdown'
import GameCard from '../components/GameCard.vue'
import api from '../services/api'

export default {
  name: 'Account',
  components: { Card, Button, DataTable, Column, Dialog, InputText, Dropdown, GameCard },
  data() {
    return {
      user: null,
      ownedGames: [],
      participatedEvents: [],
      registeredEvents: [],
      lendingHistory: [],
      showAddGameDialog: false,
      showRequestsModal: false,
      borrowRequests: [],
      selectedGameId: null,
      newGame: { name: '', condition: '' },
      isGameOwner: true, // Default to Game Owner view
      boardGames: [], // List of available board games
      borrowerDetails: {}, // to store borrower's details
      selectedGame: null,
      // For updating game instance condition
      showUpdateModal: false,
      updateCondition: '',
      updateInstanceId: null,
      // For event details modal
      showEventDetailsModal: false,
      selectedEvent: null,
      // NEW: For event update dialog
      showEventUpdateDialog: false,
      eventUpdate: {
        eventDate: '',
        eventTime: '',
        location: '',
        description: ''
      },
      // For event registration dialog
      showRegisterConfirmDialog: false,
      selectedEventDetails: null,
      organizerDetails: {},
      boardGameInstanceDetails: {},
      searchQuery: '',
      events: [],
      originalEvents: []
    }
  },
  created() {
    this.loadUserData()
    this.loadBoardGames()
  },
  methods: {
    async loadUserData() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.userAccountId) {
        this.$router.push('/login')
        return
      }
      this.user = user

      if (user.gameOwnerRoleId) {
        const [ownedRes, lendingRes] = await Promise.all([
          api.get(`/users/${user.gameOwnerRoleId}/owned-games`),
          api.get(`/users/${user.gameOwnerRoleId}/lending-history`)
        ])
        this.ownedGames = ownedRes.data
        this.lendingHistory = lendingRes.data
      }

      const [eventsRes, registrationsRes] = await Promise.all([
        api.get('/events'),
        api.get('/registrations')
      ])

      // Filter events where user is the organizer
      this.participatedEvents = eventsRes.data.filter(e => e.organizerId === user.userAccountId)

      // Get registered events by filtering registrations for this user
      const userRegistrations = registrationsRes.data.filter(r => r.userId === user.userAccountId)
      // Map registration data to event data
      this.registeredEvents = userRegistrations.map(reg => {
        const event = eventsRes.data.find(e => e.eventId === reg.eventId)
        return event
      }).filter(Boolean) // Remove any undefined events
    },
    formatDate(date) {
      const dateStr = String(date);
      if (dateStr.length === 8) {
        return `${dateStr.slice(0, 4)}-${dateStr.slice(4, 6)}-${dateStr.slice(6, 8)}`;
      }
      return date;
    },
    formatTime(time) {
      const timeStr = String(time).padStart(4, '0');
      if (timeStr.length === 4) {
        return `${timeStr.slice(0, 2)}:${timeStr.slice(2, 4)}`;
      }
      return time;
    },
    formatEventDate(date) {
      if (!date) return 'N/A';
      const dateStr = String(date);
      if (dateStr.length === 8) {
        const monthNum = parseInt(dateStr.slice(4, 6), 10);
        const day = parseInt(dateStr.slice(6, 8), 10);
        const months = [
          'January', 'February', 'March', 'April', 'May', 'June',
          'July', 'August', 'September', 'October', 'November', 'December'
        ];
        const monthName = months[monthNum - 1] || 'Unknown';
        return `${monthName} ${day}`;
      }
      return dateStr;
    },
    formatEventTime(time) {
      if (!time) return 'N/A';
      const timeStr = String(time).padStart(4, '0');
      if (timeStr.length === 4) {
        const hour = parseInt(timeStr.slice(0, 2), 10);
        const minute = timeStr.slice(2, 4);
        return `${hour}:${minute}`;
      }
      return time;
    },
    async addGame() {
      try {
        // Check if a game is selected
        if (!this.selectedGame) {
          this.$toast.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Please select a game from the dropdown.',
            life: 3000,
          })
          return
        }

        const instance = {
          condition: this.newGame.condition,
          boardGameId: this.selectedGame.gameId, // Use the ID directly from the selected object
          gameOwnerId: this.user.gameOwnerRoleId,
        }
        
        await api.post('/boardgameinstances', instance)
        
        this.$toast.add({
          severity: 'success',
          summary: 'Game Added',
          detail: `${this.newGame.name} added to your collection!`,
          life: 3000,
        })
        
        this.showAddGameDialog = false
        this.newGame = { name: '', condition: '' }
        this.loadUserData() // Refresh owned games
      } catch (error) {
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to add game.',
          life: 3000,
        })
        console.error(error)
      }
    },
    goToHistory() {
      this.$router.push('/history')
    },
    toggleView() {
      this.isGameOwner = !this.isGameOwner
    },
    async loadBoardGames() {
      try {
        const response = await api.get('/boardgames')
        this.boardGames = response.data
      } catch (error) {
        console.error('Failed to load board games:', error)
      }
    },
    async fetchBorrowerDetails(requests) {
      const borrowerIds = [...new Set(requests.map(request => request.userAccountId))];
      try {
        const promises = borrowerIds.map(id => api.get(`/users/${id}`));
        const responses = await Promise.all(promises);
        responses.forEach(response => {
          const user = response.data;
          this.borrowerDetails[user.userAccountId] = {
            name: user.name,
          };
        });
      } catch (error) {
        console.error('Error fetching borrower details:', error);
      }
    },
    // Method to fetch board game details
    async fetchBoardGameDetails(boardGameInstanceId) {
      try {
        const response = await api.get(`/boardgameinstances/${boardGameInstanceId}`);
        const boardGameInstance = response.data;
        // Check if boardGameId is present and fetch the board game name
        if (boardGameInstance.boardGameId) {
          const boardGameResponse = await api.get(`/boardgames/${boardGameInstance.boardGameId}`);
          return boardGameResponse.data.name || 'Unknown';
        }
        return 'Unknown'; // Fallback if boardGameId is missing
      } catch (error) {
        console.error('Error fetching board game details:', error);
        return 'N/A';
      }
    },
    async openRequestsModal(gameId) {
      try {
        this.selectedGameId = gameId;
        const response = await api.get(`/borrowRequests/boardgameinstance/${gameId}`);
        console.log('Borrow Requests Response:', response.data);
        
        const pendingRequests = response.data.filter(request => request.requestStatus === 'Pending');

        // Fetch borrower details using method above 
        await this.fetchBorrowerDetails(pendingRequests);

        // Map requests to include borrower names from borrowerDetails
        this.borrowRequests = pendingRequests.map(request => ({
          id: request.id,
          borrowerName: this.borrowerDetails[request.userAccountId]?.name || 'Unknown',
          requestDate: request.requestDate,
          returnDate: request.returnDate,
          userAccountId: request.userAccountId, // Keep this for reference
        }));
        console.log('Filtered Borrow Requests:', this.borrowRequests);
        this.showRequestsModal = true;
      } catch (error) {
        console.error('Failed to load borrow requests:', error);
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load borrow requests.',
          life: 3000,
        });
      }
    },
    async approveRequest(borrowRequestId) {
      try {
        await api.put(`/borrowRequests/${borrowRequestId}/status`, { requestStatus: 'Accepted' });
        this.$toast.add({
          severity: 'success',
          summary: 'Request Approved',
          detail: 'Borrow request has been approved.',
          life: 3000,
        });
        await this.openRequestsModal(this.selectedGameId);
        await this.loadUserData();
      } catch (error) {
        console.error('Failed to approve request:', error);
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to approve request.',
          life: 3000,
        });
      }
    },
    async removeRequest(borrowRequestId) {
      try {
        await api.delete(`/borrowRequests/${borrowRequestId}`);
        this.$toast.add({
          severity: 'success',
          summary: 'Request Removed',
          detail: 'Borrow request has been removed.',
          life: 3000,
        });
        await this.openRequestsModal(this.selectedGameId);
      } catch (error) {
        console.error('Failed to remove request:', error);
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to remove request.',
          life: 3000,
        });
      }
    },
    openUpdateModal(instance) {
      // Set the current instance's ID and current condition in the update modal
      this.updateInstanceId = instance.individualGameId
      this.updateCondition = instance.condition
      this.showUpdateModal = true
    },
    async updateGameCondition() {
      if (!this.updateCondition.trim()) {
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Condition cannot be empty.',
          life: 3000,
        })
        return
      }
      try {
        // Call the API to update the condition for the selected board game instance
        await api.put(`/boardgameinstances/${this.updateInstanceId}/condition`, {
          condition: this.updateCondition
        })
        this.$toast.add({
          severity: 'success',
          summary: 'Updated',
          detail: 'Game condition updated successfully.',
          life: 3000,
        })
        this.closeUpdateModal()
        this.loadUserData() // Refresh owned games
      } catch (error) {
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to update game condition.',
          life: 3000,
        })
        console.error(error)
      }
    },
    closeUpdateModal() {
      this.showUpdateModal = false
      this.updateCondition = ''
      this.updateInstanceId = null
    },
    // Method to open the event details modal and fetch board game name
    async openEventDetailsModal(event) {
      this.selectedEvent = { ...event }; // Create a copy to avoid mutating original data
      if (event.boardGameInstanceId) {
        this.selectedEvent.gameName = await this.fetchBoardGameDetails(event.boardGameInstanceId);
      } else {
        this.selectedEvent.gameName = 'N/A';
      }
      this.showEventDetailsModal = true;
    },
    // NEW: Open the event update dialog and pre-fill fields
    openEventUpdateModal(event) {
      this.selectedEvent = event;
      this.eventUpdate.eventDate = event.eventDate;
      this.eventUpdate.eventTime = event.eventTime;
      this.eventUpdate.location = event.location;
      this.eventUpdate.description = event.description;
      this.showEventUpdateDialog = true;
    },
    // NEW: Call the API to update event details using EventUpdateDTO
    async updateEventDetails() {
      try {
        const response = await api.put(`/events/${this.selectedEvent.eventId}/details`, this.eventUpdate);
        this.$toast.add({
          severity: 'success',
          summary: 'Event Updated',
          detail: 'Event details updated successfully.',
          life: 3000,
        });
        // Instant update: update the local participatedEvents array for the modified event
        const index = this.participatedEvents.findIndex(e => e.eventId === this.selectedEvent.eventId);
        if (index !== -1) {
          this.participatedEvents[index].eventDate = this.eventUpdate.eventDate;
          this.participatedEvents[index].eventTime = this.eventUpdate.eventTime;
          this.participatedEvents[index].location = this.eventUpdate.location;
          this.participatedEvents[index].description = this.eventUpdate.description;
        }
        this.showEventUpdateDialog = false;
      } catch (error) {
        this.$toast.add({
          severity: 'error',
          summary: 'Update Failed',
          detail: 'Failed to update event details.',
          life: 3000,
        });
        console.error(error);
      }
    },
    closeEventUpdateDialog() {
      this.showEventUpdateDialog = false;
    },
    openRegisterDialog(event) {
      this.selectedEvent = event;
      this.showRegisterConfirmDialog = true;
    },
    async registerForEvent() {
      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        if (!user.userAccountId) {
          this.$router.push('/login');
          return;
        }
        const registrationData = {
          userId: user.userAccountId,
          eventId: this.selectedEvent.eventId
        };
        await api.post('/registrations', registrationData);
        this.$toast.add({
          severity: 'success',
          summary: 'Registered',
          detail: 'You have been registered for the event!',
          life: 3000,
        });
      } catch (error) {
        console.error('Registration error:', error.response?.data);
        const errorMessage = error.response?.data?.errors?.[0] || 'Failed to register for the event. Please try again.';
        this.$toast.add({
          severity: 'error',
          summary: 'Registration Failed',
          detail: errorMessage,
          life: 5000,
        });
      }
      this.showRegisterConfirmDialog = false;
      this.selectedEvent = null;
    },
    async createEvent() {
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      if (!user.userAccountId) {
        this.$router.push('/login');
        return;
      }
      try {
        const eventData = {
          ...this.newEvent,
          organizerId: user.userAccountId,
        };
        await api.post('/events', eventData);
        this.$toast.add({
          severity: 'success',
          summary: 'Event Created',
          detail: 'Your event has been created!',
          life: 3000,
        });
        this.showCreateEventDialog = false;
        this.newEvent = {
          description: '',
          eventDate: '',
          eventTime: '',
          location: '',
          maxParticipants: 1,
          boardGameInstanceId: null,
        };
        const response = await api.get('/events');
        const futureEvents = this.filterFutureEvents(response.data);
        this.events = futureEvents;
        this.originalEvents = futureEvents;
        this.fetchOrganizerDetails();
        await this.fetchBoardGameInstanceDetails();
        if (this.$route.query.boardGameName) {
          this.searchQuery = this.$route.query.boardGameName;
          this.searchEvents();
        }
      } catch (error) {
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to create event.',
          life: 3000,
        });
        console.error(error);
      }
    },
    searchEvents() {
      if (!this.searchQuery.trim()) {
        this.events = this.filterFutureEvents(this.originalEvents);
      } else {
        const query = this.searchQuery.toLowerCase();
        this.events = this.filterFutureEvents(this.originalEvents).filter(event => {
          const instance = this.boardGameInstanceDetails[event.boardGameInstanceId];
          return instance && instance.boardGameName.toLowerCase().includes(query);
        });
      }
    },
    resetSearch() {
      this.searchQuery = '';
      this.events = this.filterFutureEvents(this.originalEvents);
    },
    async deleteGameInstance(instance) {
      try {
        // Check if the instance is available
        if (!instance.available) {
          this.$toast.add({
            severity: 'error',
            summary: 'Cannot Delete',
            detail: 'Cannot delete a game instance that is currently borrowed.',
            life: 3000,
          });
          return;
        }

        // Check if the instance is used in any events
        const eventsResponse = await api.get(`/events`);
        const instanceEvents = eventsResponse.data.filter(event => 
          event.boardGameInstanceId === instance.individualGameId
        );
        if (instanceEvents.length > 0) {
          this.$toast.add({
            severity: 'error',
            summary: 'Cannot Delete',
            detail: 'Cannot delete a game instance that is used in an event.',
            life: 3000,
          });
          return;
        }

        // Proceed with deletion
        await api.delete(`/boardgameinstances/${instance.individualGameId}`);
        this.$toast.add({
          severity: 'success',
          summary: 'Deleted',
          detail: 'Game instance deleted successfully.',
          life: 3000,
        });
        await this.loadUserData(); // Refresh owned games
      } catch (error) {
        console.error('Failed to delete game instance:', error);
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to delete game instance.',
          life: 3000,
        });
      }
    }

  },
}
</script>

<style scoped>
.remove-button {
  background-color: #dc2626;
  border: none;
  color: white;
}

.remove-button:hover {
  background-color: #b91c1c;
}

/* Style for the modal content */
.p-dialog .p-dialog-content {
  padding: 1rem;
}

/* Style for labels */
.event-details-label {
  font-size: 0.75rem; /* Small font size */
  font-weight: 600; /* Bold */
  text-transform: uppercase; /* Uppercase */
  color: #1f2937; /* Dark gray */
  margin-bottom: 0.25rem; /* Space below label */
}

/* Style for values (input-like boxes) */
.event-details-value {
  background-color: #f9fafb; /* Light gray background */
  border: 1px solid #e5e7eb; /* Light gray border */
  border-radius: 0.375rem; /* Rounded corners */
  padding: 0.5rem; /* Padding inside the box */
  color: #6b7280; /* Gray text */
  font-size: 0.875rem; /* Slightly smaller font */
}

/* Specific style for the description box (taller) */
.event-details-description {
  min-height: 4rem; /* Taller box for description */
  line-height: 1.5; /* Better readability for longer text */
}
</style>
