<template>
    <!-- <div>
        <h1>Дашборд</h1>
        <button @click="handleLogout">Вийти</button>
    </div> -->
    <div class="wrap">
    <div class="box">
      <h1>Welcome, {{ user?.name }}</h1>
      <p class="role-badge">{{ user?.role }}</p>

      <div class="nav-cards">

        <!-- MENTOR -->
        <div v-if="isMentor" class="nav-card" @click="router.push('/mentorships')">
          <h3>My Mentorships</h3>
          <p>View your active mentorships, consultation notes and milestones</p>
        </div>

        <!-- FIRM -->
        <div v-if="isFirm" class="nav-card" @click="router.push('/organizations/register')">
          <h3>Register Organization</h3>
          <p>Create your company profile in the NTI system</p>
        </div>

        <div
          v-for="org in myOrgs"
          :key="org.id"
          class="nav-card"
          @click="router.push(`/organizations/${org.id}`)"
        >
          <h3>{{ org.name }}</h3>
          <p>View and manage your organization profile and members</p>
        </div>

        <!-- ADMIN -->
        <div v-if="isAdmin" class="nav-card" @click="router.push('/mentorships/assign')">
          <h3>Assign Mentor</h3>
          <p>Assign a mentor to a project</p>
        </div>

        <div v-if="isAdmin" class="nav-card" @click="router.push('/milestones/create')">
          <h3>Create Milestone</h3>
          <p>Add a milestone to a project</p>
        </div>

        <div v-if="isAdmin" class="nav-card" @click="router.push('/organizations')">
          <h3>All Organizations</h3>
          <p>View all registered organizations</p>
        </div>

        <!-- STUDENT -->
        <div v-if="isStudent" class="nav-card" @click="router.push('/milestones')">
          <h3>My Milestones</h3>
          <p>Track your project progress</p>
        </div>

      </div>

      <button class="logout-btn" @click="handleLogout">Log out</button>
    </div>
  </div>

</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useOrgStore } from '@/stores/org'

const router = useRouter()
const auth = useAuthStore()
const orgStore = useOrgStore()

const user = computed(() => auth.user)
const myOrgs = ref([])

const isMentor  = computed(() => user.value?.role === 'MENTOR')
const isFirm    = computed(() => user.value?.role === 'FIRM')
const isAdmin   = computed(() => user.value?.role === 'ADMIN')
const isStudent = computed(() => user.value?.role === 'STUDENT')

onMounted(async () => {
  try {
    const result = await orgStore.getMyOrganizations()
    console.log('my orgs:', result)
    myOrgs.value = result
  } catch (e) {
    console.error('org fetch error:', e)
  }
})

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.role-badge {
  display: inline-block;
  background: #1a7a6e;
  color: white;
  padding: 2px 12px;
  border-radius: 12px;
  font-size: 13px;
  margin-bottom: 24px;
}

.nav-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 32px;
}

.nav-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 20px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.nav-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.nav-card h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #1a7a6e;
}

.nav-card p {
  margin: 0;
  font-size: 13px;
  color: #666;
}

.logout-btn {
  background: none;
  border: 1px solid #ccc;
  padding: 8px 20px;
  border-radius: 6px;
  cursor: pointer;
  color: #666;
}
</style>
