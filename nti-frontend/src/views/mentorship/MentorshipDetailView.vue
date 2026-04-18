<template>
  <div class="wrap">
    <div class="box">

      <button class="back-btn" @click="router.push('/mentorships')">
        ← Back to Dashboard
      </button>

      <div v-if="loadingMentorship">Loading...</div>

      <div v-else-if="error" class="error">{{ error }}</div>

      <template v-else-if="mentorship">
        <div class="mentorship-header">
          <h1>Mentorship Detail</h1>
          <span class="status-badge">{{ mentorship.status }}</span>
        </div>

        <div class="meta">
          <p><strong>Mentor:</strong> {{ mentorship.mentorName }}</p>
          <p><strong>Started:</strong> {{ formatDate(mentorship.startDate) }}</p>
          <p v-if="mentorship.endDate">
            <strong>Ended:</strong> {{ formatDate(mentorship.endDate) }}
          </p>
          <p>
            <strong>Application:</strong>
            {{ mentorship.applicationId ?? 'Not linked yet' }}
          </p>
        </div>

        <!-- Note form — only show if mentorship is ACTIVE -->
        <div v-if="mentorship.status === 'ACTIVE'" class="note-form">
          <h2>Add Consultation Note</h2>

          <div v-if="noteError" class="error">{{ noteError }}</div>

          <textarea
            v-model="noteContent"
            placeholder="Write your consultation note here..."
            rows="5"
          />

          <button
            @click="handleAddNote"
            :disabled="submitting || !noteContent.trim()"
          >
            {{ submitting ? 'Saving...' : 'Add Note' }}
          </button>
        </div>

        <div v-else class="closed-notice">
          This mentorship is {{ mentorship.status }} — notes cannot be added.
        </div>

        <!-- Notes timeline -->
        <div class="notes-section">
          <h2>Consultation Notes</h2>

          <div v-if="loadingNotes">Loading notes...</div>

          <div v-else-if="notes.length === 0">
            <p>No notes yet.</p>
          </div>

          <div v-else>
            <div
              v-for="note in notes"
              :key="note.id"
              class="note-card"
            >
              <div class="note-header">
                <span class="note-author">{{ note.createdByName }}</span>
                <span class="note-date">{{ formatDateTime(note.createdAt) }}</span>
              </div>
              <p class="note-content">{{ note.content }}</p>
            </div>

            <!-- Pagination -->
            <div v-if="totalPages > 1" class="pagination">
              <button
                :disabled="currentPage === 0"
                @click="changePage(currentPage - 1)"
              >
                ← Prev
              </button>

              <span>Page {{ currentPage + 1 }} of {{ totalPages }}</span>

              <button
                :disabled="currentPage >= totalPages - 1"
                @click="changePage(currentPage + 1)"
              >
                Next →
              </button>
            </div>
          </div>
        </div>

<div class="milestones-section">
  <h2>Milestones</h2>

  <div v-if="loadingMilestones">Loading milestones...</div>

  <div v-else-if="milestones.length === 0">
    <p>No milestones linked to this mentorship yet.</p>
  </div>

  <div v-else>
    <div
      v-for="m in milestones"
      :key="m.id"
      class="milestone-card"
    >
      <div class="milestone-header">
        <span class="milestone-title">{{ m.title }}</span>
        <span :class="['milestone-status', statusClass(m.status)]">
          {{ m.status }}
        </span>
      </div>

      <p v-if="m.description" class="milestone-desc">{{ m.description }}</p>

      <div class="milestone-meta">
        <span>Due: {{ m.dueDate }}</span>
        <span v-if="m.completedAt">Completed: {{ formatDate(m.completedAt) }}</span>
      </div>

      <!-- Status change — only for MENTOR and ADMIN -->
      <div v-if="canChangeStatus" class="status-change">
        <select v-model="statusSelections[m.id]">
          <option disabled value="">Change status...</option>
          <option
            v-for="s in allowedTransitions(m.status)"
            :key="s"
            :value="s"
          >{{ s }}</option>
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

      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMentorshipStore } from '@/stores/mentorship'

import { useMilestoneStore } from '@/stores/milestone'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const mentorshipStore = useMentorshipStore()

const mentorshipId = route.params.id

const mentorship = ref(null)
const notes = ref([])
const currentPage = ref(0)
const totalPages = ref(0)

const loadingMentorship = ref(false)
const loadingNotes = ref(false)
const submitting = ref(false)
const error = ref('')
const noteError = ref('')
const noteContent = ref('')

const milestoneStore = useMilestoneStore()
const authStore = useAuthStore()
const milestones = ref([])
const loadingMilestones = ref(false)
const statusSelections = ref({})
const canChangeStatus = computed(() =>
  authStore.user?.role === 'MENTOR' || authStore.user?.role === 'ADMIN'
)

onMounted(async () => {
  await loadMentorship()
  await loadNotes()
  await loadMilestones()
})

async function loadMentorship() {
  loadingMentorship.value = true
  try {
    mentorship.value = await mentorshipStore.getMentorship(mentorshipId)
  } catch (e) {
    error.value = 'Mentorship not found.'
  } finally {
    loadingMentorship.value = false
  }
}

async function loadNotes(page = 0) {
  loadingNotes.value = true
  try {
    const data = await mentorshipStore.getNotes(mentorshipId, page)
    notes.value = data.content        // Page<> wraps results in "content" array
    totalPages.value = data.totalPages
    currentPage.value = data.number   // Spring returns current page as "number"
  } catch (e) {
    // notes just won't show — non-critical
  } finally {
    loadingNotes.value = false
  }
}

async function handleAddNote() {
  noteError.value = ''
  submitting.value = true
  try {
    const newNote = await mentorshipStore.addNote(mentorshipId, noteContent.value)
    // Prepend to list so newest appears at top immediately
    // without re-fetching the whole page
    notes.value.unshift(newNote)
    noteContent.value = ''
  } catch (e) {
    noteError.value =
      e.response?.data?.error ||
      e.response?.data ||
      'Failed to add note.'
  } finally {
    submitting.value = false
  }
}

function changePage(page) {
  loadNotes(page)
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString()
}

function formatDateTime(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString()
}

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


async function loadMilestones() {
  loadingMilestones.value = true
  try {
    milestones.value = await milestoneStore.getMilestones({
      mentorshipId: mentorshipId
    })
  } catch (e) {
    // error
  } finally {
    loadingMilestones.value = false
  }
}

async function handleStatusChange(milestoneId) {
  const newStatus = statusSelections.value[milestoneId]
  if (!newStatus) return
  try {
    const updated = await milestoneStore.changeStatus(milestoneId, newStatus)
    // replace the milestone in the list with the updated one
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

.mentorship-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.status-badge {
  background: #1a7a6e;
  color: white;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 13px;
}

.meta {
  background: #f9fafb;
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 24px;
}

.meta p {
  margin: 4px 0;
  font-size: 14px;
}

.note-form {
  margin-bottom: 32px;
}

.note-form h2 {
  margin-bottom: 12px;
}

.note-form textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  resize: vertical;
  font-size: 14px;
  margin-bottom: 8px;
  box-sizing: border-box;
}

.note-form button {
  margin-top: 4px;
}

.closed-notice {
  background: #fff3cd;
  border: 1px solid #ffc107;
  border-radius: 6px;
  padding: 10px 16px;
  margin-bottom: 24px;
  font-size: 14px;
}

.notes-section h2 {
  margin-bottom: 16px;
}

.note-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 12px;
}

.note-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.note-author {
  font-weight: bold;
  font-size: 14px;
}

.note-date {
  color: #999;
  font-size: 12px;
}

.note-content {
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 16px;
  justify-content: center;
  margin-top: 16px;
  font-size: 14px;
}

.milestones-section {
  margin-top: 32px;
}

.milestones-section h2 {
  margin-bottom: 16px;
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

.milestone-status {
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
