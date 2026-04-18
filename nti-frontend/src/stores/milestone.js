import { defineStore } from 'pinia'
import api from '@/api/axios'

export const useMilestoneStore = defineStore('milestone', () => {

  async function getMilestones({ applicationId, mentorshipId, createdById } = {}) {
    const params = {}
    if (applicationId) params.applicationId = applicationId
    if (mentorshipId) params.mentorshipId = mentorshipId
    if (createdById) params.createdById = createdById
    const response = await api.get('/api/milestones', { params })
    return response.data
  }

  async function getMilestone(id) {
    const response = await api.get(`/api/milestones/${id}`)
    return response.data
  }

  async function createMilestone(data) {
    const response = await api.post('/api/milestones', data)
    return response.data
  }

  async function updateMilestone(id, data) {
    const response = await api.put(`/api/milestones/${id}`, data)
    return response.data
  }

  async function changeStatus(id, status) {
    const response = await api.patch(`/api/milestones/${id}/status`, { status })
    return response.data
  }

  return { getMilestones, getMilestone, createMilestone, updateMilestone, changeStatus }
})
