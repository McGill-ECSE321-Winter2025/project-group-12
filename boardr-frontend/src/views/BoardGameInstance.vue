<template>
  <div class="py-6">
    <h1 class="text-3xl font-bold mb-6">
      Browse a
      <span style="color: #10B981;"> Board Game
      </span>
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
              @click="requestInstance(slotProps.data)"
            />
          </template>
        </Column>
  
      </DataTable>
    </div>
  </div>
</template>
  
<script>
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import api from '../services/api'
  
export default {
  name: 'BoardGameInstances',
  props: {
    boardGameId: {
      type: [Number, String],
      required: true,
    },
  },
  components: { DataTable, Column, Button },
  data() {
    return {
      boardGameInstances: [],
      boardGameName: '',
    }
  },
  async created() {
    try {
      // Call the endpoint to get all instances for the given board game id
      const response = await api.get(`/boardgameinstances/boardgame/${this.boardGameId}`)
      this.boardGameInstances = response.data;
      if (this.boardGameInstances.length > 0) {
        this.boardGameName = this.boardGameInstances[0].boardGameName;
      } else {
        this.boardGameName = 'Unknown';
      }
    } catch (error) {
      this.$toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to load board game instances.',
        life: 3000,
      });
      console.error(error);
    }
  },
  methods: {
    requestInstance(instance) {
      // Replace with your request logic; here we simply show an alert.
      alert(`Requesting board game instance for game "${instance.boardGameName}" with instance ID ${instance.individualGameId}`);
    },
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

</style>
