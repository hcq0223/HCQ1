import request from './request'

export function selectWorkExperienceByResumeId(resumeId) {
  return request.get('/hcq/WorkExperience/selectWorkExperienceByResumeId', { params: { resumeId } })
}

export function insertWorkExperience(workExperience, resumeId) {
  return request.post('/hcq/WorkExperience/insertWorkExperience', workExperience, { params: { resumeId } })
}

export function updateWorkExperience(workExperience) {
  return request.put('/hcq/WorkExperience/updateWorkExperience', workExperience)
}

export function deleteWorkExperience(id, resumeId) {
  return request.delete('/hcq/WorkExperience/deleteWorkExperience', { params: { id, resumeId } })
}

export function workExperienceBatch(resumeId, ids) {
  return request.delete(`/hcq/WorkExperience/${resumeId}/workExperienceBatch`, { data: { ids } })
}

export function sortExperiences(resumeId, sortedIds) {
  return request.put(`/hcq/WorkExperience/${resumeId}/sortExperiences`, { sortedIds })
}
