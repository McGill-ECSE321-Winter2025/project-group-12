<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">
      Browse Available Events
      <Button
        label="Create Event"
        icon="pi pi-plus"
        class="mb-6 bg-blue-600 hover:bg-blue-700"
        @click="showCreateEventDialog = true"
      />
    </h1>

    <!-- Search bar -->
    <h2>
      <table class="mb-4" style="width: auto; margin-right: auto;">
        <tbody>
          <tr>
            <td style="padding-right: 12px;">
              <InputText
                v-model="searchQuery"
                placeholder="Search event by game name..."
                style="width: 310px;"
              />
            </td>
            <td style="padding-right: 12px;">
              <Button label="Search" class="ml-2" @click="searchEvents" />
            </td>
            <td>
              <Button label="Reset" class="ml-2" @click="searchQuery = ''; searchEvents()" />
            </td>
          </tr>
        </tbody>
      </table>
    </h2>

    <!-- DataTable for events -->
    <DataTable 
      :value="events" 
      class="p-datatable-sm" 
      responsiveLayout="scroll" 
      sortField="eventDate" 
      :sortOrder="1">
      <!-- Game Name Column with Information Icon -->
      <Column header="Game Name" style="width: 20%">
        <template #body="slotProps">
          {{ boardGameInstanceDetails[slotProps.data.boardGameInstanceId]?.boardGameName || slotProps.data.boardGameInstanceId }}
          <Button 
            icon="pi pi-info-circle"
            class="p-button-icon-only p-button-text ml-2" 
            @click="openEventDetails(slotProps.data)" />
        </template>
      </Column>

      <!-- Date Column with Sorting -->
      <Column header="Date" style="width: 10%" sortable sortField="eventDate">
        <template #body="slotProps">
          {{ formatDate(slotProps.data.eventDate) }}
        </template>
      </Column>

      <!-- Time Column -->
      <Column header="Time" style="width: 5%">
        <template #body="slotProps">
          {{ formatTime(slotProps.data.eventTime) }}
        </template>
      </Column>

      <!-- Location Column -->
      <Column field="location" header="Location" style="width: 15%"></Column>

      <!-- Organizer Column -->
      <Column header="Organizer" style="width: 10%">
        <template #body="slotProps">
          {{ organizerDetails[slotProps.data.organizerId]?.name || slotProps.data.organizerId }}
        </template>
      </Column>

      <!-- Organizer Email Column -->
      <Column header="Organizer Email" style="width: 15%">
        <template #body="slotProps">
          <a :href="'mailto:' + (organizerDetails[slotProps.data.organizerId]?.email || '') +
                    '?subject=' + encodeURIComponent(
                      ('[Boardr Event] ' +
                      (boardGameInstanceDetails[slotProps.data.boardGameInstanceId]?.boardGameName || slotProps.data.boardGameInstanceId))
                      + ' @ ' +
                      slotProps.data.location +
                      ' ' +
                      formatDate(slotProps.data.eventDate) +
                      ' ' +
                      formatTime(slotProps.data.eventTime)
                    )">
            {{ organizerDetails[slotProps.data.organizerId]?.email || 'N/A' }}
          </a>
        </template>
      </Column>

      <!-- Participate Column -->
      <Column header="Participate" style="width: 10%">
        <template #body="slotProps">
          <Button 
          :label="'Register'"
          :class="'bg-green-500 hover:bg-green-600 text-white'"
          @click="openRegisterDialog(slotProps.data, true)" />
        </template>
      </Column>
      <Column header="Opt out" style="width: 10%">
        <template #body="slotProps">
          <Button 
          :label="'Unregister'"
          :class="'p-button-sm p-button-danger text-white'"
          @click="openRegisterDialog(slotProps.data, false)" />
        </template>
      </Column>
    </DataTable>

    <!-- Create Event Dialog -->
    <Dialog v-model:visible="showCreateEventDialog" header="Create Event" :style="{ width: '30rem' }">
      <div class="space-y-4">
        <div>
          <label for="description" class="block text-sm font-medium">Description</label>
          <InputText id="description" v-model="newEvent.description" class="w-full" />
        </div>
        <div>
          <label for="date" class="block text-sm font-medium">Date (YYYYMMDD)</label>
          <InputText id="date" v-model="newEvent.eventDate" class="w-full" />
        </div>
        <div>
          <label for="time" class="block text-sm font-medium">Time (HHMM)</label>
          <InputText id="time" v-model="newEvent.eventTime" class="w-full" />
        </div>
        <div>
          <label for="location" class="block text-sm font-medium">Location</label>
          <InputText id="location" v-model="newEvent.location" class="w-full" />
        </div>
        <div>
          <label for="maxParticipants" class="block text-sm font-medium">Max Participants</label>
          <InputText id="maxParticipants" v-model="newEvent.maxParticipants" type="number" class="w-full" />
        </div>
        <div>
          <label for="gameId" class="block text-sm font-medium">Game Instance ID</label>
          <InputText id="gameId" v-model="newEvent.boardGameInstanceId" type="number" class="w-full" />
        </div>
      </div>
      <template #footer>
        <Button label="Cancel" class="p-button-text" @click="showCreateEventDialog = false" />
        <Button label="Create" class="bg-blue-600 hover:bg-blue-700" @click="createEvent" />
      </template>
    </Dialog>

    <!-- Registration Confirmation Dialog -->
    <Dialog v-model:visible="showRegisterConfirmDialog" header="Confirm Registration" :style="{ width: '20rem' }">
      <p class="mb-4">Are you sure?</p>
      <template #footer>
  <Button label="Cancel" class="p-button-text" @click="showRegisterConfirmDialog = false" />
  <Button
    :label="'Register'"
    :class="'bg-green-500 hover:bg-green-600'"
    @click="registerForEvent()"
  />
</template>
    </Dialog>

        <!-- Registration Confirmation Dialog -->
      <Dialog v-model:visible="showUnRegisterConfirmDialog" header="Confirm Opt-Out" :style="{ width: '20rem' }">
      <p class="mb-4">Are you sure?</p>
      <template #footer>
  <Button label="Cancel" class="p-button-text" @click="showUnRegisterConfirmDialog = false" />
  <Button
    :label="'Unregister'"
    :class="'p-button-sm p-button-danger'"
    @click="unregisterForEvent()"
  />
</template>
    </Dialog>

    <!-- New: Event Details Dialog -->
    <Dialog v-model:visible="showEventDetailsDialog" header="Event Details" :style="{ width: '30rem' }">
      <div class="p-4">
        <h3 class="text-xl font-bold mb-2">{{ selectedEventDetails?.gameName }}</h3>
        <p>{{ selectedEventDetails?.description }}</p>
      </div>
    </Dialog>
  </div>
</template>

<script>
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import api from '../services/api'

export default {
  name: 'Events',
  components: { Button, Dialog, InputText, DataTable, Column },
  data() {
    return {
      events: [],
      originalEvents: [],
      searchQuery: '',
      showCreateEventDialog: false,
      showRegisterConfirmDialog: false,
      showUnRegisterConfirmDialog: false,
      showEventDetailsDialog: false,   // New property for dialog visibility
      selectedEvent: null,
      selectedEventDetails: null,      // New property for holding selected event details
      organizerDetails: {}, // Organizer info mapped by organizerId
      boardGameInstanceDetails: {}, // Mapped by boardGameInstanceId
      newEvent: {
        description: '',
        eventDate: '',
        eventTime: '',
        location: '',
        maxParticipants: 1,
        boardGameInstanceId: null,
      },
    }
  },
  async created() {
    try {
      // Fetch events from the backend
      const eventsResponse = await api.get('/events');
      // Filter out past events without deleting them from the system
      const allEvents = eventsResponse.data;
      const futureEvents = this.filterFutureEvents(allEvents);
      this.events = futureEvents;
      this.originalEvents = futureEvents;
      this.fetchOrganizerDetails();
      await this.fetchBoardGameInstanceDetails();

      if (this.$route.query.boardGameName) {
      this.searchQuery = this.$route.query.boardGameName;
      this.searchEvents();
    }
    } catch (error) {
      //this.$toast.add({
        //severity: 'error',
        //summary: 'Error',
        //detail: 'Failed to load events.',
        //life: 3000,
      //});
      console.error(error);
    }
  },
  methods: {
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
    // Filters events so that only events with a date/time not in the past are kept
    filterFutureEvents(events) {
      const now = new Date();
      return events.filter(event => {
        const dateStr = String(event.eventDate).padStart(8, '0');
        const timeStr = String(event.eventTime).padStart(4, '0');
        const dateFormatted = `${dateStr.slice(0, 4)}-${dateStr.slice(4,6)}-${dateStr.slice(6,8)}`;
        const timeFormatted = `${timeStr.slice(0, 2)}:${timeStr.slice(2,4)}:00`;
        const eventDateTime = new Date(`${dateFormatted}T${timeFormatted}`);
        return eventDateTime >= now;
      });
    },
    async fetchOrganizerDetails() {
      const organizerIds = [...new Set(this.events.map(event => event.organizerId))];
      try {
        const promises = organizerIds.map(id => api.get(`/users/${id}`));
        const responses = await Promise.all(promises);
        responses.forEach(response => {
          const user = response.data;
          this.organizerDetails[user.userAccountId] = {
            name: user.name,
            email: user.email,
          };
        });
      } catch (error) {
        console.error('Error fetching organizer details:', error);
      }
    },
    async fetchBoardGameInstanceDetails() {
      const instanceIds = [...new Set(this.events
                                        .map(event => event.boardGameInstanceId)
                                        .filter(id => id !== null))];
      try {
        const promises = instanceIds.map(id => api.get(`/boardgameinstances/${id}`));
        const responses = await Promise.all(promises);
        responses.forEach(response => {
          const instanceData = response.data;
          this.boardGameInstanceDetails[instanceData.individualGameId] = instanceData;
        });
      } catch (error) {
        console.error("Error fetching board game instance details:", error);
      }
    },
    async createEvent() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.userAccountId) {
        this.$router.push('/login')
        return
      }
      try {
        const eventData = {
          ...this.newEvent,
          organizerId: user.userAccountId,
        }
        await api.post('/events', eventData)
        this.$toast.add({
          severity: 'success',
          summary: 'Event Created',
          detail: 'Your event has been created!',
          life: 3000,
        })
        this.showCreateEventDialog = false
        this.newEvent = {
          description: '',
          eventDate: '',
          eventTime: '',
          location: '',
          maxParticipants: 1,
          boardGameInstanceId: null,
        }
        const response = await api.get('/events');
        // After fetching, filter out past events again
        const futureEvents = this.filterFutureEvents(response.data);
        this.events = futureEvents;
        this.originalEvents = futureEvents;
        this.fetchOrganizerDetails();
        //
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
    openRegisterDialog(event, isRegister) {
      this.selectedEvent = event
      if (isRegister){
      this.showRegisterConfirmDialog = true
      }else{
        this.showUnRegisterConfirmDialog = true
      }
    },
    async registerForEvent() {
      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user.userAccountId) {
          this.$router.push('/login')
          return
        }
        const registrationData = {
          userId: user.userAccountId,
          eventId: this.selectedEvent.eventId
        }
        await api.post('/registrations', registrationData)
        this.$toast.add({
          severity: 'success',
          summary: 'Registered',
          detail: 'You have been registered for the event!',
          life: 3000,
        })
      } catch (error) {
        console.error('Registration error:', error.response?.data)
        const errorMessage = error.response?.data?.errors?.[0] || 'Failed to register for the event. Please try again.'
        this.$toast.add({
          severity: 'error',
          summary: 'Registration Failed',
          detail: errorMessage,
          life: 5000,
        })
      }
      this.showRegisterConfirmDialog = false
      this.selectedEvent = null
    },
    async unregisterForEvent() {
    try {
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      if (!user.userAccountId) {
        this.$router.push('/login');
        return;
      }
      // Construct the URL with userId and eventId for the cancel endpoint
      const url = `/registrations/${user.userAccountId}/${this.selectedEvent.eventId}/cancel`;
      await api.post(url); // Make the POST request to cancel the registration
      this.$toast.add({
        severity: 'success',
        summary: 'Unregistered',
        detail: 'You have successfully unregistered for the event!',
        life: 3000,
      });
    } catch (error) {
      console.error('Unregistration error:', error.response?.data);
      const errorMessage = error.response?.data?.errors?.[0] || 'Failed to unregister for the event. Please try again.';
      this.$toast.add({
        severity: 'error',
        summary: 'Unregistration Failed',
        detail: errorMessage,
        life: 5000,
      });
    }
    this.showUnRegisterConfirmDialog = false;
    this.selectedEvent = null;
  },
    searchEvents() {
      if (!this.searchQuery.trim()) {
        this.events = this.filterFutureEvents(this.originalEvents)
      } else {
        const query = this.searchQuery.toLowerCase();
        this.events = this.filterFutureEvents(this.originalEvents).filter(event => {
          const instance = this.boardGameInstanceDetails[event.boardGameInstanceId];
          return instance && instance.boardGameName.toLowerCase().includes(query);
        });
      }
    },
    openEventDetails(eventData) {
      this.selectedEventDetails = {
        gameName: this.boardGameInstanceDetails[eventData.boardGameInstanceId]?.boardGameName || eventData.boardGameInstanceId,
        description: eventData.description,
      };
      this.showEventDetailsDialog = true;
    },
  },
}
</script>