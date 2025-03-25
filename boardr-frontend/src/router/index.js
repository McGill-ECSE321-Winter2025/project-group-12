import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Account from '../views/Account.vue'
import Games from '../views/Games.vue'
import Events from '../views/Events.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

//might need to add auth here ex: meta: { requiresAuth: true },
const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/account', name: 'Account', component: Account },
  { path: '/games', name: 'Games', component: Games },
  { path: '/events', name: 'Events', component: Events },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

//auth?
// router.beforeEach((to, from, next) => {
//   const isAuthenticated = !!localStorage.getItem('userId')

//   if (to.meta.requiresAuth && !isAuthenticated) {
//     window.dispatchEvent(new CustomEvent('show-login-modal'))
//     next(false)
//   } else {
//     next()
//   }
// })

export default router