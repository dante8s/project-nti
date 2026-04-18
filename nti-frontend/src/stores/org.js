import { defineStore } from 'pinia'
import api from '@/api/axios'

export const useOrgStore = defineStore('org', () => {

  async function createOrganization(data) {
    const response = await api.post('/api/organizations', data)
    return response.data
  }

  async function getOrganization(id) {
    const response = await api.get(`/api/organizations/${id}`)
    return response.data
  }

  async function getAllOrganizations() {
    const response = await api.get('/api/organizations')
    return response.data
  }

  //
  async function getMyOrganizations() {
    const response = await api.get('/api/organizations/my')
    return response.data
  }

  async function updateOrganization(id, data) {
  const response = await api.put(`/api/organizations/${id}`, data)
  return response.data
}

  async function addMember(orgId, email) {
    const response = await api.post(`/api/organizations/${orgId}/members`, {
      email,
      role: 'MEMBER'
    })
    return response.data
  }

  async function removeMember(orgId, memberId) {
    await api.delete(`/api/organizations/${orgId}/members/${memberId}`)
  }

  async function transferOwnership(orgId, memberId) {
  const response = await api.patch(
    `/api/organizations/${orgId}/transfer-ownership/${memberId}`
  )
  return response.data
}


  return {
    createOrganization,
    getOrganization,
    getAllOrganizations,
    getMyOrganizations,
    updateOrganization,
    transferOwnership,
    addMember,
    removeMember
  }
})
