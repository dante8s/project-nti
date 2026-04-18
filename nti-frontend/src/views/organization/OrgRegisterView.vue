<template>
  <div class="wrap">
    <div class="box">
      <h1>Реєстрація організації</h1>

      <div v-if="error" class="error">{{ error }}</div>

      <form @submit.prevent="handleSubmit">
        <div class="field">
          <label>Назва організації *</label>
          <input
            v-model="form.name"
            type="text"
            placeholder="TechFirma s.r.o."
            required
          />
        </div>

        <div class="field">
          <label>ІКО (реєстраційний номер) *</label>
          <input
            v-model="form.ico"
            type="text"
            placeholder="12345678"
            required
            maxlength="20"
          />
        </div>

        <div class="field">
          <label>Сектор</label>
          <input
            v-model="form.sector"
            type="text"
            placeholder="IT, Фінанси, Освіта..."
          />
        </div>

        <div class="field">
          <label>Опис</label>
          <textarea
            v-model="form.description"
            placeholder="Коротко про вашу організацію"
            rows="4"
          />
        </div>

        <div class="field">
          <label>Контактний email</label>
          <input
            v-model="form.contactEmail"
            type="email"
            placeholder="info@company.sk"
          />
        </div>

        <div class="field">
          <label>Контактний телефон</label>
          <input
            v-model="form.contactPhone"
            type="text"
            placeholder="+421900000000"
          />
        </div>

        <div class="field">
          <label>Вебсайт</label>
          <input
            v-model="form.website"
            type="text"
            placeholder="https://company.sk"
          />
        </div>

        <button type="submit" :disabled="loading">
          {{ loading ? 'Реєстрація...' : 'Зареєструвати організацію' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useOrgStore } from '@/stores/org'

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

const loading = ref(false)
const error = ref('')

async function handleSubmit() {
  error.value = ''
  loading.value = true
  try {
    const org = await orgStore.createOrganization(form)
    // Redirect to the org profile using the returned id
    router.push(`/organizations/${org.id}`)
  } catch (e) {
    error.value =
      e.response?.data?.error ||
      e.response?.data ||
      'Помилка реєстрації організації'
  } finally {
    loading.value = false
  }
}
</script>
