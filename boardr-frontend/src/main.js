import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config'
import Aura from '@primevue/themes/aura'
import ToastService from 'primevue/toastservice' // Add this
import 'primeicons/primeicons.css'
import './assets/main.css'

const app = createApp(App)
app.use(PrimeVue, { theme: { preset: Aura } })
app.use(ToastService) // Add this
app.use(router)
app.mount('#app')