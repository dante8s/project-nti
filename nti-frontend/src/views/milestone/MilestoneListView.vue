<template>
  <div class="wrap">
    <div class="box">
      <button class="back-btn" @click="router.push('/dashboard')">
        ← Back to Dashboard
      </button>

      <h1>Milestones</h1>

      <div v-if="loading">Loading...</div>
      <div v-else-if="error" class="error">{{ error }}</div>

      <div v-else-if="milestones.length === 0">
        <p>No milestones found.</p>
      </div>

      <div v-else>
        <div
          v-for="m in milestones"
          :key="m.id"
          class="milestone-card"
        >
          <div class="milestone-header">
            <span class="milestone-title">{{ m.title }}</span>
            <span :class="['status-badge', statusClass(m.status)]">
              {{ m.status }}
            </span>
          </div>

          <p v-if="m.description" class="milestone-desc">
            {{ m.description }}
          </p>

          <div class="milestone-meta">
            <span>Due: {{ m.dueDate }}</span>
            <span v-if="m.completedAt">
              Completed: {{ formatDate(m.completedAt) }}
            </span>
            <span v-if="m.mentorshipId">
              Mentorship:
              <router-link :to="`/mentorships/${m.mentorshipId}`">
                view
              </router-link>
            </span>
          </div>

          <!-- Status change for MENTOR and ADMIN -->
          <div v-if="canChangeStatus" class="status-change">
            <select v-model="statusSelections[m.id]">
              <option disabled value="">Change status...</option>
              <option
                v-for="s in allowedTransitions(m.status)"
                :key="s"
                :value="s"
              >
                {{ s }}
              </option>
            </select>
            <button
              :disabled="!statusSelections[m.id]"
              @click="handleStatusChange(m.id)"
            >
              Apply
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMilestoneStore } from '@/stores/milestone'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const milestoneStore = useMilestoneStore()
const authStore = useAuthStore()

const milestones = ref([])
const loading = ref(false)
const error = ref('')
const statusSelections = ref({})

const user = computed(() => authStore.user)
const canChangeStatus = computed(() =>
  user.value?.role === 'MENTOR' || user.value?.role === 'ADMIN'
)

const TRANSITIONS = {
  PLANNED:     ['IN_PROGRESS', 'BLOCKED'],
  IN_PROGRESS: ['COMPLETED', 'BLOCKED', 'PLANNED'],
  BLOCKED:     ['IN_PROGRESS', 'PLANNED'],
  OVERDUE:     ['IN_PROGRESS', 'COMPLETED'],
  COMPLETED:   []
}

function allowedTransitions(status) {
  return TRANSITIONS[status] || []
}

function statusClass(status) {
  return {
    PLANNED:     'status-planned',
    IN_PROGRESS: 'status-progress',
    COMPLETED:   'status-completed',
    BLOCKED:     'status-blocked',
    OVERDUE:     'status-overdue',
  }[status] || ''
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString()
}

onMounted(async () => {
  loading.value = true
  try {

    const params = {}
    if (user.value?.role === 'STUDENT') {
      //
    }
    milestones.value = await milestoneStore.getMilestones(params)
  } catch (e) {
    error.value = 'Failed to load milestones.'
  } finally {
    loading.value = false
  }
})

async function handleStatusChange(milestoneId) {
  const newStatus = statusSelections.value[milestoneId]
  if (!newStatus) return
  try {
    const updated = await milestoneStore.changeStatus(milestoneId, newStatus)
    const idx = milestones.value.findIndex(m => m.id === milestoneId)
    if (idx !== -1) milestones.value[idx] = updated
    statusSelections.value[milestoneId] = ''
  } catch (e) {
    alert(e.response?.data?.error || 'Failed to change status.')
  }
}
</script>

<style scoped>
.back-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #1a7a6e;
  font-size: 14px;
  margin-bottom: 16px;
  padding: 0;
}

.milestone-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 12px;
}

.milestone-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.milestone-title {
  font-weight: bold;
  font-size: 15px;
}

.milestone-desc {
  font-size: 13px;
  color: #555;
  margin: 4px 0 8px;
}

.milestone-meta {
  font-size: 12px;
  color: #999;
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.milestone-meta a {
  color: #1a7a6e;
}

.status-badge {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 12px;
  font-weight: 600;
}

.status-planned     { background: #e5e7eb; color: #374151; }
.status-progress    { background: #dbeafe; color: #1d4ed8; }
.status-completed   { background: #d1fae5; color: #065f46; }
.status-blocked     { background: #fee2e2; color: #991b1b; }
.status-overdue     { background: #fef3c7; color: #92400e; }

.status-change {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.status-change select {
  padding: 4px 8px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 13px;
}
</style>
