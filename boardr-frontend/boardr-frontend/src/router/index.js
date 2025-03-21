import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Account from '../views/Account.vue'
import Games from '../views/Games.vue'
import Events from '../views/Events.vue'
import Login from '../components/Login.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/account', name: 'Account', component: Account },
  { path: '/games', name: 'Games', component: Games },
  { path: '/events', name: 'Events', component: Events },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router