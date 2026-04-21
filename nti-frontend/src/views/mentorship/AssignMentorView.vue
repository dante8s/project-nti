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
<style scoped>
/* Standard layout wrappers */
.wrap {
  display: flex;
  justify-content: center;
  padding: 40px 20px;
}

.box {
  background: #ffffff;
  max-width: 500px;
  width: 100%;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
}

h1 {
  margin-top: 0;
  margin-bottom: 24px;
  font-size: 24px;
  color: #111827;
}

/* Back Button */
.back-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #1a7a6e;
  font-size: 14px;
  margin-bottom: 16px;
  padding: 0;
  font-weight: 500;
}

.back-btn:hover {
  text-decoration: underline;
}

/* Form Fields */
.field {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.field input {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
  width: 100%;
  box-sizing: border-box;
  font-family: inherit;
}

.field input:focus {
  outline: none;
  border-color: #1a7a6e;
  box-shadow: 0 0 0 2px rgba(26, 122, 110, 0.2);
}

/* Alert Messages */
.error {
  background: #fee2e2;
  color: #991b1b;
  padding: 10px 16px;
  border-radius: 6px;
  margin-bottom: 16px;
  font-size: 14px;
}

.success {
  background: #d1fae5;
  color: #065f46;
  padding: 10px 16px;
  border-radius: 6px;
  margin-bottom: 16px;
  font-size: 14px;
}

/* Submit Button */
button[type="submit"] {
  background: #1a7a6e;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  width: 100%;
  margin-top: 8px;
  transition: background-color 0.2s;
}

button[type="submit"]:hover:not(:disabled) {
  background: #135c52;
}

button[type="submit"]:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  opacity: 0.7;
}
</style>
