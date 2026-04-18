<template>
  <div class="wrap">
    <div class="box">
      <button class="back-btn" @click="router.back()">
        ← Back
      </button>

      <h1>Edit Organization</h1>

      <div v-if="loadingOrg">Loading...</div>
      <div v-else-if="loadError" class="error">{{ loadError }}</div>

      <template v-else>
        <div v-if="error" class="error">{{ error }}</div>
        <div v-if="success" class="success">Organization updated successfully.</div>

        <form @submit.prevent="handleSubmit">
          <div class="field">
            <label>Name *</label>
            <input v-model="form.name" type="text" required />
          </div>

          <div class="field">
            <label>ICO (registration number) *</label>
            <input v-model="form.ico" type="text" required maxlength="20" />
          </div>

          <div class="field">
            <label>Sector</label>
            <input v-model="form.sector" type="text" />
          </div>

          <div class="field">
            <label>Description</label>
            <textarea v-model="form.description" rows="4" />
          </div>

          <div class="field">
            <label>Contact Email</label>
            <input v-model="form.contactEmail" type="email" />
          </div>

          <div class="field">
            <label>Contact Phone</label>
            <input v-model="form.contactPhone" type="text" />
          </div>

          <div class="field">
            <label>Website</label>
            <input v-model="form.website" type="text" />
          </div>

          <div class="button-row">
            <button type="submit" :disabled="loading">
              {{ loading ? 'Saving...' : 'Save Changes' }}
            </button>
            <button type="button" class="cancel-btn" @click="router.back()">
              Cancel
            </button>
          </div>
        </form>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrgStore } from '@/stores/org'

const route = useRoute()
const router = useRouter()
const orgStore = useOrgStore()

const form = reactive({
  name: '',
  ico: '',
  sector: '',
  description: '',
  contactEmail: '',
  contactPhone: '',
  website: ''
})

const loadingOrg = ref(false)
const loading = ref(false)
const loadError = ref('')
const error = ref('')
const success = ref(false)

// Load existing org data into the form on mount
onMounted(async () => {
  loadingOrg.value = true
  try {
    const org = await orgStore.getOrganization(route.params.id)
    form.name         = org.name         || ''
    form.ico          = org.ico          || ''
    form.sector       = org.sector       || ''
    form.description  = org.description  || ''
    form.contactEmail = org.contactEmail || ''
    form.contactPhone = org.contactPhone || ''
    form.website      = org.website      || ''
  } catch (e) {
    loadError.value = 'Failed to load organization data.'
  } finally {
    loadingOrg.value = false
  }
})

async function handleSubmit() {
  error.value = ''
  success.value = false
  loading.value = true
  try {
    await orgStore.updateOrganization(route.params.id, form)
    success.value = true

    setTimeout(() => router.push(`/organizations/${route.params.id}`), 1000)
  } catch (e) {
    error.value =
      e.response?.data?.error ||
      e.response?.data ||
      'Failed to update organization.'
  } finally {
    loading.value = false
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
}

.field textarea {
  resize: vertical;
}

.button-row {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.cancel-btn {
  background: none;
  border: 1px solid #ccc;
  color: #666;
  padding: 8px 20px;
  border-radius: 6px;
  cursor: pointer;
}

.success {
  background: #d1fae5;
  color: #065f46;
  padding: 10px 16px;
  border-radius: 6px;
  margin-bottom: 16px;
  font-size: 14px;
}

.edit-bar {
  margin-bottom: 16px;
}
</style>
