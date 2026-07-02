import request from './request'

export function selectSkillsByResumeId(resumeId) {
  return request.get('/hcq/Skill/selectSkillsByResumeId', { params: { resumeId } })
}

export function selectSkillsByCategory(resumeId, category) {
  return request.get('/hcq/Skill/selectSkillsByCategory', { params: { resumeId, category } })
}

export function getCategories(resumeId) {
  return request.get('/hcq/Skill/getCategories', { params: { resumeId } })
}

export function insertSkill(skill, resumeId) {
  return request.post('/hcq/Skill/insertSkill', skill, { params: { resumeId } })
}

export function updateSkill(skill) {
  return request.put('/hcq/Skill/updateSkill', skill)
}

export function deleteSkill(id, resumeId) {
  return request.delete('/hcq/Skill/deleteSkill', { params: { id, resumeId } })
}

export function skillBatch(resumeId, ids) {
  return request.delete(`/hcq/Skill/${resumeId}/skillBatch`, { data: { ids } })
}

export function skillSort(resumeId, sortedIds) {
  return request.put(`/hcq/Skill/${resumeId}/skillSort`, sortedIds)
}
