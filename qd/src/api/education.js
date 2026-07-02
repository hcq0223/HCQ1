import request from './request'

export function selectEducationByResumeId(resumeId) {
  return request.get('/hcq/Education/selectEducationByResumeId', { params: { resumeId } })
}

export function insertEducation(education, resumeId) {
  return request.post('/hcq/Education/insertEducation', education, { params: { resumeId } })
}

export function updateEducation(education) {
  return request.put('/hcq/Education/updateEducation', education)
}

export function deleteEducation(id, resumeId) {
  return request.delete('/hcq/Education/deleteEducation', { params: { id, resumeId } })
}

export function educationBatch(resumeId, ids) {
  return request.delete(`/hcq/Education/${resumeId}/educationBatch`, { data: { ids } })
}
