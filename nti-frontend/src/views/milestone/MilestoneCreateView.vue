<template>
  <div class="wrap">
    <div class="box">
      <button class="back-btn" @click="router.push('/dashboard')">
        ← Back
      </button>

      <h1>Create Milestone</h1>
      <div v-if="error" class="error">{{ error }}</div>

      <form @submit.prevent="handleSubmit">
        <div class="field">
          <label>Title *</label>
          <input v-model="form.title" type="text" required />
        </div>

        <div class="field">
          <label>Description</label>
          <textarea v-model="form.description" rows="4" />
        </div>

        <div class="field">
          <label>Due Date *</label>
          <input v-model="form.dueDate" type="date" required />
        </div>

        <div class="field">
          <label>Mentorship ID</label>
          <input v-model="form.mentorshipId" type="text" placeholder="UUID" />
        </div>

        <div class="field">
          <label>Application ID</label>
          <input v-model="form.applicationId" type="number" />
        </div>

        <button type="submit" :disabled="loading">
          {{ loading ? 'Creating...' : 'Create Milestone' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useMilestoneStore } from '@/stores/milestone'

const router = useRouter()
const milestoneStore = useMilestoneStore()

const form = reactive({
  title: '',
  description: '',
  dueDate: '',
  mentorshipId: '',
  applicationId: null
})

const loading = ref(false)
const error = ref('')

async function handleSubmit() {
  error.value = ''
  loading.value = true
  try {
    const payload = {
      title: form.title,
      description: form.description || null,
      dueDate: form.dueDate,
      mentorshipId: form.mentorshipId || null,
      applicationId: form.applicationId || null
    }
    await milestoneStore.createMilestone(payload)
    router.push('/dashboard')
  } catch (e) {
    error.value = e.response?.data?.error || 'Failed to create milestone.'
  } finally {
    loading.value = false
  }
}
</script>
