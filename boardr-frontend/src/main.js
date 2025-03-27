import { createApp } from 'vue'
import { createPinia } from 'pinia' // Add Pinia
import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config'
import Aura from '@primevue/themes/aura'
import ToastService from 'primevue/toastservice'
import ConfirmationService from 'primevue/confirmationservice'
import 'primeicons/primeicons.css'
import './assets/main.css'

const app = createApp(App)
const pinia = createPinia()

app.use(PrimeVue, { 
    theme: {
        preset: Aura,
        options: {
            prefix: 'p',
            darkModeSelector: '.my-app-dark',
            cssLayer: false
        }
    } 
    
})
app.use(ToastService)
app.use(router)
app.use(pinia) // Add Pinia
app.use(ConfirmationService)

app.mount('#app')