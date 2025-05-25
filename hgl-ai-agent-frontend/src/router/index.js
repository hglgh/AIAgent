import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoveMasterView from '../views/LoveMasterView.vue'
import ManusView from '../views/ManusView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/love-master',
      name: 'love-master',
      component: LoveMasterView
    },
    {
      path: '/manus',
      name: 'manus',
      component: ManusView
    }
  ]
})

export default router 