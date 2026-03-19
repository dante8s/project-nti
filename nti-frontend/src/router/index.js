import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () =>
        import('@/views/auth/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () =>
        import('@/views/auth/RegisterView.vue')
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () =>
        import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true }
    },
    {
  path: '/forgot-password',
  name: 'forgot-password',
  component: () =>
    import('@/views/auth/ForgotPasswordView.vue')
},
{
  path: '/reset-password',
  name: 'reset-password',
  component: () =>
    import('@/views/auth/ResetPasswordView.vue')
},
  ]
})

// Захист маршрутів
router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { name: 'login' }
  }
})

export default router