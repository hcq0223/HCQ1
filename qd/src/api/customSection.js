import request from './request'

export function selectCustomSectionsByResumeId(resumeId) {
  return request.get('/hcq/CustomSection/selectCustomSectionsByResumeId', { params: { resumeId } })
}

export function insertCustomSection(section, resumeId) {
  return request.post('/hcq/CustomSection/insertCustomSection', section, { params: { resumeId } })
}

export function updateCustomSection(section) {
  return request.put('/hcq/CustomSection/updateCustomSection', section)
}

export function deleteCustomSection(id, resumeId) {
  return request.delete('/hcq/CustomSection/deleteCustomSection', { params: { id, resumeId } })
}

export function customSectionBatch(resumeId, ids) {
  return request.delete(`/hcq/CustomSection/${resumeId}/customSectionBatch`, { data: { ids } })
}

export function customSectionSort(resumeId, sortedIds) {
  return request.put(`/hcq/CustomSection/${resumeId}/customSectionSort`, sortedIds)
}
