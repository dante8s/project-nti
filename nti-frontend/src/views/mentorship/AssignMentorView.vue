<template>
  <div class="wrap">
    <div class="box">
      <button class="back-btn" @click="router.push('/dashboard')">← Back</button>

      <h1>Assign Mentor</h1>
      <div v-if="error" class="error">{{ error }}</div>
      <div v-if="success" class="success">Mentor assigned successfully!</div>

      <form @submit.prevent="handleSubmit">
        <div class="field">
          <label>Mentor User ID *</label>
          <input v-model="form.mentorUserId" type="number" required />
        </div>

        <div class="field">
          <label>Application ID (optional)</label>
          <input v-model="form.applicationId" type="number" />
        </div>

        <button type="submit" :disabled="loading">
          {{ loading ? 'Assigning...' : 'Assign Mentor' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useMentorshipStore } from '@/stores/mentorship'

const router = useRouter()
const mentorshipStore = useMentorshipStore()

const form = reactive({ mentorUserId: '', applicationId: null })
const loading = ref(false)
const error = ref('')
const success = ref(false)

async function handleSubmit() {
  error.value = ''
  success.value = false
  loading.value = true
  try {
    await mentorshipStore.assignMentor(form.mentorUserId, form.applicationId)
    success.value = true
    form.mentorUserId = ''
    form.applicationId = null
  } catch (e) {
    error.value = e.response?.data?.error || 'Failed to assign mentor.'
  } finally {
    loading.value = false
  }
}
</script>
