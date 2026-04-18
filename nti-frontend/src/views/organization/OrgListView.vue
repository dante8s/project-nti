<template>
  <div class="wrap">
    <div class="box">
      <button class="back-btn" @click="router.push('/dashboard')">← Back</button>
      <h1>All Organizations</h1>

      <div v-if="loading">Loading...</div>
      <div v-else-if="orgs.length === 0"><p>No organizations yet.</p></div>

      <div v-else>
        <div
          v-for="o in orgs"
          :key="o.id"
          class="org-row"
          @click="router.push(`/organizations/${o.id}`)"
        >
          <div>
            <strong>{{ o.name }}</strong>
            <span class="sector">{{ o.sector }}</span>
          </div>
          <span :class="['status-badge', o.status.toLowerCase()]">{{ o.status }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useOrgStore } from '@/stores/org'

const router = useRouter()
const orgStore = useOrgStore()
const orgs = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try { orgs.value = await orgStore.getAllOrganizations() }
  finally { loading.value = false }
})
</script>

<style scoped>
.org-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
}
.org-row:hover { background: #f9fafb; }
.sector { font-size: 12px; color: #666; margin-left: 10px; }
.status-badge { font-size: 12px; padding: 2px 10px; border-radius: 12px; }
.status-badge.pending   { background: #fef3c7; color: #92400e; }
.status-badge.active    { background: #d1fae5; color: #065f46; }
.status-badge.suspended { background: #fee2e2; color: #991b1b; }
</style>
