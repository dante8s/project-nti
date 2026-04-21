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

<style scoped>
/* Standard layout wrappers (if not defined globally) */
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

/* Reference Styles */
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

.field input,
.field textarea {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
  width: 100%;
  box-sizing: border-box;
  font-family: inherit;
}

.field input:focus,
.field textarea:focus {
  outline: none;
  border-color: #1a7a6e;
  box-shadow: 0 0 0 2px rgba(26, 122, 110, 0.2);
}

.field textarea {
  resize: vertical;
}

/* New: Error message styling (matches your .success style but red) */
.error {
  background: #fee2e2;
  color: #991b1b;
  padding: 10px 16px;
  border-radius: 6px;
  margin-bottom: 16px;
  font-size: 14px;
}

/* New: Submit button styling */
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
