<template>
  <div class="wrap">
    <div class="box">
      <h1>Mentor Dashboard</h1>

      <div v-if="loading">Loading...</div>

      <div v-else-if="error" class="error">{{ error }}</div>

      <div v-else-if="mentorships.length === 0">
        <p>You have no active mentorships.</p>
      </div>

      <div v-else>
        <div
          v-for="m in mentorships"
          :key="m.id"
          class="mentorship-card"
          @click="goToDetail(m.id)"
        >
          <div class="mentorship-header">
            <span class="status-badge">{{ m.status }}</span>
            <span class="date">Since: {{ formatDate(m.startDate) }}</span>
          </div>

          <p v-if="m.applicationId">
            Application ID: {{ m.applicationId }}
          </p>
          <p v-else>
            No application linked yet
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMentorshipStore } from '@/stores/mentorship'

const router = useRouter()
const mentorshipStore = useMentorshipStore()

const mentorships = ref([])
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    mentorships.value = await mentorshipStore.getMyMentorships()
  } catch (e) {
    error.value = 'Failed to load mentorships.'
  } finally {
    loading.value = false
  }
})

function goToDetail(id) {
  router.push(`/mentorships/${id}`)
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.mentorship-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
}

.mentorship-card:hover {
  background-color: #f5f5f5;
}

.mentorship-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.status-badge {
  background: #1a7a6e;
  color: white;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 13px;
}

.date {
  color: #666;
  font-size: 13px;
}
</style>
