import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Account from '../views/Account.vue'
import Games from '../views/Games.vue'
import Events from '../views/Events.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import History from '../views/History.vue'
import BoardGameInstance from '../views/BoardGameInstance.vue'  // <-- Import here

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/account', name: 'Account', component: Account },
  { path: '/games', name: 'Games', component: Games },
  { path: '/events', name: 'Events', component: Events },
  { path: '/history', name: 'History', component: History },
  { 
    path: '/boardgameinstances/:boardGameId', 
    name: 'BoardGameInstance', 
    component: BoardGameInstance,
    props: true,
  },  // <-- New route with parameter
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Optional authentication guard here...

export default router

