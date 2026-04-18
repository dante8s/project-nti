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
{
  path: '/organizations/register',
  name: 'org-register',
  component: () => import('@/views/organization/OrgRegisterView.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/organizations/my',
  name: 'my-organizations',
  component: () => import('@/views/organization/OrgListView.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/organizations/:id',
  name: 'org-profile',
  component: () => import('@/views/organization/OrgProfileView.vue'),
  meta: { requiresAuth: true }
},
{
  path:'/mentorships',
  name:'/mentor-dashboard',
  component: () => import('@/views/mentorship/MentorDashboardView.vue'),
  meta: {requiresAuth: true}
},
// Mentor assign (admin)
{
  path: '/mentorships/assign',
  name: 'mentorship-assign',
  component: () => import('@/views/mentorship/AssignMentorView.vue'),
  meta: { requiresAuth: true }
},
{
  path:'/mentorships/:id',
  name:'mentorship-detail',
  component: () => import('@/views/mentorship/MentorshipDetailView.vue'),
  meta: {requiresAuth: true}
},
{
  path: '/organizations/:id/edit',
  name: 'org-edit',
  component: () => import('@/views/organization/OrgEditView.vue'),
  meta: { requiresAuth: true }
},
// Milestones
{
  path: '/milestones',
  name: 'milestones',
  component: () => import('@/views/milestone/MilestoneListView.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/milestones/create',
  name: 'milestone-create',
  component: () => import('@/views/milestone/MilestoneCreateView.vue'),
  meta: { requiresAuth: true }
},
// Organizations list (admin)
{
  path: '/organizations',
  name: 'organizations',
  component: () => import('@/views/organization/OrgListView.vue'),
  meta: { requiresAuth: true }
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
