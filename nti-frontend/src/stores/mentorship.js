import { defineStore } from 'pinia'
import api from '@/api/axios'

export const useMentorshipStore = defineStore('mentorship', () => {

  async function getMyMentorships() {
    const response = await api.get('/api/mentorships/my')
    return response.data
  }

  async function getMentorship(id) {
    const response = await api.get(`/api/mentorships/${id}`)
    return response.data
  }

  async function getNotes(mentorshipId, page = 0, size = 10) {
    const response = await api.get(
      `/api/mentorships/${mentorshipId}/notes?page=${page}&size=${size}`
    )
    return response.data
  }

  async function addNote(mentorshipId, content) {
    const response = await api.post(
      `/api/mentorships/${mentorshipId}/notes`,
      { content }
    )
    return response.data
  }

  async function assignMentor(mentorUserId, applicationId = null) {
  const response = await api.post('/api/mentorships', {
    mentorUserId: Number(mentorUserId),
    applicationId: applicationId || null
  })
  return response.data
}

  return { getMyMentorships, getMentorship, getNotes, addNote, assignMentor }
})
