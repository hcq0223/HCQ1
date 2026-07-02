import request from './request'

export function selectProjectsByResumeId(resumeId) {
  return request.get('/hcq/ProjectExperience/selectProjectsByResumeId', { params: { resumeId } })
}

export function insertProject(project, resumeId) {
  return request.post('/hcq/ProjectExperience/insertProject', project, { params: { resumeId } })
}

export function updateProject(project) {
  return request.put('/hcq/ProjectExperience/updateProject', project)
}

export function deleteProject(id, resumeId) {
  return request.delete('/hcq/ProjectExperience/deleteProject', { params: { id, resumeId } })
}

export function projectBatch(resumeId, ids) {
  return request.delete(`/hcq/ProjectExperience/${resumeId}/projectBatch`, { data: { ids } })
}

export function projectSort(resumeId, sortedIds) {
  return request.put(`/hcq/ProjectExperience/${resumeId}/projectSort`, sortedIds)
}
