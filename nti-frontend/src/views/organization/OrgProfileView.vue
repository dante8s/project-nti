<template>
  <div class="wrap">
    <div class="box">
      <button class="back-btn" @click="router.push('/dashboard')">
        ← Back to Dashboard
      </button>

      <div v-if="loading">Loading...</div>
      <div v-else-if="error" class="error">{{ error }}</div>

      <template v-else-if="org">
        <div class="org-header">
          <h1>{{ org.name }}</h1>
          <span :class="['status-badge', org.status.toLowerCase()]">
            {{ org.status }}
          </span>
        </div>

        <div class="meta">
          <p><strong>ICO:</strong> {{ org.ico }}</p>
          <p v-if="org.sector"><strong>Sector:</strong> {{ org.sector }}</p>
          <p v-if="org.description"><strong>Description:</strong> {{ org.description }}</p>
          <p v-if="org.contactEmail"><strong>Email:</strong> {{ org.contactEmail }}</p>
          <p v-if="org.contactPhone"><strong>Phone:</strong> {{ org.contactPhone }}</p>
          <p v-if="org.website"><strong>Website:</strong> {{ org.website }}</p>
        </div>

        <div v-if="isOwner || isAdmin" class="edit-bar">
          <button @click="router.push(`/organizations/${route.params.id}/edit`)">
            Edit Organization
          </button>
        </div>

        <!-- Members section -->
        <div class="members-section">
          <h2>Members</h2>

          <div v-if="org.members && org.members.length > 0">
            <div
              v-for="m in org.members"
              :key="m.id"
              class="member-row"
            >
              <div class="member-info">
                <span class="member-name">{{ m.userName }}</span>
                <span class="member-email">{{ m.userEmail }}</span>
              </div>
              <div class="member-actions">
                <span :class="['role-badge', m.role.toLowerCase()]">{{ m.role }}</span>
                <button
                  v-if="isOwner && m.role !== 'OWNER'"
                  class="remove-btn"
                  @click="handleRemoveMember(m.id)"
                >
                  Remove
                </button>

                <button
                  v-if="isAdmin && m.role !== 'OWNER'"
                  class="transfer-btn"
                  @click="handleTransferOwnership(m.id)"
                >
                  Make Owner
                </button>
              </div>
            </div>
          </div>

          <p v-else>No members yet.</p>
          <div v-if="transferError" class="error">{{ transferError }}</div>
          <div v-if="transferSuccess" class="success">Ownership transferred successfully.</div>

          <!-- Add member form — OWNER only -->
          <div v-if="isOwner" class="add-member-form">
            <h3>Add Member</h3>
            <div v-if="memberError" class="error">{{ memberError }}</div>
            <div class="add-member-inputs">
              <input
                v-model="newMemberEmail"
                type="email"
                placeholder="member@email.com"
              />
              <button
                @click="handleAddMember"
                :disabled="addingMember || !newMemberEmail.trim()"
              >
                {{ addingMember ? 'Adding...' : 'Add Member' }}
              </button>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrgStore } from '@/stores/org'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const orgStore = useOrgStore()
const authStore = useAuthStore()

const transferError = ref('')
const transferSuccess = ref(false)

const org = ref(null)
const loading = ref(false)
const error = ref('')
const newMemberEmail = ref('')
const addingMember = ref(false)
const memberError = ref('')

const isOwner = computed(() =>
  org.value?.members?.some(
    m => m.userEmail === authStore.user?.email && m.role === 'OWNER'
  )
)
const isAdmin = computed(() =>
authStore.user?.role === 'ADMIN'
)

onMounted(async () => {
  loading.value = true
  try {
    org.value = await orgStore.getOrganization(route.params.id)
  } catch (e) {
    error.value = 'Organization not found.'
  } finally {
    loading.value = false
  }
})

async function handleAddMember() {
  memberError.value = ''
  addingMember.value = true
  try {
    const added = await orgStore.addMember(route.params.id, newMemberEmail.value)
    org.value.members.push(added)
    newMemberEmail.value = ''
  } catch (e) {
    memberError.value =
      e.response?.data?.error ||
      e.response?.data ||
      'Failed to add member.'
  } finally {
    addingMember.value = false
  }
}

async function handleRemoveMember(memberId) {
  try {
    await orgStore.removeMember(route.params.id, memberId)
    org.value.members = org.value.members.filter(m => m.id !== memberId)
  } catch (e) {
    alert(e.response?.data?.error || 'Failed to remove member.')
  }
}

async function handleTransferOwnership(memberId) {
  transferError.value = ''
  transferSuccess.value = false
  if (!confirm('Are you sure you want to transfer ownership to this member?')) return

  try {
    await orgStore.transferOwnership(route.params.id, memberId)
    transferSuccess.value = true
    // Reload org
    org.value = await orgStore.getOrganization(route.params.id)
    // clear success after 3 seconds
    setTimeout(() => transferSuccess.value = false, 3000)
  } catch (e) {
    transferError.value =
      e.response?.data?.error ||
      e.response?.data ||
      'Failed to transfer ownership.'
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

.org-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.status-badge {
  padding: 2px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}

.status-badge.pending  { background: #fef3c7; color: #92400e; }
.status-badge.active   { background: #d1fae5; color: #065f46; }
.status-badge.suspended { background: #fee2e2; color: #991b1b; }

.meta {
  background: #f9fafb;
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 24px;
}

.meta p { margin: 4px 0; font-size: 14px; }

.members-section h2 { margin-bottom: 16px; }

.member-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  margin-bottom: 8px;
}

.member-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.member-name { font-weight: bold; font-size: 14px; }
.member-email { font-size: 12px; color: #666; }

.member-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.role-badge {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 12px;
}

.role-badge.owner  { background: #1a7a6e; color: white; }
.role-badge.member { background: #e5e7eb; color: #374151; }

.remove-btn {
  background: none;
  border: 1px solid #fca5a5;
  color: #dc2626;
  padding: 3px 10px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
}

.add-member-form {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.add-member-form h3 { margin-bottom: 10px; font-size: 15px; }

.add-member-inputs {
  display: flex;
  gap: 8px;
}

.add-member-inputs input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
}
.transfer-btn {
  background: none;
  border: 1px solid #93c5fd;
  color: #1d4ed8;
  padding: 3px 10px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
}

.success {
  background: #d1fae5;
  color: #065f46;
  padding: 10px 16px;
  border-radius: 6px;
  margin-bottom: 12px;
  font-size: 14px;
}
</style>
